#!/bin/sh
backup_dir=/var/backups/
deploy_dir=${backup_dir}deployments/scripts/

cfg_file=${backup_dir}deployments/config_tables.txt

getConfigTables () {
	echo "Calling getConfigTables"
}