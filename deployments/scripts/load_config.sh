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
