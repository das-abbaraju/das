#!/bin/bash
#
# Dump pics_config config tables and import into specified database
# Run from cobalt when we release (ie every Monday/Thursday)

. main.sh

echo "Dumping pics_config config tables"
/usr/bin/mysqldump $db_config $cfg_tbls > ${backup_dir}${pics_config_sql}
echo "Compressing ${backup_dir}${pics_config_sql}"
/bin/gzip -f ${backup_dir}${pics_config_sql}

echo "done"

