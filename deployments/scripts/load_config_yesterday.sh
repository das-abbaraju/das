#!/bin/sh
backup_dir=/var/backups/
deploy_dir=${backup_dir}deployments/
cfg_file=${deploy_dir}config_tables.txt
dbname=pics_config
dbname2=pics_yesterday
filename=pics_no_cfg_tbls.sql.gz
filename2=pics_cfg_tbls.sql.gz
dlfile=${backup_dir}${filename}
dlfile2=${backup_dir}${filename2}
tmpfile=/tmp/${filename}
tmpfile2=/tmp/${filename2}
sqltmpfile=/tmp/pics_no_cfg_tbls.sql
sqltmpfile2=/tmp/pics_cfg_tbls.sql

echo "downloading pics_config.sql.gz..."
/usr/bin/scp tallred@db1.picsauditing.com:$dlfile $dlfile
/usr/bin/scp tallred@db1.picsauditing.com:$dlfile2 $dlfile2

echo "copying pics.sql.gz..."
/bin/cp -f $dlfile $tmpfile 
/bin/cp -f $dlfile2 $tmpfile2 

echo "unzipping /tmp/sql.gz"
/bin/gunzip $tmpfile 
/bin/gunzip $tmpfile2 

echo drop tables in pics_yesterday
/var/backups/drop_tables.sh pics_yesterday
/var/backups/drop_tables.sh pics_yesterday
/var/backups/drop_tables.sh pics_yesterday
/var/backups/drop_tables.sh pics_yesterday

echo "loading into pics_config"
/usr/bin/mysql $dbname < $sqltmpfile 
/usr/bin/mysql $dbname2 < $sqltmpfile
/usr/bin/mysql $dbname2 < $sqltmpfile2

echo "cleanup"
/bin/rm -f $sqltmpfile 
/bin/rm -f $sqltmpfile2 

echo "done"

