// Add this to the end of your server VM arguments
// Run...Open Run Dialog...Arguments.VMArguments
// Then restart Tomcat
	-XX:MaxPermSize=256m
	-Dpics.debug=1
	-Dpics.autoLogin=941
	
// Replace this from your Servers / Tomcat / server.xml
			<Context docBase="picsWeb2" path="/picsWeb2" reloadable="true" source="org.eclipse.jst.j2ee.server:picsWeb2"/></Host>
// With this
				<Context docBase="PICSORG" path="/" reloadable="false"
					source="org.eclipse.jst.j2ee.server:PICSORG">
					<Resource name="jdbc/pics" auth="Container" type="javax.sql.DataSource"
						maxActive="60" maxIdle="30" maxWait="10000" removeAbandoned="true" minIdle="10"
						validationQuery="SELECT 1" validationInterval="30000" removeAbandonedTimeout="20" 
						driverClassName="com.mysql.jdbc.Driver" logAbandoned="true" username="pics" 
						password="M0ckingj@y" url="jdbc:mysql://cobalt.picsauditing.com:3306/pics_alpha1" />
				</Context>
			</Host>

			
			