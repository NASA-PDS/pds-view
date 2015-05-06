#!/usr/bin/perl

# This is a utility script for updating an old LogAnalysisInfo from a new one
# (during an update).  Run this script, and provide the pathnames of the old
# and new LogAnalysisInfo directories, and it will copy everything over.
# It will not modify the old LogAnalysisInfo directory.

use strict;
my $usage = "\nUsage: update.pl {old-LogAnalysisInfo} {new-LogAnalysisInfo}\n\n" .
    "  {old-LogAnalysisInfo}: the pathname of the old LogAnalysisInfo directory\n" .
    "  {new-LogAnalysisInfo}: the pathname of the new LogAnalysisInfo directory\n\n";


if (($ARGV[0] eq "") || ($ARGV[1] eq "") || (!($ARGV[0] =~ /LogAnalysisInfo\/*$/)) || (!($ARGV[1] =~ /LogAnalysisInfo\/*$/))) {
    print $usage;
    exit(0);
}

my $oldlai = $ARGV[0];
my $newlai = $ARGV[1];

if ($oldlai =~ /^(.*[^\/])$/) {
    $oldlai .= "/";
}
if ($newlai =~ /^(.*[^\/])$/) {
    $newlai .= "/";
}

sub runcmd {
    my ($cmd) = @_;
    print $cmd . "\n";
    system($cmd);
}

print "Updating $newlai from the contents of $oldlai\n";

runcmd("cp ${oldlai}users.cfg ${newlai}users.cfg");
#runcmd("cp ${oldlai}licenses.cfg ${newlai}licenses.cfg");
runcmd("cp ${oldlai}schedule.cfg ${newlai}schedule.cfg");
runcmd("cp ${oldlai}system.cfg ${newlai}system.cfg");
runcmd("cp ${oldlai}preferences.cfg ${newlai}preferences.cfg");
runcmd("cp ${oldlai}roles_enterprise.cfg ${newlai}roles_enterprise.cfg");
runcmd("cp ${oldlai}roles_standard.cfg ${newlai}roles_standard.cfg");
runcmd("rm ${newlai}profiles/*");
runcmd("cp ${oldlai}profiles/* ${newlai}profiles");
runcmd("cp ${oldlai}rewrite_rules/* ${newlai}rewrite_rules");
runcmd("cp -r ${oldlai}users_cache/* ${newlai}users_cache");
runcmd("rm -fr ${newlai}Databases");
runcmd("rsync -av ${oldlai}Databases/* ${newlai}Databases");
