// Add this to the end of your server VM arguments
// Run...Open Run Dialog...Arguments.VMArguments
// Then restart Tomcat
	-Dpics.autoLogin=941
	-Dpics.debug=1

// Replace this from your Servers / Tomcat / server.xml
			<Context docBase="picsWeb2" path="/picsWeb2" reloadable="true" source="org.eclipse.jst.j2ee.server:picsWeb2"/></Host>
// With this
				<Context docBase="picsWeb2" path="/picsWeb2"
					reloadable="true" source="org.eclipse.jst.j2ee.server:picsWeb2">
					<Resource name="jdbc/pics" auth="Container"
						type="javax.sql.DataSource" maxActive="60" maxIdle="30"
						maxWait="10000" removeAbandoned="true" removeAbandonedTimeout="20"
						driverClassName="com.mysql.jdbc.Driver" logAbandoned="true"
						username="pics" password="pics"
						url="jdbc:mysql://alpha.picsauditing.com:3306/pics_alpha" />
				</Context>
			</Host>
