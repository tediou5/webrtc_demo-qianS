#!/usr/bin/perl 

################################################################################ 
# File:         
# Author:       Theodore Cao, guilin.cao@hp.com
# Date:         2015-12-25
#
# Description:  Massive clients Logs in. 
#
#
#               [Theodore: 2016-10-27] Start/Stop independent & virtual clients
#
#               Indepedend Client
#               ~~~~~~~~~~~~~~~~~
#               Creates a process for each independent client, which is a java
#               process and consumes about 40MB. 100 independent clients
#               consume about 4GB. 
#               
#
#               Virtual Client
#               ~~~~~~~~~~~~~~
#               Create one process to simulate multiple clients.
#               
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

# count of independent clients.      
# e.g. 16: user-A0100 ~ user-A010F
my $COUNT = 8;       
                    

# count of virtual clients 
# e.g. 16: user-A0800 ~ user-A080F, gw-B0800 ~ gw-B080F
my $COUNT_V = 16;   




# ------------------------------------------------------------------------------ 
# glocals
# ------------------------------------------------------------------------------ 
my $g_arg_cmd="";
my $g_arg_h=0;
my $PASSWORD='abcd1234';


&main();

# ------------------------------------------------------------------------------ 
# MAIN
# ------------------------------------------------------------------------------ 
sub main
{
	GetOptions( 
			'i=i' 		=> \$COUNT,
			'v=i' 		=> \$COUNT_V,
			'c=s' 		=> \$g_arg_cmd,
            'h!'        => \$g_arg_h 
	) or ( &usage() and die ("option error. I will die. faint!"));

    if ( $g_arg_h > 0 ) {
        print "INF: print help info ... \n";
        &usage();
        exit 0;
    }

    if("" eq $g_arg_cmd) {
        print "ERR: arguments required! \n";
        &usage();
        exit 1;
    }


    for(my $i=0; $i< $COUNT; $i++) {
        my $serv_name  = sprintf("user-%lX-login", hex("A0100") + $i);
        my $user_name  = sprintf("user-%lX", hex("A0100") + $i);
        my $cmd="ck-service.sh -s ${serv_name} cn.teclub.ha.test.ClientOnline  \"${user_name} ${PASSWORD} \/tmp\" ${g_arg_cmd}";

        print "\nINF: Executing: \"$cmd\" ...\n";
        system($cmd);

        if( (1+$i) % 8 == 0  && (1 + $i)<$COUNT) {
            print "\nDBG: sleep a while...\n\n"; 
            sleep(2);
        }
    }

    print "\nINF: ${g_arg_cmd} ${COUNT} Independent Clients ^_^  \n";
    sleep(4);

    my $sleep_s = 4; 
    if($g_arg_cmd eq "start"){
        # login takes more time than logoff. 
        $sleep_s +=  ${COUNT_V}/4;
    }else{
        $sleep_s +=  ${COUNT_V}/8;
    }

    my $cmd="ck-service.sh -t ${sleep_s}  virtual-client  cn.teclub.ha.test.VirtualClient \"${COUNT_V}\" ${g_arg_cmd}";
    print "\nINF: Virtual Client: \"$cmd\" ...\n";
    print "\nDBG: Sleep enough time: ${sleep_s} seconds ... \n";
    system($cmd);
    print "\nINF: ${g_arg_cmd} Virtual Client ^_^ \n";

}



sub usage{

my $tt_usage ="
Description: massive clients log in ck-services. 

Usage: perl tc-mass-clients.pl [OPTIONS] 

Options:
~~~~~~~~
-c <start/stop>     start/stop ck-services.

-h                  Show help info.

-i <NUM>            Count of independent clients.      
                    Default: 8
         
-v <NUM>            Count of virtual clients.      
                    Default: 16
         
Examples:
~~~~~~~~
  > perl tc-mass-clients.pl -i 32 -v 128  -c start
  > perl tc-mass-clients.pl -i 32 -v 128  -c stop
  >
  > perl tc-mass-clients.pl  -c start
  > perl tc-mass-clients.pl  -c stop

";

    print $tt_usage;
}


