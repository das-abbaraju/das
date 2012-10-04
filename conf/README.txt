// 1) Replace the following from your Servers / Tomcat / server.xml
			<Context docBase="picsWeb2" path="/picsWeb2" reloadable="true" source="org.eclipse.jst.j2ee.server:picsWeb2"/></Host>
// with this:
				<Context docBase="PICSORG" path="/" reloadable="false"
					source="org.eclipse.jst.j2ee.server:PICSORG">
					<Resource name="jdbc/pics" auth="Container" type="javax.sql.DataSource"
						maxActive="60" maxIdle="30" maxWait="10000" removeAbandoned="true" minIdle="10"
						validationQuery="SELECT 1" validationInterval="30000" removeAbandonedTimeout="20" 
						driverClassName="com.mysql.jdbc.Driver" logAbandoned="true" username="pics" 
						password="M0ckingj@y" url="jdbc:mysql://cobalt.picsauditing.com:3306/pics_alpha1" />
				</Context>
			</Host>

// 2) Add the following to the end of your server VM arguments:
// (To navigate to the VM arguments: Run-->Open Run Dialog-->Arguments.VMArguments)

	-XX:MaxPermSize=256m
	-Dpics.debug=1
	// This is for the session cookie
	-Dsk="9KuRXTx0cnuZefrt0EIfXd1MFqKvMY9x7OSub0B1EGLpR69b1Z+sdB7p6PT3Sy5rhl6qXKYyINdPJoHMWCqBNQ=="
	-Dpics.autoLogin=941

// 3) Restart Tomcat.

/***********************/
			
// If you are going to use RabbitMQ and the default settings in resources/amqp_connection.properties, you can alternately set these
// as a JNDI Resource in the Context as:
					<Resource auth="Container" name="amqp/pics" type="org.springframework.amqp.rabbit.connection.CachingConnectionFactory"
					factory="com.picsauditing.jndi.RabbitMqInitialContextFactory" host="HOST" username="USERNAME" password="PASSWORD"/>

// To enable persistent tomcat database sessions, replace this line from context.xml
	<Manager pathname="" />
// With this
	<Manager className="org.apache.catalina.session.PersistentManager"
		saveOnRestart="false" minIdleSwap="-1" maxIdleSwap="10" maxIdleBackup="0">
		<Store className="org.apache.catalina.session.JDBCStore"
			driverName="com.mysql.jdbc.Driver"
			connectionURL="jdbc:mysql://cobalt.picsauditing.com:3306/pics_alpha1?user=pics&amp;password=M0ckingj@y"
			sessionTable="app_session" sessionIdCol="id" sessionDataCol="sessionData"
			sessionValidCol="validSession" sessionMaxInactiveCol="maxInactiveInterval"
			sessionLastAccessedCol="lastAccessedTime" sessionAppCol="appName" />
	</Manager>
// And copy .m2\repository\mysql\mysql-connector-java\x.x.x\mysql-connector-java-x.x.x.jar to the tomcat lib folder