#!/bin/bash
. ./setenv.sh
clear

# $1: number of new monitor
# $2: output file
java  cn.teclub.ha.net.serv.StAddMonitor $1 $2 

