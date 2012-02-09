#!/bin/bash
FIND="/usr/bin/find"
XARGS="/usr/bin/xargs"
LOG_DIR="/var/log/contractor"
TYPE="-type f"
TIME="-mtime +5"
NAME="-iname *.log.*"
CMD="rm -f"
$FIND $LOG_DIR $TYPE $NAME $TIME | $XARGS $CMD



