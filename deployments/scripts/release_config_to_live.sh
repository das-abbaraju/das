#!/bin/sh
backup_dir=/var/backups/
filename="pics_config_tbls.sql"
deploy_dir=${backup_dir}deployments/
cfg_file=${deploy_dir}config_tables.txt
dumpfile=${backup_dir}${filename}
dumpfile2=${dumpfile}.gz
dumpdb="pics_config"
dbname="pics"

exec<$cfg_file
while read line
do
cfg_tbls="${cfg_tbls} $line"
done


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

