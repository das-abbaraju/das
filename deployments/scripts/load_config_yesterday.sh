#!/bin/sh
# Run from cobalt nightly

. main.sh

download () {
	echo "downloading $1.gz..."
	download_file=${backup_dir}$l.gz
	/usr/bin/scp tallred@db1.picsauditing.com:$download_file $download_file

	echo "copying $1.gz to tmp..."
	/bin/cp -f $download_file /tmp/$1.gz
	
	echo "unzipping $1.gz"
	/bin/gunzip /tmp/$1.gz
}

download $pics_live_sql
download $pics_config_sql

echo drop tables in pics_yesterday
/var/backups/drop_tables.sh pics_yesterday
/var/backups/drop_tables.sh pics_yesterday
/var/backups/drop_tables.sh pics_yesterday
/var/backups/drop_tables.sh pics_yesterday

echo "loading into pics_config"
/usr/bin/mysql $db_yesterday < /tmp/$pics_config_sql
/usr/bin/mysql $db_yesterday < /tmp/$pics_live_sql
/usr/bin/mysql $db_config < /tmp/$pics_live_sql

echo "cleanup"
/bin/rm -f /tmp/$pics_config_sql
/bin/rm -f /tmp/$pics_live_sql

echo "done"

