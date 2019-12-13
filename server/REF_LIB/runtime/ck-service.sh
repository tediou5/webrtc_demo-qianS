#!/bin/bash
# ==============================================================================
#
#       Filename: 	ck-service.sh 
#
#    Description:  	Service execution script
#
#
#        Version:  	1.0
#        Created:  	2014-12-20 07:42:40
#       Revision:  	none
#       Compiler:  	bash
#
#         Author:  	Theodore Cao 
#        Company:  	Spring Tech, Shanghai
#
# ------------------------------------------------------------------------------
# 		Versions:   1.0  2014-12-20
#           		Created
#
#       
#                   1.1  [Theodore: 2015-02-21]
#                   DO NOT sleep after start/stop a process, so that hundreds
#                   of virtual clients can start in a short time.
#
# 
#                   1.2  [Theodore: 2016-02-24]
#                   Add an argument $5, to control the sleep time after
#                   stopping/starting the process. 
#
#                   1.3 [Theodore: 2016-10-26]
#                   Add option for: silence & verbose mode
#
# ==============================================================================


if [ x$StLibHome !=  x ]; then
    #echo "INFO: StLibHome is set to $StLibHome"
    . $StLibHome/runtime/st-func.sh
else
    echo "WARN: try to load st-func.sh in current directory..."
    . ./st-func.sh
fi




# ------------------------------------------------------------------------------ 
# Functions
# ------------------------------------------------------------------------------ 

FUNC_RET=0

function usage 
{
    cat << EOF
Usage:
    $G_SCRIPT [OPTIONS] <ServiceName>  <JavaApp> <Args>  status|start|stop

Options:
    -h              show help info

    -s              silent mode
                    DO NOT sleep after starting/stoping the progress;
                    NO NOT use stdin or stdout files;

    -t  SLEEP_TIME  sleep seconds after start/stop the progress
                    float is supported. e.g. 0.7, 1.2.
                    Default: 0.5 second.


    -v              show verbose info

Examples:
   show help info:
        $G_SCRIPT -h
        $G_SCRIPT 

   start progress client_online
        $G_SCRIPT user00-login cn.teclub.ha.test.ClientOnline "uesr00 abcD1234 /tmp/sdcard" start

   stop progress client_online
        $G_SCRIPT user00-login cn.teclub.ha.test.ClientOnline "uesr00 abcD1234 /tmp/sdcard" stop

EOF
}



function show_service
{
    if [ $G_OPT_VERBOSE -eq 1 ]; then
        echo "DBG: process info before grep -v"
        echo $cmd_check0 | sh
    fi

    echo "-------- Show Service Info  ---------------------------------------------------- "
    if [ -f $PID_PATH_NAME ]; then
        echo "Service PID: `cat $PID_PATH_NAME` "
        echo $cmd_check | sh
        ret=$?
        if [ $ret -ne 0 ];then
            echo "ERR: Java Process '$JAVA_APP' is NOT running, but the PID file still exist!"
            echo "WRN: You may delete the PID file, manually." 
        fi
    else
        echo "NO PID file \"$PID_PATH_NAME\"! "
        echo "Process Info: "
        echo $cmd_check | sh
        ret=$?
        if [ $ret -eq 0 ];then
            echo "ERR: Java Process is running: '$JAVA_APP', while PID file is missing! " 
            echo "WRN: Kill the process, manually!"
        fi
    fi

    echo "-------------------------------------------------------------------------------- "
}



# ------------------------------------------------------------------------------ 
# MAIN ENTRY
# ------------------------------------------------------------------------------ 


G_OPT_SILENT=0
G_OPT_SLEEP_TIME=0.5
G_OPT_VERBOSE=0
G_SCRIPT=`basename $0`


