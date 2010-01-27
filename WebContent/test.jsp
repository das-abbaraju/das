<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="net.sf.ehcache.CacheManager"%>
<%@page import="net.sf.ehcache.Cache"%>
<%@page import="java.util.List"%>
<%@page import="net.sf.ehcache.Element"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>



This is tomcat: <%=request.getLocalPort()%>
&nbsp;&nbsp;&nbsp;
<a href="http://localhost:8080/picsWeb2/test.jsp">8080</a>
&nbsp;
<a href="http://localhost:8180/picsWeb2/test.jsp">8180</a>
<br />
<br />

<h2>Session Dump&nbsp;&nbsp;&nbsp;&nbsp;<a href="test.jsp?reset=yo">Set
something in session</a>
</h1>
</h2>

<%
	if (request.getParameter("reset") != null) {
		

		session.setAttribute("test", new Long(System.currentTimeMillis()).toString());
	}
%>


session string
<%=session.getAttribute("test")%>

<br />
<br />
<h2>Cache Dump&nbsp;&nbsp;&nbsp;&nbsp;<a
	href="test.jsp?addCacheValue=yo">Add Item To Caches</a>&nbsp;&nbsp;&nbsp;&nbsp;<a
	href="test.jsp?clearCaches=yo">Clear Caches</a></h2>
<%
	CacheManager.create();
	String[] cacheNames = CacheManager.getInstance().getCacheNames();

	for (String cn : cacheNames) {
		out.write("Cache: " + cn + "<br/>");

		Cache cache = CacheManager.getInstance().getCache(cn);

		if (request.getParameter("clearCaches") != null) {
			cache.removeAll();
		}

		if (request.getParameter("addCacheValue") != null) {

			cache.put(new Element(new Long(System.currentTimeMillis()).toString(), "abc"

			));
		}

		List<Object> keys = (List<Object>) cache.getKeys();

		for (Object s : keys) {
			out.write("&nbsp;&nbsp;&nbsp;&nbsp;" + s + ": " + cache.get(s).getValue() + "<br/>");
		}
	}
%>

</body>
</html>
