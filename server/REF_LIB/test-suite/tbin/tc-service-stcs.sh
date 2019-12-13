#!/bin/bash
# [Theodore: 2016-10-29] Deprecated

################################################################################ 
# Author:       Theodore Cao 
# Date          2015-1-18
# Description:  Start Central Server as a service 
#               i.e. a background running procss. 
#
################################################################################ 

if [ $# -ne 1 ];then
    me=`basename $0`
    echo "Usage: $me status|start|stop "
    exit 1
fi


#ck-service.sh  stcs $1 "cn.teclub.ha.net.serv.StServerSSL"  "none"  1  2>&1
ck-service.sh  -t 1 stcs "cn.teclub.ha.net.serv.StSngSrvMain"  "none"  $1  2>&1


