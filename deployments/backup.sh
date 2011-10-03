#!/bin/bash
# General MySQL backup utility for PICS

# General Settings
backup_dir=/var/backups/

pics_live_sql=pics.sql
pics_config_sql=pics_config.sql
db_live=pics
db_config=pics_config
db_config_released=pics_config_released
db_yesterday=pics_yesterday

MYSQL=$(which mysql)
MYSQL="/usr/bin/mysql -uroot"
AWK=$(which awk)
GREP=$(which grep)

# Functions

#  dumps the contents of "pics" database WITHOUT config tables and compresses the sql file
#  every week and every month this copies the backup file so you will
#  always have a copy less than a day old, week old, and month old
#
get_config_tables ()
{
	cfg_file=${backup_dir}deployments/config_tables.txt
	ign_tbls=""
	cfg_tbls=""
	dos2unix $cfg_file
	exec<$cfg_file
	while read line
	do
	ign_tbls="${ign_tbls} --ignore_table ${db_live}.${line}"
	cfg_tbls="${cfg_tbls} $line"
	done
}

cleanup () {
	echo "cleanup temp files"
	/bin/rm -f /tmp/pics*
}

dump_database () {
	# Usage: dump_database pics_config 1
	# Usage: dump_database pics
	database=$1
	get_config_tables
	
	case "$2" in
		1)
			echo "Dumping only config tables from $1"
			/usr/bin/mysqldump --opt $database $cfg_tbls > ${backup_dir}$pics_config_sql
			;;
		*)
			echo "Dumping all non-config tables from $1"
			/bin/cat /var/backups/restore_prepend > $filename
			/usr/bin/mysqldump --opt $database $ign_tbls >> ${backup_dir}$pics_live_sql
			/bin/cat /var/backups/restore_append >> $filename
			;;
	esac
}

backup_config () {
	dump_database $db_config 1
	/bin/mv ${backup_dir}${pics_config_sql} ${backup_dir}config/
	
	echo "Compressing ${backup_dir}config/${pics_config_sql}"
	/bin/gzip -f ${backup_dir}config/${pics_config_sql}
}

backup_live () {
	filename=${backup_dir}$pics_live_sql
	#Backup pics db without config tables
	dump_database $db_live
	
	#Backup pics db config tables only
	dump_database $db_live 1
	
	/bin/gzip -f $filename
	/bin/gzip -f ${backup_dir}$pics_config_sql
	
	if [ `date +%a` = "Mon" ]; then
	   cp $filename.gz ${filename}w.gz
	fi
	
	if [ `date +%d` = "01" ]; then
	   cp $filename.gz ${backup_dir}archive/pics`date +"%Y%m%d"`.sql.gz
	fi
}

load_config () {
	dumpfile=${backup_dir}$pics_config_sql
	dumpdb="pics_config"
	dbname=$1
	if [ "$dbname" = "" ]
	then
		echo "Usage: $0 load_config TARGET_DATABASE"
		echo "Example: $0 load_config pics_alpha1"
		exit 1
	fi
	
	dump_database $dumpdb 1
	
	echo "Importing config tables to $dbname"
	/usr/bin/mysql $dbname < $dumpfile
	
	echo "Cleaning up dump files"
	/bin/rm $dumpfile
}

download () {
	file=$1
	echo "downloading $file.gz..."
	download_file=${backup_dir}${file}.gz
	echo "download_file = $download_file"
	/usr/bin/scp tallred@db1.picsauditing.com:$download_file $download_file

	echo "copying $file.gz to tmp..."
	/bin/cp -f $download_file /tmp/$file.gz
	
	echo "unzipping $file.gz"
	/bin/gunzip /tmp/$file.gz
}

drop_tables () {
	#Usage: $0 database_name
	database="$1"
	
	for i in {1..4}
	do
		TABLES=$($MYSQL $database -e 'show tables' | $AWK '{ print $1}' | $GREP -v '^Tables' )
	
		for t in $TABLES
		do
		        echo "Dropping $t table from $database database..."
		        $MYSQL -e "drop table $database.$t"
		done
	done
}

load_config_yesterday () {
	download $pics_live_sql
	download $pics_config_sql
	
	drop_tables pics_yesterday
	
	echo "loading into pics_yesterday"
	/usr/bin/mysql $db_yesterday < /tmp/$pics_config_sql
	/usr/bin/mysql $db_yesterday < /tmp/$pics_live_sql
	echo "loading into pics_config"
	/usr/bin/mysql $db_config < /tmp/$pics_live_sql
	/usr/bin/mysql $db_config < ${backup_dir}deployments/clean_config.sql
	
	cleanup
}

release_config_to_live () {
	dumpfile=${backup_dir}$pics_config_sql
	
	dump_database $db_config 1
	
	drop_tables $db_config_released
	/usr/bin/mysql $db_config_released < $dumpfile

	echo "Compressing config tables dump file"
	/usr/bin/gzip -f $dumpfile
	
	echo "Sending config tables to db1"
	/usr/bin/scp $dumpfile.gz tallred@db1.picsauditing.com:$dumpfile.gz
	
	echo "uncompressing config tables dumpfile on db1"
	/usr/bin/ssh tallred@db1.picsauditing.com "sh ${backup_dir}deployments/backup.sh load_config_to_live"

	echo "Cleaning up dump files"
	/bin/rm $dumpfile.gz
}

load_config_to_live () {
	dumpfile=${backup_dir}$pics_config_sql

	echo "uncompressing config tables dumpfile on db1"
	/usr/bin/gunzip $dumpfile.gz
	
	echo "Importing config tables to live"
	/usr/bin/mysql $db_live < $dumpfile
	
	echo "clear cache"
	/usr/bin/mysql $db_live -e "UPDATE app_properties SET value = '1' WHERE property = 'PICS.clear_cache'"
	
	echo "updating configupdatedate on live"
	# we're accounting for the time difference between Houston and Irvine -- The time set reflects PST
	/usr/bin/mysql $db_live -e "UPDATE app_properties SET value = DATE_SUB(NOW(), INTERVAL 2 HOUR) WHERE property = 'ConfigUpdateDate'"
	
	echo "Rezip the config data"
	/bin/gzip -f $dumpfile
}


case "$1" in
  backup_config)
    # Dump pics_config config tables and import into specified database
    # Run from cobalt when we release (ie every Monday/Thursday)
    backup_config
        ;;
  backup_live)
    # Currently scheduled for every night at 2am Central
    # Another process copies these files to cobalt later in the morning
    backup_live
        ;;
  load_config)
        #Dump pics_config config tables and import into specified database
        load_config $2
        ;;
  load_config_yesterday)
        # Run from cobalt nightly
        load_config_yesterday
        ;;
  load_config_to_live)
        load_config_to_live
        ;;
  release_config_to_live)
        # Run this on cobalt
        # Targetted ad hoc run for Monday and Thurday afternoon
        release_config_to_live
        ;;
  *)
        echo $"Usage (on live): $0 {backup_live|load_config_to_live}"
        echo $"Usage (on cobalt): $0 {backup_config|load_config DB|load_config_yesterday|release_config_to_live}"
        exit 1
esac

echo "done"

exit $?
