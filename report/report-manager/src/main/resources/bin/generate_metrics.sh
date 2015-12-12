#!/bin/bash

if [ $# -lt 1 ]; then
    echo "USAGE: generate_metrics.sh <YYYY-MM> [<node>]"
    exit
fi

date=$1
url=pds-rpt
REP_HOME=/report_service/reports
NODES="all_node atm en img rings ppi rings sbn sbn_umd sbn_psi naif geo"

mkdir -p $date
if [ $# -eq 2 ]; then
	node=$2
	rsync -av pds-rpt:${REP_HOME}/${node}/$date/*_hq_domain_report_*.csv ${date}/
	pushd ${date}
	if [ -f ${node}_hq_domain_report_0.csv ]; then
		mv ${node}_hq_domain_report_0.csv ${node}_hq_domain_report_usa_${date}.csv
		open ${node}_hq_domain_report_usa_${date}.csv
		mv ${node}_hq_domain_report_1.csv ${node}_hq_domain_report_intl_${date}.csv
		open ${node}_hq_domain_report_intl_${date}.csv
	fi
else
	rsync -av pds-rpt:${REP_HOME}/*/${date}/*_hq_domain_report_*.csv ${date}/
	pushd ${date}
	for node in $NODES; do
	    if [ -f ${node}_hq_domain_report_0.csv ]; then
			mv ${node}_hq_domain_report_0.csv ${node}_hq_domain_report_usa_${date}.csv
			open ${node}_hq_domain_report_usa_${date}.csv
			mv ${node}_hq_domain_report_1.csv ${node}_hq_domain_report_intl_${date}.csv
			open ${node}_hq_domain_report_intl_${date}.csv
	    fi
	done
fi

basedir=$(dirname $0)
python $basedir/rename_reports.py $date
popd

exit 0