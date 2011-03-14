#!/bin/bash
	
if [ "$1" == "" ]; then
    echo
    echo "Parameters required: Need to include decryption password as parameter"
    echo
    echo "::: ./transferLogs.sh <decrypt_password> :::" 
    echo
    exit 0
fi

decrypt_pass=$1

# Create log file for information
log=report_service_update.`date +%Y%m%d`.log
rm -f $log

# Get Log Home from environment.properties
log_home=`egrep sawmill.log.home ./environment.properties | awk -F\= '{print $NF}'`

# Get Sawmill Home from environment.properties
sawmill_home=`egrep sawmill.home ./environment.properties | awk -F\= '{print $NF}'`

# Get MySQL login info from database.properties
user=`egrep datasource.username ./database.properties | awk -F\= '{print $NF}'`
password=`egrep datasource.password ./database.properties | awk -F\( '{print $NF}' | tr -d ')'`
password=`./decrypt.sh input="$password" password=$decrypt_pass verbose=no`

echo "Log Home - $log_home"
echo "MySQL username - $user"

# Query Mysql DB for profile and log_set information
mysql -u $user -p$password pdsbeta.jpl.nasa.gov report_service -e "select p.node, p.name, ls.label, ls.hostname,ls.username, ls.pathname from profiles p, log_sets ls where ls.active_flag='y' and p.active_flag='y' and p.profile_id=ls.profile_id" >> $log
mysql -u $user -p$password pdsbeta.jpl.nasa.gov report_service --skip-column-names -B -e "select p.node, p.name, ls.label, ls.hostname,ls.username, ls.pathname from profiles p, log_sets ls where ls.active_flag='y' and p.active_flag='y' and p.profile_id=ls.profile_id" > temp.txt

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
    echo "Updating Sawmill database" >> $log
    echo "$sawmill_home/sawmill.cgi -p $name -a ud" >> $log
    $sawmill_home/sawmill.cgi -p $name -a ud >> $log
     echo "-----" >> $log
done < temp.txt

rm temp.txt

exit 0