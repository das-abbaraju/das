<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.picsauditing.actions.HTTP500" %>
<%@ page isErrorPage="true"%>
<%
	HTTP500 http500 = new HTTP500(request, application, exception);
	http500.saveError();
%>
<!doctype html>
	<head>
		<link rel="stylesheet" type="text/css" media="screen" href="/css/pics.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="/css/forms.css" />
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
		<script type="text/javascript">
			$(function() {
			    $('#toggle_stacktrace').live('click', function(event) {
			        $('#stacktrace').slideToggle();
			    });
			});
		</script>
		<style type="text/css">
			#bodyholder
			{
				margin-top: 50px;
			}
			
			#stacktrace
			{
				display: none;
			}
		</style>
	</head>
	<body>
		<div id="main">
			<div id="bodyholder">
				<div id="content">
					<h1>HTTP 500 ERROR</h1>
					<div id="error">
						We could not reach the page you tried to connect to. Please refresh
						to return to PICS. If you believe this was caused by a bug on our
						website, then please let <a href="mailto:errors@picsauditing.com?subject=HTTP 500 ERROR" class="email">us know</a>.
						<%
							if (http500.hasException()) {
						%>
						<div id="stacktrace">
							<pre>
<%= http500.getFullInformation() %>
							</pre>
						</div>
						<br />
						<br />
						<a href="javascript:;" class="preview" id="toggle_stacktrace">Click here to view the error.</a>
						<%
							}
						%>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>