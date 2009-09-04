<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" href="js/jquery/weekcalendar/jquery.weekcalendar.css">
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery/weekcalendar/jquery.weekcalendar.js.min.js"></script>
<script type="text/javascript">
$(function(){
	$('#calendar').weekCalendar({
			firstDayOfWeek: 1,
			timeslotHeight: 40,
			timeslotsPerHour: 2,
			defaultEventLength: 4
			
		});
});
</script>
</head>
<body>
<div id="calendar_wrapper">
<div id="calendar"></div>
</div>
</body>
</html>