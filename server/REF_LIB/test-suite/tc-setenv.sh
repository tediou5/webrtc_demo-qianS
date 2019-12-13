################################################################################ 
#
# Created:          [Theodore: 2015-12-24]
#
# Description:      Set environment for all TEST CASES
#                   
#                   This script does:
#                   - add jar files in {ProjectDir}/lib into CLASSPATH;
#                   - add 'ProjectHome/bin' and 'pwd/conf' into CLASSPATH;
#                   - add 'pwd/bin' into PATH;
#                   - import tc-func
#                       
#                   ATTENTION: JDK 1.7 must be set before calling this script. 
#                       
#
################################################################################ 



# arg: fake-dev-dir
# Change this var if the fake-dev-dir changes
FakeDevDir=~/cook/fake-dev
StLibHome=~/work_lib4


# global variables
WorkDir=`pwd`
StPrjHome=`pwd`/..
StBinDir=${StLibHome}/bin


function tc_create_links {
    cd $StPrjHome

    # [Theodore: 2016-07-11] This link seems useless！
    echo "INF: create links to StGenLib4 Project (useless?)"
    if [ -s $StLibHome ];then
        echo -e "INF: StLibHome exist \"$StLibHome\" "
        echo -e "INF: !!!! make sure StLibHome --> StGenLib4 Project !!!!"
        ls -l $StLibHome/ 
    else
        echo -e "INF: Create link \"$StLibHome\" --> StGenLib4 Project "
        ln -s `pwd`   $StLibHome 
    fi


    echo "INF: create links to fake-dev"
    cd test-suite
    if [ ! -s fake-dev ];then
        ln -s  $FakeDevDir fake-dev
    fi

    echo "INF: DONE! Create Symbol Links!"
}




# ------------------------------------------------------------------------------ 
# MAIN ENTRY
# ------------------------------------------------------------------------------ 

echo -e "!!!! ATTENTION: JDK 1.7 must be set, in advance !!!!"

# [Theodore: 2016-10-27] must execute at first! 
# Following env is based on the created symbol links.
tc_create_links


cd $StLibHome/runtime
. ./setenv_jar.sh

cd $WorkDir
. ./tbin/tc-func

export StLibHome
export CLASSPATH=.:$WorkDir/conf:$StBinDir:$CLASSPATH
export PATH=$WorkDir/tbin:$PATH
export PATH=$StPrjHome/runtime:$PATH
export PATH=.:$PATH
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true"


# test case env
export TC_RESULT_LOG"=result.log"
export TC_RESULT_BM="result.bm"
export TC_STDOUT=stdout.log


# un-comment when debuging the environment!
#st_show_env

