#!/usr/bin/perl 
################################################################################ 
# File:         
# Author:       Theodore Cao, guilin.cao@hp.com
# Date:         2015-12-25
#
# Description:  Clients Log in. 
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
my $g_arg_cmd;
my $PASSWORD='abcd1234';

&main();

# ------------------------------------------------------------------------------ 
# MAIN
# ------------------------------------------------------------------------------ 
sub main
{
	GetOptions( 
			'c=s' 		=> \$g_arg_cmd
	) or ( &usage() and die ("option error. I will die. faint!"));

    ###### 10,000 clients
    for(my $i=1; $i< 4; $i++) {
    #for(my $i=1; $i<32; $i++) {
    #for(my $i=1; $i<64; $i++) {
    #for(my $i=1; $i<128+3; $i++) {
    #for(my $i=1; $i<3*128+33; $i++) {
    #for(my $i=1; $i<4*1024+33; $i++) {
        my $serv_name  = sprintf("clt-login-%lX", hex("CC0000") + $i);
        my $user_name  = sprintf("test-user-%lX", hex("CC0000") + $i);
        my $cmd="../ck-service.sh  ${serv_name} ${g_arg_cmd} \"cn.teclub.ha.test.TestClientOnline\"  \"${user_name} ${PASSWORD}\" 2>&1";
        print "[INFO] Running Command: \"$cmd\" \n\n";
        system($cmd);
    }
}


sub usage{
    print "Usage of virtual-clients.pl \n\n";
    print "Options: \n";
    print "~~~~~~~~ \n\n";
    print "-c <start|stop> \n";
    print "        Command for the service. \n";
    print "        Start/Stop services, which log in server. \n\n";
}
