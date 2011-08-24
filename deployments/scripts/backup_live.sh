#!/bin/sh
# MySQL backup script for db1 on LIVE
#  dumps the contents of "pics" database WITHOUT config tables and compresses the sql file
#  every week and every month this copies the backup file so you will
#  always have a copy less than a day old, week old, and month old
#
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

