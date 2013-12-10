package process_handlers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import benchmark.TestLoader;
import benchmark.ProcessListener;

import commands.ClientCommand;
import commands.Command;

public class BenchmarkClientHandler implements ClientHandler {

	ClientCommand lastCommand;

	ProcessListener listener;

	Process process;

	BufferedWriter processOutputStream = null;

	String name = null;

	final int vnrunhost;

	@Override
	public String toString() {
		return name;
	}

	public BenchmarkClientHandler(int vnrunhost, ClientCommand lastCommand, ProcessListener _listener) {
		this(vnrunhost, null, lastCommand, _listener);
	}

	public BenchmarkClientHandler(int vnrunhost, String name, ClientCommand lastCommand, ProcessListener _listener) {
		this.vnrunhost = vnrunhost;
		this.name = name;
		this.lastCommand = lastCommand;
		listener = _listener;

		String cmd = TestLoader.getClientCmd();

		cmd = cmd.replaceAll("MODEL", TestLoader.getModelnetFile());
		cmd = cmd.replaceAll("VNODE", String.valueOf(vnrunhost - 1));

		try {
			process = Runtime.getRuntime().exec(cmd);

			processOutputStream = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(
					process.getOutputStream())));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		final BenchmarkClientHandler me = this;

		new Thread() {
			public void run() {

				BufferedReader out = new BufferedReader(new InputStreamReader(process.getErrorStream()));

				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter("client__" + me.name));
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(1);
				}

				listener.processCreated(me);

				String line;
				try {
					while (true) {
						line = out.readLine();
						if (line == null)
							break;
						bw.write(line + "\n");
						bw.flush();
						if (line.matches("^Finished.*")) {
							listener.clientSent(me);
						}
					}
				} catch (IOException e) {
				}

				try {
					process.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (listener != null) {
					if (process.exitValue() == 0) {
						listener.processFinished(me);
					} else {
						listener.errorCaught(me);
					}
				}
			}
		}.start();

	}

	@Override
	public void sendRequests(int count, long delay, boolean randomDelay) {

		try {
			// String x = String.format("%d %d %s\n", delay, count, randomDelay
			// ? "true" : "false");
			// System.out.println(x);
			processOutputStream.write(delay + " " + count + " " + randomDelay + "\n");
			processOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	@Override
	public void addProcessListener(ProcessListener procesListener) {
		if (listener != null)
			throw new RuntimeException();
		listener = procesListener;

	}

	@Override
	public void kill() {
		process.destroy();
		listener.processFinished(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void stop() {
		try {
			processOutputStream.write("bye\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		listener.processFinished(this);
	}

	public Command getLastCommand() {
		return lastCommand;
	}

	public void setLastCommand(Command c) {
		if (c instanceof ClientCommand)
			lastCommand = (ClientCommand) c;

	}

}
