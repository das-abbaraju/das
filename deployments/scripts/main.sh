#!/bin/bash
# General MySQL backup utility for PICS

# General Settings
backup_dir=/var/backups/

pics_live_sql=pics.sql
pics_config_sql=pics_config.sql
db_live=pics
db_config=pics_config
db_yesterday=pics_yesterday

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

backup_config () {

	# Dump pics_config config tables and import into specified database
	# Run from cobalt when we release (ie every Monday/Thursday)

echo "Dumping pics_config config tables"
/usr/bin/mysqldump $db_config $cfg_tbls > ${backup_dir}${pics_config_sql}
echo "Compressing ${backup_dir}${pics_config_sql}"
/bin/gzip -f ${backup_dir}${pics_config_sql}

echo "done"

}


# Usage: backup_db.sh
# Currently scheduled for every night at 2am Central

# Another process copies these files to cobalt later in the morning

. main.sh

filename=${backup_dir}pics.sql
wfilename=${backup_dir}pics.sql.gz
mfilename=${backup_dir}pics.sql.gz

filename_cfg=${backup_dir}pics_config.sql

#Backup pics db without config tables
/bin/cat /var/backups/restore_prepend > $filename
/usr/bin/mysqldump --opt $db_live $ign_tbls >> $filename
/bin/cat /var/backups/restore_append >> $filename
/bin/gzip -f $filename

#Backup pics db config tables only
/usr/bin/mysqldump --opt $db_live $cfg_tbls > $filename_cfg
/bin/gzip -f $filename_cfg

if [ `date +%a` = "Mon" ]; then
   cp $filename.gz $wfilename
fi

if [ `date +%d` = "01" ]; then
   cp $filename.gz $mfilename
fi

#!/bin/bash
#
#Dump pics_config config tables and import into specified database
filename="pics_config_tbls.sql"
dumpfile=${backup_dir}${filename}
dumpdb="pics_config"
dbname=$1

if [ "$1" = "" ]
then
	echo "Usage: ./load_config DATABASENAME"
	echo "Example: ./load_config alpha1"
	exit 1
fi
echo "Dumping pics_config config tables"
/usr/bin/mysqldump $dumpdb $cfg_tbls > $dumpfile

echo "Importing config tables to $dbname"
/usr/bin/mysql $dbname < $dumpfile

echo "Cleaning up dump files"
/bin/rm $dumpfile

echo "done"
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

#!/bin/sh
# Run this on cobalt
# Targetted ad hoc run for Monday and Thurday afternoon

. main.sh

filename="pics_config_tbls.sql"
deploy_dir=${backup_dir}deployments/
dumpfile=${backup_dir}${filename}
dumpfile2=${dumpfile}.gz
dumpdb="pics_config"
dbname="pics"

echo "Dumping pics_config config tables"
/usr/bin/mysqldump $dumpdb $cfg_tbls > $dumpfile

echo "Compressing config tables dump file"
/usr/bin/gzip -f $dumpfile

echo "Sending config tables to db1"
/usr/bin/scp $dumpfile2 tallred@db1.picsauditing.com:$dumpfile2

echo "uncompressing config tables dumpfile on db1"
/usr/bin/ssh tallred@db1.picsauditing.com /usr/bin/gunzip $dumpfile2

echo "Importing config tables to live"
/usr/bin/ssh tallred@db1.picsauditing.com '/usr/bin/mysql $dbname < $dumpfile'

echo "Cleaning up dump files"
/bin/rm $dumpfile2
/usr/bin/ssh tallred@db1.picsauditing.com /bin/rm $dumpfile
echo "done"


case "$1" in
  backup_config)
    backup_config
        ;;
  backup_live)
    backup_live
        ;;
  load_config)
        load_config
        ;;
  load_config_yesterday)
        load_config_yesterday
        ;;
  release_config_to_live)
        release_config_to_live
        ;;
  *)
        echo $"Usage: $0 {backup_config|backup_live|load_config|load_config_yesterday|release_config_to_live}"
        exit 1
esac

exit $?

