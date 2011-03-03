#!/bin/bash

if [ "$1" == "" ] || [ "$2" == "" ]; then
    echo
    echo "Parameters required: Need to include MySQL username and password"
    echo "                     with access to report_service DB."
    echo
    echo "::: ./transferLogs.sh <username> <password> :::" 
    echo
    exit 0
fi

user=$1
pass=$2

# Create log file for information
log=transfer.`date +%Y%m%d`.log
rm -f $log

# Get Log Home from env_vars.xml
log_home=`egrep log_dest ./env_vars.xml | awk -F\< '{print $2}' | awk -F\> '{print $2}'`

# Query Mysql DB for profile and log_set information
mysql -u $user -p$pass report_service -B -e "select p.node, p.name, ls.label, ls.hostname,ls.username, ls.pathname from profiles p, log_sets ls where ls.active_flag='y' and p.active_flag='y' and p.profile_id=ls.profile_id" >> $log
mysql -u $user -p$pass report_service --skip-column-names -B -e "select p.node, p.name, ls.label, ls.hostname,ls.username, ls.pathname from profiles p, log_sets ls where ls.active_flag='y' and p.active_flag='y' and p.profile_id=ls.profile_id" > temp.txt

while read line; do
    node=`echo "${line}" | awk '{print $1}'`
    name=`echo "${line}" | awk '{print $2}'`
    label=`echo "${line}" | awk '{print $3}'`
    
    hostname=`echo "${line}" | awk '{print $4}'`
    username=`echo "${line}" | awk '{print $5}'`
    pathname=`echo "${line}" | awk '{print $6}'`
    echo >> $log
    echo >> $log
    echo "Copying from: $username@$hostname:$pathname" >> $log
    echo "Copying to: $log_home/$node/$name/$label" >> $log
    echo >> $log
    rsync -rv --ignore-existing -e ssh $username@$hostname:$pathname $log_home/$node/$name/$label >> $log
    echo >> $log
    echo "-----" >> $log
done < temp.txt

rm temp.txt

exit 0