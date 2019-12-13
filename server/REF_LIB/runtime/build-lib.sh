#!/bin/bash

# ------------------------------------------------------------------------------ 
# globals
# ------------------------------------------------------------------------------ 
StSrcDir=../src
StBinDir=./bin

. ./setenv.sh
. ./st-func.sh

sleep 2
clear

# ------------------------------------------------------------------------------ 
# Main Entry
# ------------------------------------------------------------------------------ 

st_echo "INF: SET UP BUILDING ENVIRONMENT"

JAVAC_OPT="-Xlint:deprecation  -d  ${StBinDir}  -sourcepath  ${StSrcDir} "

st_echo "INF: Create an empty 'bin' folder"
create_bin_f ${StBinDir}


st_echo "INF: Start building ..."

#set -x
javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/common/*.java 
st_echo "==== [$?] Finish Building 'cn.teclub.common' ==== "

javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/lib/*.java 
java cn.teclub.ha.lib.StConst

javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/net/*.java  
javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/net/serv/St*.java   \
                    ${StSrcDir}/cn/teclub/ha/net/serv/MySql*.java  \
                    ${StSrcDir}/cn/teclub/ha/net/serv/request/St*.java 
javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/request/St*.java  

#javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/test/*.java 
#javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/net/client/*.java 
st_echo "==== [$?] Finish Building Server ===="

#set +x
sleep 2


st_echo "INF: copy config files ..."
cp  ${StSrcDir}/hibernate.cfg.xml       ${StBinDir}
cp  ${StSrcDir}/St*.hbm.xml             ${StBinDir}
cp  ${StSrcDir}/st*.properties          ${StBinDir}


echo -e "\n---------------------------------------------------------------"
echo -e "WRN: Manually change 'key_store' in bin/st_ha_srv.properties"
echo -e "---------------------------------------------------------------\n"

echo -e "==== DONE ^_^ ==== \n"

# [Theodore: 2016-07-11]

