<%@page import="com.picsauditing.PICS.*"%>
<%@page import="com.picsauditing.access.*"%>
<jsp:useBean id="permissions" class="Permissions" scope="session" />
<% if (!permissions.loginRequired(response, request)) return; %>
<jsp:useBean id="pageBean" class="WebPage" scope ="page"/>
<jsp:useBean id="pBean" class="PermissionsBean" scope="session" />
<jsp:useBean id="FACILITIES" class="Facilities" scope ="application"/>
<jsp:useBean id="AUDITORS" class="Auditors" scope="application"/>
