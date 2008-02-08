<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<% if (!permissions.loginRequired(response, request)) return; %>
<jsp:useBean id="pageBean" class="com.picsauditing.PICS.WebPage" scope ="page"/>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope="session" />
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope ="application"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope="application"/>
