#!/bin/sh
# ==============================================================================
#
#       Filename: 	tc-util.sh
#
#    Description:  	H.A. Test Utility Script
#
#        Version:  	1.0
#        Created:  	2014-12-20 
#       Revision:  	none
#       Compiler:  	bash
#
#         Author:  	Theodore Cao 
#        Company:  	Spring Tech, Shanghai
#
# ------------------------------------------------------------------------------
# 		Versions:   1.0  2014-12-20
#           		Created
#       
#                   1.1  TODO
#
# ==============================================================================

# ------------------------------------------------------------------------------ 
# globals
# ------------------------------------------------------------------------------ 

TC_WORKDIR=`pwd`

TC_RUNTIME=${TC_WORKDIR}/tc-runtime
TC_RESULT=${TC_WORKDIR}/tc-result
TC_BM=${TC_WORKDIR}/benchmark
TC_BIN=${TC_WORKDIR}/bin
TC_SRC=${TC_WORKDIR}/src


cmd_help="
Usage: $0 clear|list|save|bm|show_bm {TestCase} | show_log {TestCase}

sub commands:
~~~~~~~~~~~~~
bm              Save Previous Results as Benchmarks
clear           Clear pervious test case
help            Show this help message
list            Show all test cases
save            Save Previous Test Case Result 
show_bm  TC     Show Test Case result benchmark
show_log TC     Show Test Case result log
show_out TC     Show Test Case STDOUT 
version         Show StGenLib Version (based on TC002 result.log)
"

# ------------------------------------------------------------------------------ 
# Real Meat
# ------------------------------------------------------------------------------ 


#set -x 
if [ $# -eq 0 ];then
    echo  "$cmd_help"
	exit 1
fi

case $1 in
    version)
    cat ./tc-result/TC002/stdout.log |grep INFO |grep -i version
    ;;
    show_out)
    if [ $# -ne 2 ];then
        echo  "$cmd_help"
        exit 1
    fi
    echo "INFO: Show Test Case Stdout"
    cat ./tc-result/$2/stdout.log | more
    ;;

    show_bm)
    if [ $# -ne 2 ];then
        echo  "$cmd_help"
        exit 1
    fi
    echo "INFO: Show Test Case result benchmark "
    cat ./tc-result/$2/result.bm | more
    ;;
        
    show_log)
    if [ $# -ne 2 ];then
        echo  "$cmd_help"
        exit 1
    fi
    echo "INFO: Show Test Case result log"
    cat ./tc-result/$2/result.log | more
    ;;

    bm)
    echo "INFO: Save Previous Results as Benchmarks"
    echo "INFO: Old Benchmarks: "
    cd $TC_BM
    ls -trl *.bm
    cd $TC_RESULT 
    ls -d TC* | grep -v '_20' |grep -v 'grep' | while read tc
    do
        # ls -l ${tc}/result.log
        echo "update test case benchmark: $tc"
        cp  ${tc}/result.log $TC_BM/${tc}.bm
    done
    echo "INFO: New Benchmarks: "
    cd $TC_BM
    ls -trl *.bm
    ;;

    save)
    echo "INFO: Save Previous Test Case Result"
    if [ -d $TC_RESULT ]; then
        ts_append=_`date +%F_%H%M%S`
        mv $TC_RESULT ${TC_RESULT}_${ts_append}
    fi
    ;;
    
    clear)
    echo "INFO: Clear Testing Environment"
    ts_append=_`date +%F_%H%M%S`
    if [ -d $TC_BIN ]; then
        mv $TC_BIN ${TC_BIN}_${ts_append}
        mv ${TC_BIN}_${ts_append}  /tmp/
    fi
    if [ -d $TC_SRC ]; then
        mv $TC_SRC ${TC_SRC}_${ts_append}
        mv ${TC_SRC}_${ts_append}  /tmp/
    fi

    if [ -d $TC_RESULT ]; then
        mv $TC_RESULT ${TC_RESULT}_${ts_append}
        mkdir  $TC_RESULT 
    fi
    if [ -d $TC_RUNTIME ]; then
        echo "WARN: remove previouse runtime folder"
        mv $TC_RUNTIME  ${TC_RUNTIME}_${ts_append}
        mv ${TC_RUNTIME}_${ts_append} /tmp/
    fi
    ;;

    list)
    echo "INFO: Show All Test Cases "
    ls TC*  | grep -v 'grep' |grep -v 'tc-'
    ;;
esac 

