#!/bin/sh
source $([[ $0 =~ "/" ]] && echo ${0%/*}/common.sh || echo common.sh) 
java ${OPTS} lsr.paxos.test.ka47.KaServer $*
