<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="net.sf.ehcache.CacheManager"%>
<%@page import="net.sf.ehcache.Cache"%>
<%@page import="net.sf.ehcache.Statistics"%>
<%@page import="java.util.List"%>
<%@page import="net.sf.ehcache.Element"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cache Info</title>
<link rel="stylesheet" href="css/reports.css"/>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
	$('#web1').load('http://web1.picsorganizer.com/monitorAjax.jsp');
	$('#web2').load('http://web2.picsorganizer.com/monitorAjax.jsp');
});
</script>
</head>
<body>
<h2>EhCache Statistics</h2>

<table>
	<tr>
		<td id="web1">
			
		</td>
		<td style="width: 20px;"></td>
		<td id="web2">
		
		</td>
	</tr>
</table>

</body>
</html>
