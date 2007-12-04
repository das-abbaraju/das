<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*,java.text.*,java.util.*, com.google.gdata.client.*, com.google.gdata.client.calendar.*,com.google.gdata.data.*, com.google.gdata.data.extensions.*, com.google.gdata.util.*" errorPage="" %>
<%
	java.net.URL postUrl =
	  new java.net.URL("http://www.google.com/calendar/feeds/default/private/full");
	EventEntry myEntry = new EventEntry();
	
	myEntry.setTitle(new PlainTextConstruct("Tennis with Beth"));
	myEntry.setContent(new PlainTextConstruct("Meet for a quick lesson."));
	
	Person author = new Person("Jo March", null, "jo@gmail.com");
	myEntry.getAuthors().add(author);
	
	DateTime startTime = DateTime.parseDateTime("2006-04-17T15:00:00-08:00");
	DateTime endTime = DateTime.parseDateTime("2006-04-17T17:00:00-08:00");
	When eventTimes = new When();
	eventTimes.setStartTime(startTime);
	eventTimes.setEndTime(endTime);
	myEntry.addTime(eventTimes);
	
	CalendarService myService =
	  new CalendarService("exampleCo-exampleApp-1");
	myService.setUserCredentials("jo@gmail.com", "mypassword");
	
	// Send the request and receive the response:
	EventEntry insertedEntry = myService.insert(postUrl, myEntry);
%>
<html>
<head>
<title>Registrar Contacto</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body text="#666666" link="#666666" vlink="#666666" alink="#666666" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

</body>
</html>
