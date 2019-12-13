################################################################################ 
#
# Created:          [Theodore: 2015-12-24]
#
# Description:      Add jar files in {ProjectDir}/libs into CLASSPATH
#
# Logs:             [Theodore: 2016-11-18] add argument 'ProjectPath'
#                   
#
################################################################################ 



# ------------------------------------------------------------------------------ 
#
# global variables
#
# ------------------------------------------------------------------------------ 


function usage 
{
    cat << EOF
Description: 
~~~~~~~~~~~~
    Add jar files in <DirectoryPath>

    Usage: setenv_jar.sh  <DirectoryPath> 

Examples:
~~~~~~~~~~
    $ setenv_jar.sh  ~/cook/work/git_teclub.cn/StSoftware/src/fambo/StGenLib4/libs

EOF
}



# ------------------------------------------------------------------------------ 
# MAIN ENTRY
# ------------------------------------------------------------------------------ 

if [ $# != 1 ];then
    usage
    sleep 3
    exit 1
fi


JAR_LIB_PATH=$1
tmp_file=/tmp/.st_setenv_jar_tmp_`date +%F_%H%M%S`


#echo "DBG: add jar files in JAR_LIB_PATH \"$JAR_LIB_PATH\" "
ls ${JAR_LIB_PATH} | while read aa
do
    #echo "[$$]DBG: add jar file: $aa ..."
    CLASSPATH=$CLASSPATH:${JAR_LIB_PATH}/${aa}
    echo "$CLASSPATH" > $tmp_file
done


# [Theodore: 2016-04-02] a temp file is used to store CLASSPATH in previous
# while loop.  CLASSPATH, set in above while loop, is NOT valid here!!!
#
NEW_LIB=`cat $tmp_file`
export CLASSPATH=$NEW_LIB:$CLASSPATH



# un-comment when debuging the environment!
#st_show_env

