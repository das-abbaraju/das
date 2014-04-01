#!/bin/bash
####################################################################
# This script will load the databases for you.  It's pretty awesome
# This script assumes that you have mysql server running and ready
# to go.  This script also assumes that your personal user or root
# user is either passwordless or you have the creditials for your 
# personal account set up in ~/.my.cnf 
####################################################################

# Command Vars
MYSQL=`which mysql`
ECHO=`which echo`
GREP=`which grep`
DATE="/bin/date"

## Sanity check commands
check_bootstrap=`$MYSQL -e "show databases"|$GREP pics_bootstrap`
check_log_archive=`$MYSQL -e "show databases"|$GREP log_archive`
## Commented out the translation database check for the time being
#check_translations=`$MYSQL -e "show databases"|$GREP pics_translations`
check_pics_user=`$MYSQL -D mysql -e "select user from user"|$GREP pics`
check_picsro_user=`$MYSQL -D mysql -e "select user from user"|$GREP picsro`
## Commented out the translation user for the time being
#check_trans_user=`$MYSQL -D mysql -e "select user from user"|$GREP pics_translations`
check_pics_admin_user=`$MYSQL -D mysql -e "select user from user"|$GREP pics_admin`

# File variables
bootstrap_dump="pics_bootstrap.sql"
archive_dump="log_archive_schema.sql"
users="local_users_and_permissions.sql"
dbs="create_databases.sql"
archive_db="log_archive"
bootstrap_db="pics_bootstrap"
# Command Vars


function sanity_check {
	if [[ -z "$check_bootstrap" ]]; then
		$ECHO "Please run $0 create_databases"
		exit
	elif [[ -z "$check_log_archive" ]]; then 
		$ECHO "Please run $0 create_databases"
		exit
	elif [[ -z "$check_pics_user" ]]; then 
		$ECHO "Please run $0 create_users"
		exit
	elif [[ -z "$check_picsro_user" ]]; then 
		$ECHO "Please run $0 create_users"
		exit
	elif [[ -z "$check_pics_admin_user" ]]; then 
		$ECHO "Please run $0 create_users"
		exit
	fi
}

function create_databases {
	$ECHO "Creating Databases"
	$MYSQL < $dbs
	$ECHO "Databases have been created"
}

function create_users {
	$ECHO "Creating Users"
	$MYSQL < $users
	$ECHO "Users have been created"
}

function load_databases {
	$ECHO "Loading $archive_db $(date +"%b %d %H:%M:%S")  \n"
	$MYSQL $archive_db < $archive_dump
	$ECHO "Done loading $archive_db $(date +"%b %d %H:%M:%S") \n"
	$ECHO "Loading $bootstrap_db $(date +"%b %d %H:%M:%S") \n"
	$MYSQL $bootstrap_db < $bootstrap_dump
	$ECHO "Done loading $bootstrap_db $(date +"%b %d %H:%M:%S") \n"
}

case "$1" in
	create_databases)
		create_databases
	;;
	create_users)
		create_users
	;;
	load_databases)
		sanity_check
		load_databases
	;;
	*)
		$ECHO "Please enter an argument $0 { create_databases | create_users | load_databases }"
esac
exit $?
