#!/bin/bash
################################################################################ 
# Author:       Theodore Cao 
# Date          2015-1-18
# Description:  Start client-online testing app as a background process. 
#
################################################################################ 


function st_help
{
    me=`basename $0`
    cat << EOF
Usage: 
~~~~~~~~~~
Start the service: 
     $me start <user> <passwd> <home-dir>  [buddle-name]
Stop/check the service: 
     $me status|stop <user> 

Examples: 
~~~~~~~~~~
Start the service: 
     $me start gw04 abcd1234  ./fake-dev/__gw04/sdcard/AA-FAMBO
     $me start gw04 abcd1234  ./fake-dev/__gw04/sdcard/AA-FAMBO  st_client_online2.properties
Stop/check the service: 
     $me status|stop gw04

EOF
}


serv_args=""
if [ $# -eq 5 ];then
    serv_args="$2 $3 $4 $5"
elif [ $# -eq 4 ];then
    serv_args="$2 $3 $4"
elif [ $# -eq 2 ]; then
    serv_args=$2
else
    st_help
    exit 1
fi

ck-service.sh  clt-online_$2  -t 3 "cn.teclub.ha.test.ClientOnline"  "$serv_args"  $1  2>&1

