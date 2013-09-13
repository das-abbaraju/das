<%@ page language="java" %>
<%
	String test = request.getParameter("test");
	if (test != null) {
		Cookie userName = new Cookie("testCookie", test);
		userName.setMaxAge(1000);  //session scoped
		response.addCookie(userName);
	}
%>

<html>
<head>
<title>Clients</title>
</head>
<body>
	<table border="1">
	<tr>
		<td>sessionid</td>
		<td>
			1 = <%= session.getId() %><br/>
			2 = <%= request.getRequestedSessionId() %><br/>
		</td>
	</tr>
	<tr>
		<td><a href="http://whatsmyip.org">IP info</a></td>
		<td>
			1 = <%= request.getRemoteHost() %><br/>
			2 = <%= request.getRemoteAddr() %><br/>
			3 = <%= request.getRemoteUser() %><br/>
			
		</td>
	</tr>
	<tr>
		<td>session attributes</td>
		<td>

		<% java.util.Enumeration<String> e = session.getAttributeNames();
		
			while( e.hasMoreElements() ) {
				String name = (String) e.nextElement();%>
				
				<%= name %> =
				 
				<%
				Object o = session.getAttribute( name );
				
				if( o instanceof com.picsauditing.access.Permissions ) {
					com.picsauditing.access.Permissions perms = (com.picsauditing.access.Permissions) o;
					%>
					<%= perms.getUsername()%>
					<%	
				}
				else {
					%>
					<%= o %>
					<%	
				}
				%><br/><%
			}
		%>
		</td>
	</tr>
	<tr>
		<td>cookies</td>
		<td>
			<%
			javax.servlet.http.Cookie[] cookies = request.getCookies();
			if( cookies != null ) {
			
				for( javax.servlet.http.Cookie cookie : request.getCookies() ) {
			%>
				<%= cookie.getName() %> = <%= cookie.getValue() %><br/>					
			<%					
				}
			}
			%>
		</td>
	</tr>
	<tr>
		<td>headers</td>
		<td>

		<% e = request.getHeaderNames();
		
			while( e.hasMoreElements() ) {
				String name = (String) e.nextElement();%>
				
				<%= name %> = <%= request.getHeader( name ) %><br/>
			<% }
		%>
		</td>
	</tr>
	
	</table>
	
	


</body>
</html>
						