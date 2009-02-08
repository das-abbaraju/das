<%@page import="com.picsauditing.PICS.*"%>
<%@page import="com.picsauditing.access.*"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<% if (!permissions.loginRequired(response, request)) return; %>
