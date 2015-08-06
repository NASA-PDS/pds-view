#!/bin/bash

if [ $# -lt 3 ]; then
    echo "USAGE: build_sbn_report <start date> <end date> <dir date>"
    echo "Ex: build_sbn_report 01/Jul/2015 31/Jul/2015 2015-08" 
    exit
fi

start_date=$1
end_date=$2
output_dir=/report_service/reports/sbn/$3

# Check that output dir exists
if [ ! -d ${output_dir} ]; then
	echo "The output directory ${output_dir} does not exist"
	exit
fi

/usr/local/report/sawmill -p sbn_web -a ud
/usr/local/report/sawmill -p sbn_web -a ect -rn sbn_hq_domain_report -et true -er -1 -od ${output_dir} -df ${start_date} - ${end_date}
