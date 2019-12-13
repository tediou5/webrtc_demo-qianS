################################################################################ 
#
# Created:          [Theodore: 2015-12-24]
#
# Description:      Set environment for runtime
#                   
#                   This script does:
#                   - add jar files in several projects into CLASSPATH;
#                   - add 'bin' folder into CLASSPATH;
#                   - add 'pwd' into PATH;
#                       
#                   ATTENTION: JDK 1.7 must be set before calling this script. 
#                       
#
################################################################################ 


# ------------------------------------------------------------------------------ 
# global variables
# ------------------------------------------------------------------------------ 

StBinDir=`pwd`/bin




# ------------------------------------------------------------------------------ 
# MAIN ENTRY
# ------------------------------------------------------------------------------ 


echo -e "!!!! ATTENTION: JDK 1.7 must be set, in advance !!!!"


# [Theodore: 2016-11-18] to use ck-service.sh,  set StLibHome!
export StLibHome=../../StGenLib4

export PATH=$StLibHome/runtime:$PATH
export PATH=.:`pwd`:$PATH
export CLASSPATH=.:$StBinDir:$CLASSPATH
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true"


. st-func.sh
. setenv_jar.sh  ../../StGenLib4/libs
. setenv_jar.sh  ../../StSmsModule/libs
. setenv_jar.sh  ../libs



# un-comment when debuging the environment!
# st_show_env



