#!/bin/bash

if [ $# -lt 1 ]; then
    echo "enter date YYYY-MM"
    exit
fi

date=$1
url=pds-rpt
REP_HOME=/report_service/reports
NODES="atm en img rings ppi rings sbn_umd sbn_psi naif geo"

mkdir -p $date
rsync -av pds-rpt:${REP_HOME}/*/${date}/*_hq_domain_report_*.csv ${date}/
cd ${date}
for node in $NODES; do
    if [ -f ${node}_hq_domain_report_0.csv ]; then
	mv ${node}_hq_domain_report_0.csv ${node}_hq_domain_report_usa_${date}.csv
	open ${node}_hq_domain_report_usa_${date}.csv
	mv ${node}_hq_domain_report_1.csv ${node}_hq_domain_report_intl_${date}.csv
	open ${node}_hq_domain_report_intl_${date}.csv
    fi
done

basedir=$(dirname $0)
python $basedir/rename_reports.py $date

exit 0