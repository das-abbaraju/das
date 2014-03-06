This directory contains everything you need to load and refresh a bootstrap (skeleton) database on your personal machine.
The scripts in here assume 2 things.
1.  You have MySQL 5.5 installed and do not need to specify a user & pass to log into your MySQL server.  Either because you not securied your server installation or you have specified your user & password in ~/.my.cnf
2.  That your MySQL server is actually running.  While there are some sanity checks to make sure you have the databases set up and users set up, it's not verifying that mysql is running, though I guess it would technically error out with a "cannot connect to localhost" error.

The only script that you need to be concerned with is the load_db.sh.
Make sure that you have added the options that are in my.cnf file to your machine's my.cnf file and that the options are being picked up by the server.

Currently, this script has only been tested on Linux and MacOSX.  Theoretically since it's a simple bash script, it *should* work if you have git bash installed on your Windows machine.