count_shift=0
while getopts hst:v cmdopt
do
    case $cmdopt in 
    h) usage
       exit 0
        ;;

    s) G_OPT_SILENT=1
       echo "INF: silent mode"
       count_shift=`expr $count_shift + 1`
       ;;

    t) G_OPT_SLEEP_TIME=$OPTARG
       echo "INF: set sleep-time: $G_OPT_SLEEP_TIME seconds"
       count_shift=`expr $count_shift + 2`
       ;;

    v) G_OPT_VERBOSE=1
       echo "INF: verbose mode"
       count_shift=`expr $count_shift + 1`
       ;;

    *) echo "[ERR] unknown option: \"$cmdopt\" !" 
       echo "Input Command:  $G_SCRIPT $*"
       usage
       exit 1
       ;;
    esac
done

shift $count_shift


if [ $# -ne 4 ]; then
    echo "ERR: Incorrect Arguments!"
    echo "Command & Arguments: [$#] $G_SCRIPT $*"
    usage
    exit 1
fi





# ------------------------------------------------------------------------------ 
# globals
# ------------------------------------------------------------------------------ 

#set -x

SLEEP_TIME=$G_OPT_SLEEP_TIME
SELF_SCRIPT=$G_SCRIPT
SERVICE_NAME=$1
JAVA_APP_CLASS=$2
JAVA_APP_ARGS=$3
SERV_CMD=$4

CMD_STDIN=.ck-service__${SERVICE_NAME}.stdin
CMD_STDOUT=.ck-service__${SERVICE_NAME}.stdout
CMD_STDERR=.ck-service__${SERVICE_NAME}.stderr
PID_PATH_NAME=.ck-service__${SERVICE_NAME}.pid



JAVA_APP="$JAVA_APP_CLASS $JAVA_APP_ARGS"
if [ "x" = "x$JAVA_APP_ARGS" ];then
    JAVA_APP="$JAVA_APP_CLASS"
fi

cmd_check0="ps -ef | grep \"$JAVA_APP\" "
cmd_check="ps -ef | grep \"$JAVA_APP\" | grep -v grep | grep -v $SELF_SCRIPT"


case $SERV_CMD in
    status)
        echo "Get status of $SERVICE_NAME ..."
        show_service 
        ;;

    start)
        # ret=0: find the running service process
        echo $cmd_check | sh > /dev/null; ret=$?
        if [ $ret -eq 0 ]; then
            echo "WRN: find running process!"
            show_service
            exit 1
        fi

        if [ -f $PID_PATH_NAME ]; then
            echo "WRN: PID File Exists: $PID_PATH_NAME"
            show_service
            exit 1
        fi

        echo "Starting $SERVICE_NAME ..."

        if [ $G_OPT_SILENT -eq 1 ]; then
            # silent mode
            nohup java ${JAVA_APP} 2>&1 > /dev/null  & 
            echo $! >> $PID_PATH_NAME
        else
            if [ ! -f ${CMD_STDIN} ];then
                touch ${CMD_STDIN}
            fi
            nohup java ${JAVA_APP} < ${CMD_STDIN} 2>&1 > ${CMD_STDOUT}  & 
            echo $! >> $PID_PATH_NAME
            sleep $SLEEP_TIME
            show_service 
        fi


        echo "Started Service: $SERVICE_NAME "
        ;;

    stop)
        if [ ! -f $PID_PATH_NAME ]; then
            echo "ERR: NO PID FILE: $PID_PATH_NAME "
            show_service
            exit 1
        fi

        # ret=0: find the running service process
        echo $cmd_check | sh > /dev/null; ret=$?
        if [ $ret -ne 0 ]; then
            echo "ERR: NO Process is found!"
            show_service
            echo "WRN: ONLY delete PID File: $PID_PATH_NAME"
            rm $PID_PATH_NAME
            exit 1
        fi

        PID=$(cat $PID_PATH_NAME);
        echo "Stopping service: $SERVICE_NAME ..."
        kill $PID 
        rm $PID_PATH_NAME

        if [ $G_OPT_SILENT -ne 1 ]; then
            sleep $SLEEP_TIME
            show_service 
        fi
        echo "Stopped service: $SERVICE_NAME "
        ;;

    *)
        echo "ERROR: unknown service command $SERV_CMD "
        exit 1
    ;;
esac 

