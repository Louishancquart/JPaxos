package lsr.paxos.recovery;

import static lsr.common.ProcessDescriptor.processDescriptor;

import java.io.IOException;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import lsr.common.SingleThreadDispatcher;
import lsr.paxos.ActiveRetransmitter;
import lsr.paxos.RetransmittedMessage;
import lsr.paxos.SnapshotProvider;
import lsr.paxos.core.Paxos;
import lsr.paxos.messages.Message;
import lsr.paxos.messages.MessageType;
import lsr.paxos.messages.Recovery;
import lsr.paxos.messages.RecoveryAnswer;
import lsr.paxos.network.MessageHandler;
import lsr.paxos.network.Network;
import lsr.paxos.storage.SingleNumberWriter;
import lsr.paxos.storage.Storage;
import lsr.paxos.storage.SynchronousViewStorage;

public class ViewSSRecovery extends RecoveryAlgorithm implements Runnable {
    private boolean firstRun;
    private Paxos paxos;
    private final int numReplicas;
    private Storage storage;
    private SingleThreadDispatcher dispatcher;
    private ActiveRetransmitter retransmitter;
    private RetransmittedMessage recoveryRetransmitter;

    public ViewSSRecovery(SnapshotProvider snapshotProvider, String stableStoragePath)
            throws IOException {
        numReplicas = processDescriptor.numReplicas;

        storage = createStorage(new SingleNumberWriter(stableStoragePath, "sync.view"));
        paxos = createPaxos(snapshotProvider, storage);
        dispatcher = paxos.getDispatcher();
    }

    public Paxos getPaxos() {
        return paxos;
    }

    public void start() {
        dispatcher.submit(this);
    }

    public void run() {
        // do not execute recovery mechanism on first run
        if (firstRun) {
            onRecoveryFinished();
            return;
        }

        retransmitter = new ActiveRetransmitter(paxos.getNetwork(), "ViewSSRecoveryRetransmitter");
        retransmitter.init();
        logger.info("Sending recovery message");
        Network.addMessageListener(MessageType.RecoveryAnswer, new RecoveryAnswerListener());
        recoveryRetransmitter = retransmitter.startTransmitting(new Recovery(storage.getView(), -1));
    }

    protected Paxos createPaxos(SnapshotProvider snapshotProvider, Storage storage)
            throws IOException {
        return new Paxos(snapshotProvider, storage);
    }

    private Storage createStorage(SingleNumberWriter writer) {
        Storage storage = new SynchronousViewStorage(writer);
        firstRun = storage.getView() == 0;
        if (processDescriptor.isLocalProcessLeader(storage.getView())) {
            storage.setView(storage.getView() + 1);
        }
        return storage;
    }

    // Get all instances before <code>nextId</code>
    private void startCatchup(final int nextId) {
        new RecoveryCatchUp(paxos.getCatchup(), storage).recover(nextId, new Runnable() {
            public void run() {
                onRecoveryFinished();
            }
        });
    }

    private void onRecoveryFinished() {
        fireRecoveryFinished();
        Network.addMessageListener(MessageType.Recovery, new ViewRecoveryRequestHandler(paxos));
    }

    private class RecoveryAnswerListener implements MessageHandler {
        private BitSet received;
        private RecoveryAnswer answerFromLeader = null;

        public RecoveryAnswerListener() {
            received = new BitSet(numReplicas);
        }

        public void onMessageReceived(Message msg, final int sender) {
            assert msg.getType() == MessageType.RecoveryAnswer;
            final RecoveryAnswer recoveryAnswer = (RecoveryAnswer) msg;

            // drop messages from lower views
            if (recoveryAnswer.getView() < storage.getView()) {
                return;
            }

            if (logger.isLoggable(Level.INFO))
                logger.info("Got a recovery answer " + recoveryAnswer +
                            (processDescriptor.getLeaderOfView(recoveryAnswer.getView()) == sender
                                    ? " from leader" : ""));

            dispatcher.submit(new Runnable() {
                public void run() {
                    recoveryRetransmitter.stop(sender);
                    received.set(sender);

                    // update view
                    if (storage.getView() < recoveryAnswer.getView()) {
                        storage.setView(recoveryAnswer.getView());
                        answerFromLeader = null;
                    }

                    if (processDescriptor.getLeaderOfView(storage.getView()) == sender) {
                        answerFromLeader = recoveryAnswer;
                    }

                    if (received.cardinality() > numReplicas / 2) {
                        onCardinality();
                    }
                }
            });
        }

        private void onCardinality() {
            recoveryRetransmitter.stop();
            recoveryRetransmitter = null;

            if (answerFromLeader == null) {
                Recovery recovery = new Recovery(storage.getView(), -1);
                recoveryRetransmitter = retransmitter.startTransmitting(recovery);
            } else {
                startCatchup((int) answerFromLeader.getNextId());
                Network.removeMessageListener(MessageType.RecoveryAnswer, this);
            }
        }

        public void onMessageSent(Message message, BitSet destinations) {
        }
    }

    private static final Logger logger = Logger.getLogger(ViewSSRecovery.class.getCanonicalName());
}
