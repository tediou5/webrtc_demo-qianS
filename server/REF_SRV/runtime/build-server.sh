#!/bin/bash

# ------------------------------------------------------------------------------ 
# globals
# ------------------------------------------------------------------------------ 
StBinDir=./bin


. ./setenv.sh

sleep 2; clear

# ------------------------------------------------------------------------------ 
# Main Entry
# ------------------------------------------------------------------------------ 



st_echo "0. Create an empty 'bin' folder ..."
create_bin_f ${StBinDir}



st_echo "1. build GenLib ..."
StSrcDir=../../StGenLib4/src
JAVAC_OPT="-Xlint:deprecation  -d  ${StBinDir}  -sourcepath  ${StSrcDir} "

javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/common/*.java 
javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/lib/*.java 
java cn.teclub.ha.lib.StConst

javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/net/*.java  
javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/request/St*.java  
st_echo "[$?] Finish Building GenLib! \n"



st_echo "2. build SmsModule ..."
StSrcDir=../../StSmsModule/src
JAVAC_OPT="-Xlint:deprecation  -d  ${StBinDir}  -sourcepath  ${StSrcDir} "
javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/sms/St*.java 
st_echo "[$?] Finish Building SMS! \n"



st_echo "3. build Server..."
StSrcDir=../src
JAVAC_OPT="-Xlint:deprecation  -d  ${StBinDir}  -sourcepath  ${StSrcDir} "
javac $JAVAC_OPT    ${StSrcDir}/cn/teclub/ha/net/serv/St*.java   \
                    ${StSrcDir}/cn/teclub/ha/net/serv/MySql*.java  \
                    ${StSrcDir}/cn/teclub/ha/net/serv/request/St*.java 
st_echo "[$?] Finish Building Server! \n"


st_echo "INF: copy config files ..."
cp  ${StSrcDir}/hibernate.cfg.xml       ${StBinDir}
cp  ${StSrcDir}/St*.hbm.xml             ${StBinDir}
cp  ${StSrcDir}/st*.properties          ${StBinDir}
cp  ../../StSmsModule/src/st*.properties    ${StBinDir}


echo -e "\n-------------------------------------------------------------------------"
echo -e "WRN: Manually change 'key_store' in bin/st_ha_srv.properties"
echo -e "-------------------------------------------------------------------------\n"

echo -e "==== DONE ^_^ ==== \n"



