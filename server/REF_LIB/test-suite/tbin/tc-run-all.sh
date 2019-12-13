function run_testcase {
    tc-run.sh $1  v
    sleep 3
}


clear 
sleep 1


. ./tc-setenv.sh
echo -e "######## Start Test Suite at `date` \n"

run_testcase TC001
run_testcase TC002
run_testcase TC004
run_testcase TC005

#run_testcase TC010

echo ""
echo -e "######## End Test Suite at `date` \n"

