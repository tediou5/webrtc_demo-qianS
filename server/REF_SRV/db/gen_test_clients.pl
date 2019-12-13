#!/usr/bin/perl 
################################################################################ 
# File:         db_gen_test.perl
# Author:       Theodore Cao, guilin.cao@hp.com
#
# Description:  Generate a SQL script to add 10,000 clients into DB 
#               
# Version:      1.0 2015-03-01
#               Created 
#
#               1.1 2016-10-23
#               Modified for StGenLib4
#
################################################################################ 

BEGIN
{
    unshift(@INC,".");
}

use strict;
use Getopt::Long;
use Time::Local;
use Env;
use File::Copy;


# ------------------------------------------------------------------------------ 
# configurations
# ------------------------------------------------------------------------------ 


# ------------------------------------------------------------------------------ 
# glocals
# ------------------------------------------------------------------------------ 
my $g_outfile="db_add_10000clients.sql";
my $g_count=16*1024;
my $g_frd_count=16;

&main();

# ------------------------------------------------------------------------------ 
#
# MAIN
#
# ------------------------------------------------------------------------------ 
sub main
{
    print "[DEBUG] OUPUT SQL file: $g_outfile \n";
	open(F_SQL, ">$g_outfile") or die ("cann't open SQL file. $!");

    print (F_SQL  "USE db_st_ha \n\n");

    ###### 10,000 clients
    #for(my $i=1; $i<16*1024+1; $i++) {
    for(my $i=0; $i<$g_count; $i++) {
        my $user_id     = sprintf("0x%lX", hex("0A0100") + $i);
        my $gw_id       = sprintf("0x%lX", hex("0B0100") + $i);
        my $user_name   = sprintf("user-%lX", hex("0A0100") + $i);
        my $gw_name     = sprintf("gw-%lX",   hex("0B0100") + $i);
        my $user_label  = sprintf("User-%lX", hex("0A0100") + $i);
        my $gw_label    = sprintf("GW-%lX",   hex("0B0100") + $i);

        my $user_sip    = sprintf("0x%lX",   hex("0A0100") + $i);
        my $gw_sip      = sprintf("0x%lX",   hex("0B0100") + $i);

        # INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xCC0000,  0x00, 'user0000', 'user_0000',  0x010000);
        #
        # INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB0F,  0x01, 'gw0F', '房车 04', 0xB000F );
        print (F_SQL  "INSERT INTO tb_client(id,flag,sip_acct_id,name,label) VALUES(${user_id},0x00,${user_sip}, '${user_name}', '${user_label}'); \n");
        print (F_SQL  "INSERT INTO tb_client(id,flag,sip_acct_id,name,label) VALUES(${gw_id},0x01,${gw_sip}, '${gw_name}','${gw_label}'   ); \n");
    }


    my $count=$g_count - $g_frd_count;
    for(my $i=0; $i<$count; $i++) {
        my $user_id     = sprintf("0x%lX", hex("0A0100") + $i);

        for(my $j=0; $j<16; $j++){
            my $gw_id       = sprintf("0x%lX", hex("0B0100") + $i +$j);
            print (F_SQL  "INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (${user_id}, ${gw_id}, 0x0100); \n");
        }
    }

    print (F_SQL  "SELECT count(*)  FROM tb_client; \n");
    print (F_SQL  "\n");


    close(F_SQL);
    print "[INFO] DONE! \n\n";
}




