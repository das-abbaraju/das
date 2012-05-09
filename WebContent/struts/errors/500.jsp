<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.StringWriter"%>
<%@ page isErrorPage="true"%>

<!doctype html>
	<head>
		<link rel="stylesheet" type="text/css" media="screen" href="/css/pics.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="/css/forms.css" />
	</head>
	<body>
		<div id="main">
			<div id="bodyholder" style="margin-top: 50px;">
				<div id="content">
					<h1>HTTP 500 ERROR</h1>
					<div id="error">
						We could not reach the page you tried to connect to. Please refresh
						to return to PICS. If you believe this was caused by a bug on our
						website, then please let <a href="mailto:errors@picsauditing.com?subject=HTTP 500 ERROR">us know</a>.
						<%
							if (exception != null) {
								StringWriter sw = new StringWriter();
								exception.printStackTrace(new PrintWriter(sw));
								String exceptionStack = sw.toString();
								
								application.log("HTTP 500 ERROR page reached, logging exception", exception);
						%>
							<pre>
<%=exceptionStack %>
							</pre>
						<%
							}
						%>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>