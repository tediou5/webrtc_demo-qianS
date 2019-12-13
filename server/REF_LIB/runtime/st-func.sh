#!/bin/bash
# ==============================================================================
#
#       Filename: 	st-func.sh
#
#    Description:  	Function library file.
#
#
#        Version:  	1.0
#        Created:  	2016-02-24
#       Revision:  	none
#       Compiler:  	bash
#
#         Author:  	Theodore Cao 
#        Company:  	Spring Tech, Shanghai
#
# ------------------------------------------------------------------------------
# 		Versions:   1.0  2016-02-24
#           		Created
#       
#
#
# ==============================================================================



function st_echo
{
    msg=$1
    echo -e "[$$] $1" 
    sleep 1
}


# Description: create binary folder
# If the target binary folder exists, it is backed up. 
function create_bin_f
{
    bin_dir=$1
    if [ ! -d  ${bin_dir} ]; then
        mkdir ${bin_dir}
    else
        echo "INFO: Backup old binary folder '${bin_dir}' and create a new one ..." 
        mv ${bin_dir}  ${bin_dir}_`date +%F_%H%M%S`
        mkdir ${bin_dir}
    fi
}



function st_show_env
{
    echo -e "====================================================================="
    echo -e "######## Java Environment  ##########################################"
    echo -e "Java Compiler:  `which javac`"
    echo -e "JAVA_HOME     = $JAVA_HOME \n"
    echo -e "PATH=$PATH \n"
    echo -e "JAVA_TOOL_OPTIONS=$JAVA_TOOL_OPTIONS \n"
    echo -e "CLASSPATH=$CLASSPATH"
    echo -e "StLibHom=$StLibHome"
    echo -e "====================================================================="


    while IFS=':' read -ra ADDR; do
      for i in "${ADDR[@]}"; do
          # process "$i"
          echo "[CP]$i"
      done
    done <<< "$CLASSPATH"

}



