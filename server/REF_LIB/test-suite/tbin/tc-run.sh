#!/bin/bash
# ==============================================================================
#
#       Filename: 	tc-run.sh
#
#    Description:   Test Case Driver Script
#
#                   Driver Script does almost everying for running a test case.
#
#                   Specificly, it does following job:
#                   - create Test Case runtime folder;
#                   - copy necessary files to runtime folder;
#                   - cd test-case-runtime-folder;
#                   - execute the test case, by calling script TCxxx;
#
#                   NOTE: Test Case Script TCxxx must be executed in
#                   test-case-running-folder! 
#
#        Version:  	1.1
#        Created:  	2014-12-20
#       Revision:  	none
#       Compiler:  	bash
#
#         Author:  	Theodore Cao
#        Company:  	Spring Tech, Shanghai
#
# ------------------------------------------------------------------------------
# 		Versions:   1.0 2014-12-20
#           		Created
#
#                   1.1 2016-04-04
#                   Modified for StGenLib2
#
#
#
# ==============================================================================


# ------------------------------------------------------------------------------ 
# error checking
# ------------------------------------------------------------------------------ 
 
me=`basename $0`
cmd_help="
Description: 
~~~~~~~~~~~~
    Run a test case. 

    Usage: $me <TestCase> {verify|benchmark|v|bm}

Examples:
~~~~~~~~~~
    $me TC002 v
    $me TC002 bm
"



g_ts_home=${StLibHome}/test-suite
g_tc_name=$1
g_tc_opt=$2
g_tc_homedir=`pwd`
g_tc_runtime_dir=${g_tc_homedir}/tc-runtime
g_tc_result_dir=${g_tc_homedir}/tc-result
g_tc_bm_dir=${g_ts_home}/benchmark

# must SAME to setenv.sh ???
export TC_RESULT_BM="result.bm"


# [Theodore: 2016-07-11] set-env before this script!
#. ./tc-setenv.sh
#st_show_env

. ${g_ts_home}/tbin/tc-func



# error checking
#set -x 
if [ $# -ne 2 ];then
    echo "$cmd_help"
	exit 1
fi

if [ ! -f ${g_ts_home}/testcase/$1  ];then
    echo "ERR: $1 is NOT a test case script! "
    exit 2
fi


case $g_tc_opt in
    verify|v|benchmark|bm)
    #echo "DBG: Supported Option: $g_tc_opt !!!"
    ;;
    *)
    echo "ERR: Unknown Option: $g_tc_opt !!!"
    exit 2
    ;;
esac 


#
# check runtime folder
#
if [ -d $g_tc_runtime_dir ]
then
    echo "ERR: Previous test case runtime is NOT stored! "
    exit 3
fi
mkdir $g_tc_runtime_dir
touch $g_tc_runtime_dir/${g_tc_name}_${g_tc_opt}


# check benchmark and result folder
#
if [ ! -d $g_tc_bm_dir ]
then
    echo "INF: Create Test Case Benchmark Folder."
    mkdir $g_tc_bm_dir
fi
if [ ! -d $g_tc_result_dir ]
then
    echo "INF: Create Test Case Result Folder."
    mkdir $g_tc_result_dir
fi
if [ -d $g_tc_result_dir/$g_tc_name ];
then
    echo "INF: Bakcup previous result of \"$g_tc_name\" "
    ts_append=_`date +%F_%H%M%S`
    mv $g_tc_result_dir/$g_tc_name  $g_tc_result_dir/${g_tc_name}_${ts_append}
fi




# ------------------------------------------------------------------------------ 
# set up test case
# ------------------------------------------------------------------------------ 


cd $g_tc_runtime_dir
cp  ${g_ts_home}/testcase/$g_tc_name              ./
cp  ${g_ts_home}/benchmark/${g_tc_name}.bm        $TC_RESULT_BM  
ln -s  $g_ts_home/fake-dev  


# get test case info from TC script
g_tc_info=`grep 'tc_info='  $g_tc_name | awk -F '=' '{print $2}'`



# ------------------------------------------------------------------------------ 
# run test case
# ------------------------------------------------------------------------------ 

echo ""
echo "======= TEST CASE STARTS ================================================="
echo "Name:   $g_tc_name ($g_tc_opt)"
echo "Info:   $g_tc_info"
#echo "STARTS: `date` ..."

sleep 1

cd $g_tc_runtime_dir


# re-direction 
exec  6>&1
exec > $TC_STDOUT
echo "#### Test Case Start: `date`" 

. ./$g_tc_name 

echo "#### Test Case Ends: `date`" 
exec 1>&6 6>&-      # 恢复stdout, 然后关闭文件描述符#6. 



echo "ENDS:   `date`"

case $g_tc_opt in
    verify|v)
    diff $TC_RESULT_BM $TC_RESULT_LOG
    ret=$?
    if [ $ret -eq 0  ];then
        echo "######## Result: ################################################ PASS ^_^ "
    else
        echo "######## Result: ################################################ FAIL ### "
    fi
    ;;

    benchmark|bm)
    echo "#### Create benchmark file "
    mv  $TC_RESULT_LOG $TC_RESULT_BM
    cat $TC_RESULT_BM
    ;;

    *)
    echo "DBG: opt: ${g_tc_opt}   do nothing ..."
    ;;
esac 



cd $g_tc_homedir

case $g_tc_opt in
    benchmark|bm)
        echo "======== Store Test Case Benchmark "
        cp $g_tc_runtime_dir/$TC_RESULT_BM   $g_tc_bm_dir/${g_tc_name}.bm  
        
        echo "======== Latest Benchmarks: "
        cd  $g_tc_bm_dir
        pwd
        ls -trl  *.bm  
    ;;
    *)
        #echo "DBG: opt: ${g_tc_opt}   do nothing ..."
    ;;
esac 


sleep 1


# Back up test case runtime
mv $g_tc_runtime_dir  $g_tc_result_dir/${g_tc_name}


