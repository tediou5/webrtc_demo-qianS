################################################################################ 
#
# Created:          [Theodore: 2015-12-24]
#
# Description:      Set environment for runtime
#                   
#                   This script does:
#                   - add jar files in {ProjectDir}/lib into CLASSPATH;
#                   - add 'bin' folder into CLASSPATH;
#                   - add 'pwd' into PATH;
#                       
#                   ATTENTION: JDK 1.7 must be set before calling this script. 
#                       
#
################################################################################ 


#
# global variables
#
StGenLibHome=`pwd`/..
StBinDir=`pwd`/bin

. ./st-func.sh
. ./setenv_jar.sh

# ------------------------------------------------------------------------------ 
# MAIN ENTRY
# ------------------------------------------------------------------------------ 


echo -e "!!!! ATTENTION: JDK 1.7 must be set, in advance !!!!"

export CLASSPATH=.:$StBinDir:$CLASSPATH
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true"
export PATH=.:`pwd`:$PATH


# un-comment when debuging the environment!
st_show_env



