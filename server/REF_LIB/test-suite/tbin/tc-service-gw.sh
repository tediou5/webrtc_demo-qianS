# [Theodore: 2016-10-29] Deprecated

if [ $# -ne 1 ];then
    echo "Usage: $0 status|start|stop "
    exit 1
fi

serv_name=tc_start_gw0
serv_cmd=cn.teclub.ha.test.TestGateway
serv_args="AA00 gw0_log4j.cfg"


ck-service.sh  $serv_name $1 $serv_cmd "$serv_args"  2>&1



