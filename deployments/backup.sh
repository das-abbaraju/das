#!/bin/bash
# General MySQL backup utility for PICS

# General Settings
backup_dir=/var/backups/

pics_live_sql=pics.sql
pics_config_sql=pics_config.sql
db_live=pics
db_config=pics_config
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
	dos2unix $cfg_file
	exec<$cfg_file
	while read line
	do
	ign_tbls="${ign_tbls} --ignore_table ${db_live}.${line}"
	cfg_tbls="${cfg_tbls} $line"
	done
}
get_config_tables

cleanup () {
	echo "cleanup temp files"
	/bin/rm -f /tmp/pics*
}

dump_database () {
	# Usage: dump_database pics_config 1
	# Usage: dump_database pics
	database=$1
	
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
	
	echo "Compressing ${backup_dir}${pics_config_sql}"
	/bin/gzip -f ${backup_dir}${pics_config_sql}
}

backup_live () {
	filename=${backup_dir}pics.sql
	
	filename_cfg=${backup_dir}pics_config.sql
	
	#Backup pics db without config tables
	dump_database $db_live
	
	/bin/gzip -f $filename
	
	#Backup pics db config tables only
	dump_database $db_live 1
	
	/bin/gzip -f $filename_cfg
	
	wfilename=${backup_dir}pics.sql.gz
	mfilename=${backup_dir}pics.sql.gz
	
	if [ `date +%a` = "Mon" ]; then
	   cp $filename.gz $wfilename
	fi
	
	if [ `date +%d` = "01" ]; then
	   cp $filename.gz $mfilename
	fi
}

load_config () {
	dumpfile=${backup_dir}$pics_config_sql
	dumpdb="pics_config"
	dbname=$2
	
	if [ "$dbname" = "" ]
	then
		echo "Usage: $0 $1 TARGET_DATABASE"
		echo "Example: $0 $1 pics_alpha1"
		exit 1
	fi
	
	dump_database $dumpdb 1
	
	echo "Importing config tables to $dbname"
	/usr/bin/mysql $dbname < $dumpfile
	
	echo "Cleaning up dump files"
	/bin/rm $dumpfile
}

download () {
	echo "downloading $1.gz..."
	download_file=${backup_dir}$l.gz
	/usr/bin/scp tallred@db1.picsauditing.com:$download_file $download_file

	echo "copying $1.gz to tmp..."
	/bin/cp -f $download_file /tmp/$1.gz
	
	echo "unzipping $1.gz"
	/bin/gunzip /tmp/$1.gz
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
	
	cleanup
}

release_config_to_live () {
	filename="pics_config_tbls.sql"
	deploy_dir=${backup_dir}deployments/
	dumpfile=${backup_dir}$pics_config_sql
	dumpdb="pics_config"
	dbname="pics"
	
	dump_database $db_config 1
	
	drop_tables pics_config_release
	/usr/bin/mysql pics_config_release < $dumpfile

	echo "Compressing config tables dump file"
	/usr/bin/gzip -f $dumpfile
	
	echo "Sending config tables to db1"
	/usr/bin/scp $dumpfile.gz tallred@db1.picsauditing.com:$dumpfile.gz
	
	echo "uncompressing config tables dumpfile on db1"
	/usr/bin/ssh tallred@db1.picsauditing.com /usr/bin/gunzip $dumpfile.gz
	
	echo "Importing config tables to live"
	/usr/bin/ssh tallred@db1.picsauditing.com '/usr/bin/mysql $dbname < $dumpfile'
	
	echo "Cleaning up dump files"
	/bin/rm $dumpfile.gz
	/usr/bin/ssh tallred@db1.picsauditing.com /bin/rm $dumpfile
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
        load_config
        ;;
  load_config_yesterday)
        # Run from cobalt nightly
        load_config_yesterday
        ;;
  release_config_to_live)
        # Run this on cobalt
        # Targetted ad hoc run for Monday and Thurday afternoon
        release_config_to_live
        ;;
  *)
        echo $"Usage: $0 {backup_config|backup_live|load_config|load_config_yesterday|release_config_to_live}"
        exit 1
esac

echo "done"

exit $?
