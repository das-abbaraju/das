// Add this to the end of your server VM arguments
// Run...Open Run Dialog...Arguments.VMArguments
// Then restart Tomcat
	-Dpics.autoLogin=941
	-Dpics.debug=1

// Add this to your Servers / Tomcat / server.xml
	<Resource name="jdbc/pics" auth="Container"
		type="javax.sql.DataSource" maxActive="60" maxIdle="30"
		maxWait="10000" removeAbandoned="true" removeAbandonedTimeout="20"
		driverClassName="com.mysql.jdbc.Driver" logAbandoned="true"
		username="pics" password="pics"
		url="jdbc:mysql://alpha.picsauditing.com:3306/pics2?zeroDateTimeBehavior=convertToNull&amp;jdbcCompliantTruncation=false" />
 