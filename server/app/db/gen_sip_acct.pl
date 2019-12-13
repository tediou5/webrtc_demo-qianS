#!/usr/bin/perl 
################################################################################ 
# File:         gen_sip_acct.pl
# Author:       Theodore Cao, guilin.cao@hp.com
#
# Description:  Generate SIP account file
#               
# Version:      1.0  2016-01-10 Created 
#               1.1  2016-09-04 Update
#               Generate both SIP user file & S.T. SQL script;
#
################################################################################ 

BEGIN
{
    unshift(@INC,".");
}

use strict;
use Getopt::Long;
use Time::Local;
use File::Copy;


# ------------------------------------------------------------------------------ 
# configurations
# ------------------------------------------------------------------------------ 


# ------------------------------------------------------------------------------ 
# glocals
# ------------------------------------------------------------------------------ 
my $g_out_sip="users.db.txt";
my $g_out_sql="db_sip_account.sql";


# Free SIP accounts: 
my $g_count_free=1024;

# Testing SIP accounts: 
#   128 --> Fambo User
#   128 --> Fambo GW
#
my $g_count_test=1024*16;

#my $DEF_PASS="^^ST_CallObj_1984_password_##";
my $DEF_PASS="abcd1234";


&main();

# ------------------------------------------------------------------------------ 
#
# MAIN
#
# ------------------------------------------------------------------------------ 
sub main
{
    print "[INF] Output to files: ${g_out_sip} & ${g_out_sql} \n";

	open(F_SIP, ">$g_out_sip") or die ("cann't open SIP User file. $!");
	open(F_SQL, ">$g_out_sql") or die ("cann't open SQL file. $!");

    ###### free accounts
    print "[INF] Generate ${g_count_free} FREE accounts ... \n";
    for(my $i=0; $i<$g_count_free; $i++) {
        my $sip_id  = sprintf("stuF%04lX", $i);
        my $sip_id2 = sprintf("stgF%04lX", $i);
        my $r_id    = sprintf("0x01%04lX", $i);
        my $r_id2   = sprintf("0x02%04lX", $i);

        print (F_SIP   "${sip_id}\@sip.teclub.cn ${DEF_PASS}\n");
        print (F_SIP  "${sip_id2}\@sip.teclub.cn ${DEF_PASS}\n");
        print (F_SQL "INSERT INTO tb_sip_acct(id, sip_id, sip_passwd) VALUES (${r_id}, '${sip_id}', '${DEF_PASS}'); \n");
        print (F_SQL "INSERT INTO tb_sip_acct(id, sip_id, sip_passwd) VALUES (${r_id2}, '${sip_id2}', '${DEF_PASS}');\n");
    }
    print (F_SIP  "\n\n\n\n \n\n\n\n");
    print (F_SQL  "\n\n\n\n \n\n\n\n");


    ###### testing accounts
    print "[INF] Generate ${g_count_test} TESTING accounts ... \n";
    for(my $i=0; $i<$g_count_test; $i++) {
        my $sip_id  = sprintf("stu%04lX", $i);
        my $sip_id2 = sprintf("stg%04lX", $i);
        my $r_id    = sprintf("0x0A%04lX", $i);
        my $r_id2   = sprintf("0x0B%04lX", $i);

        print (F_SIP   "${sip_id}\@sip.teclub.cn ${DEF_PASS}\n");
        print (F_SIP  "${sip_id2}\@sip.teclub.cn ${DEF_PASS}\n");
        print (F_SQL "INSERT INTO tb_sip_acct(id, sip_id, sip_passwd) VALUES (${r_id}, '${sip_id}', '${DEF_PASS}'); \n");
        print (F_SQL "INSERT INTO tb_sip_acct(id, sip_id, sip_passwd) VALUES (${r_id2}, '${sip_id2}', '${DEF_PASS}');\n");
    }

    print (F_SIP  "\n\n\n\n \n\n\n\n");
    print (F_SQL  "\n\n\n\n \n\n\n\n");

    close(F_SIP);
    close(F_SQL);
    print "[INF] DONE ^_^ \n\n";
}




