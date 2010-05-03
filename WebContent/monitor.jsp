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
</head>
<body>
<h2>EhCache Statistics</h2>

<table class="report">
	<tr>
		<td><strong>Cache Name</strong></td>
		<td><strong>Size</strong></td>
		<td><strong>Hits</strong></td>
		<td><strong>Misses</strong></td>
	</tr>

<%
CacheManager.create();
String[] cacheNames = CacheManager.getInstance().getCacheNames();

for( String cn : cacheNames )
{
	Cache cache = CacheManager.getInstance().getCache(cn);

	Statistics stats = cache.getStatistics();
	%>
	<tr>
		<td><%= cn %></td>
		<td><%= stats.getObjectCount() %></td>
		<td><%= stats.getCacheHits() %>
		<br />M:<%= stats.getInMemoryHits() %>
		<br />D:<%= stats.getOnDiskHits() %>
		</td>
		<td><%= stats.getCacheMisses() %></td>
	</tr>
<% 	} %>
</table>

</body>
</html>
