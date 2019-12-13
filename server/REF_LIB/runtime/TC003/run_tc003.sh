#!/bin/bash
################################################################################ 
# Author:       Theodore Cao 
# Date          2015-12-25
# Description:  Many Client logs in and keeps ONLINE
#
################################################################################ 

WordDir=`pwd`
cd ..
. ./setenv.sh
cd $WordDir

echo -e "\n\n"
echo "INFO: StServer proces:"
ps -ef |grep -i StServer |grep -v grep
echo -e "####################################################################"
echo -e "####  Make sure server is running!!!! (wait for 10 seconds)"
echo -e "####  "
echo -e "####  ---- If no server process, stop test case! "
echo -e "####################################################################"
echo -e "\n\n"
sleep 10; clear


tc_time_start=`date`
perl virtual-clients.pl -c start
ps -ef |grep java|grep TestClientLogin

echo -e "\nINFO: sleep 60 seconds before stopping all services"
sleep 60
perl virtual-clients.pl -c stop

tc_time_end=`date`
echo -e "\n\n"
echo -e "#### Test Start: $tc_time_start"
echo -e "#### Test End  : $tc_time_end"


