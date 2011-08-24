backup_dir=/var/backups/

pics_config_sql=pics_config.sql
db_live=pics
db_config=pics_config
db_yesterday=pics_yesterday

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

