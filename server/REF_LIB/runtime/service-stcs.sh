#!/bin/bash

################################################################################ 
# Author:       Theodore Cao 
# Date          2015-1-18
# Description:  Start Central Server as a service 
#               i.e. a background running procss. 
#
################################################################################ 

if [ $# -ne 1 ];then
    echo "Usage: $0 status|start|stop "
    exit 1
fi

. ./setenv.sh
sleep 1; clear


set -x
./ck-service.sh  stcs  "cn.teclub.ha.net.serv.StSngSrvMain"  "none"  $1  2>&1


