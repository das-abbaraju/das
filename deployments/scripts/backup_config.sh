#!/bin/sh
#
# Dump pics_config config tables and import into specified database
# Run from cobalt when we release (ie every Monday/Thursday)

backup_dir=/var/backups/
filename="pics_config.sql"
deploy_dir=${backup_dir}deployments/
cfg_file=${deploy_dir}config_tables.txt
cfg_tbls=

dumpfile=${backup_dir}${filename}
dumpdb="pics_config"

exec<$cfg_file
while read line
do
cfg_tbls="${cfg_tbls} $line"
done

echo "Dumping pics_config config tables"
/usr/bin/mysqldump $dumpdb $cfg_tbls > $dumpfile

echo "done"
