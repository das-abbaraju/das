#!/bin/bash
export CATALINA_OPTS="-Dpics.env=localhost -Dtranslation.server=https://acoustickitty.picsorganizer.com -Xms1536m -Xmx1536m -XX:MaxPermSize=512m -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -Dsk=9KuRXTx0cnuZefrt0EIfXd1MFqKvMY9x7OSub0B1EGLpR69b1Z+sdB7p6PT3Sy5rhl6qXKYyINdPJoHMWCqBNQ== -Dloglevel=INFO -Dloglevel_report=INFO -Dloglevel_timing=INFO -Dpics.ftpDir=/var/pics/alpha/www_files -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=5400 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=true -Dcom.sun.management.jmxremote.password.file=${CATALINA_HOME}/conf/jmxremote.password -Dcom.sun.management.jmxremote.access.file=${CATALINA_HOME}/conf/jmxremote.access -Djava.library.path=/usr/local/Cellar/tomcat-native/1.1.29/lib""
