#!/bin/bash

S_TIME=40

if [ $# -ne 1 ];then
    echo "Usage: $0 status|start|stop "
    exit 1
fi


echo "!!!! Run this script on Server with LightConntion !!!!"
sleep 3


echo "INF: start 4K (4*1024) virtual clients..."
ck-service.sh -t $S_TIME  st_test__v20 cn.teclub.ha.test.VirtualClient "256 0A2000 0B2000"  $1
ck-service.sh -t $S_TIME  st_test__v21 cn.teclub.ha.test.VirtualClient "256 0A2100 0B2100"  $1
ck-service.sh -t $S_TIME  st_test__v22 cn.teclub.ha.test.VirtualClient "256 0A2200 0B2200"  $1
ck-service.sh -t $S_TIME  st_test__v23 cn.teclub.ha.test.VirtualClient "256 0A2300 0B2300"  $1

ck-service.sh -t $S_TIME  st_test__v24 cn.teclub.ha.test.VirtualClient "256 0A2400 0B2400" $1
ck-service.sh -t $S_TIME  st_test__v25 cn.teclub.ha.test.VirtualClient "256 0A2500 0B2500" $1
ck-service.sh -t $S_TIME  st_test__v26 cn.teclub.ha.test.VirtualClient "256 0A2600 0B2600" $1
ck-service.sh -t $S_TIME  st_test__v27 cn.teclub.ha.test.VirtualClient "256 0A2700 0B2700" $1


ck-service.sh -t $S_TIME  st_test__v28 cn.teclub.ha.test.VirtualClient "256 0A2800 0B2800" $1
ck-service.sh -t $S_TIME  st_test__v29 cn.teclub.ha.test.VirtualClient "256 0A2900 0B2900" $1
ck-service.sh -t $S_TIME  st_test__v2A cn.teclub.ha.test.VirtualClient "256 0A2A00 0B2A00" $1
ck-service.sh -t $S_TIME  st_test__v2B cn.teclub.ha.test.VirtualClient "256 0A2B00 0B2B00" $1

ck-service.sh -t $S_TIME  st_test__v2C cn.teclub.ha.test.VirtualClient "256 0A2C00 0B2C00" $1
ck-service.sh -t $S_TIME  st_test__v2D cn.teclub.ha.test.VirtualClient "256 0A2D00 0B2D00" $1
ck-service.sh -t $S_TIME  st_test__v2E cn.teclub.ha.test.VirtualClient "256 0A2E00 0B2E00" $1
ck-service.sh -t $S_TIME  st_test__v2F cn.teclub.ha.test.VirtualClient "256 0A2F00 0B2F00" $1




# [Theodore: 2016-11-10] on macbook15, you cannot start too many clients! 
#
#sleep 50
#
#echo "INF: start 4K (4*1024) virtual clients..."
#ck-service.sh -t $S_TIME  st_test__v30 cn.teclub.ha.test.VirtualClient "256 0A3000 0B3000"  $1
#ck-service.sh -t $S_TIME  st_test__v31 cn.teclub.ha.test.VirtualClient "256 0A3100 0B3100"  $1
#ck-service.sh -t $S_TIME  st_test__v32 cn.teclub.ha.test.VirtualClient "256 0A3200 0B3200"  $1
#ck-service.sh -t $S_TIME  st_test__v33 cn.teclub.ha.test.VirtualClient "256 0A3300 0B3300"  $1
#
#ck-service.sh -t $S_TIME  st_test__v34 cn.teclub.ha.test.VirtualClient "256 0A3400 0B3400" $1
#ck-service.sh -t $S_TIME  st_test__v35 cn.teclub.ha.test.VirtualClient "256 0A3500 0B3500" $1
#ck-service.sh -t $S_TIME  st_test__v36 cn.teclub.ha.test.VirtualClient "256 0A3600 0B3600" $1
#ck-service.sh -t $S_TIME  st_test__v37 cn.teclub.ha.test.VirtualClient "256 0A3700 0B3700" $1
#
#ck-service.sh -t $S_TIME  st_test__v38 cn.teclub.ha.test.VirtualClient "256 0A3800 0B3800" $1
#ck-service.sh -t $S_TIME  st_test__v39 cn.teclub.ha.test.VirtualClient "256 0A3900 0B3900" $1
#ck-service.sh -t $S_TIME  st_test__v3A cn.teclub.ha.test.VirtualClient "256 0A3A00 0B3A00" $1
#ck-service.sh -t $S_TIME  st_test__v3B cn.teclub.ha.test.VirtualClient "256 0A3B00 0B3B00" $1
#
#ck-service.sh -t $S_TIME  st_test__v3C cn.teclub.ha.test.VirtualClient "256 0A3C00 0B3C00" $1
#ck-service.sh -t $S_TIME  st_test__v3D cn.teclub.ha.test.VirtualClient "256 0A3D00 0B3D00" $1
#ck-service.sh -t $S_TIME  st_test__v3E cn.teclub.ha.test.VirtualClient "256 0A3E00 0B3E00" $1
#ck-service.sh -t $S_TIME  st_test__v3F cn.teclub.ha.test.VirtualClient "256 0A3F00 0B3F00" $1



echo "INF: DONE!"



