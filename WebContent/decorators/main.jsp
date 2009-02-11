<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%@page import="com.picsauditing.access.MenuComponent"%>
<%@page import="com.picsauditing.access.PicsMenu"%>
<%@page import="com.picsauditing.access.OpPerms"%>
<%@page import="com.picsauditing.util.URLUtils"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Date"%>
<%
	MenuComponent menu = PicsMenu.getMenu(permissions);
%>
<%@ taglib uri="sitemesh-decorator" prefix="decorator"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>PICS - <decorator:title
	default="PICS" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="print" href="css/print.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reset.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css" title="bluemenu"/>
<script src="js/chrome.js" type="text/javascript" ></script>
<script src="js/pics_main.js" type="text/javascript" ></script>

<!--CSS FIXES FOR INTERNET EXPLORER --->
<!--[if IE]>
	<link rel="stylesheet" href="css/ie.css" type="text/css" />
<![endif]-->

<!-- compliance patch for microsoft browsers -->
<!--[if lt IE 7]>
	<link rel="stylesheet" href="css/ie6.css" type="text/css" />
<![endif]-->

<decorator:head />
</head>
<body onload="<decorator:getProperty property="body.onload" />" onunload="<decorator:getProperty property="body.onunload" />">
<div id="header">
<div id="headermain">

<!--[if lte IE 6]>
	<div id="unsupportedBrowser">
		You are using an UnSupported Browser. Please upgrade to IE 7.
	</div>
<![endif]-->

<div id="masthead">
<!-- !begin header -->

<div id="sidebox">
	<div id="boxbody">
		<p><% if (permissions.isLoggedIn()) { %>
<span id="name">Welcome, <%=permissions.getName() %></span>
| <a href="<%= PicsMenu.getHomePage(PicsMenu.getMenu(permissions), permissions)%>">Home</a>| <a href="Login.action?button=logout">Logout</a>
<% } else { %>
<span id="name">Welcome</span> | <a href="Login.action">Login</a>
<% } %></p>
	</div>
</div>
<% if (permissions.isActive() && !permissions.isContractor()) { %>

<div id="headersearch">
<form action="ContractorSearch.action" method="post">
<input name="filter.accountName" type="text" class="blueMain" size="20" onfocus="clearText(this)" onblur="unclearText(this)"/>
<input type="submit" value="Search" class="blueMain" />
</form>
</div>
<% } %>
<div id="logo"><a href="Home.action"><img src="images/logo_r3.jpg" alt="image" width="105" height="112" /></a></div>
</div></div></div>

<!-- !begin navigation -->
<div id="nav">		
<div id="MainMenu">
	<div id="tab">
		<div id="navbar">		
			<ul>
			<%
			for(MenuComponent item : menu.getChildren()) {
				if (item.visible()) { %>
				<li><a<%=item.hasUrl() ? (" href=\""+item.getUrl()+"\"") : "" %> onmouseover="cssdropdown.dropit(this,event,'menu<%= item.getId()%>')"><span><%=item.getName()%></span></a></li><%
				}
			}
			%>
			</ul>
		</div>		
	</div>
</div>
</div>
<!-- !end navigation -->

<div id="main">
<div id="bodyholder">
<div id="content">
<div id="helpbox">
	<a href='javascript:D2H_ShowHelp(<decorator:getProperty property="meta.contextID" default="1" />, 
	<% if(permissions.isActive()) { %>
		<% if(permissions.isContractor()) { %>"help/c/default.htm"
		<% } else if(permissions.isOperator() || permissions.isCorporate()) { %>"help/o/default.htm"
		<% } else { %>"help/a/default.htm"
		<% } %>, "_BLANK", 1)'>Help Center</a> |
	<% } %>
	<a id="_lpChatBtn"
	onmouseover="showChat();"
	onmouseout="hideChat();"
	href='<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&file=visitorWantsToChat&site=90511184&byhref=1&imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a' 
	target='chat90511184'
	onClick="lpButtonCTTUrl = '<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&file=visitorWantsToChat&site=90511184&imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a&referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" >Chat</a>
</div>
<div id="chatIcon" style="display: none;">
	<img src='<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=repstate&site=90511184&channel=web&&ver=1&imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a' 
	name='hcIcon' border=0 />
</div>
<!-- !begin content -->
<decorator:body />
<!-- !end content -->
</div>
</div>
</div>

<!-- !begin subnavigation -->
<% for(MenuComponent submenu : menu.getChildren()) { %>
<div id="menu<%= submenu.getId()%>" class="dropmenudiv">
	<ul><%
		for(MenuComponent item : submenu.getChildren()) {
			if (item.visible()) { %>
		<li><a <%=item.hasUrl() ? ("href=\""+item.getUrl()+"\"") : "" %>><span><%=item.getName()%></span></a></li><%
			}
		}
		%>
	</ul>
</div>
<% } %>
<!-- !end subnavigation -->
<!-- BEGIN Invitation Positioning  -->
<script language="javascript" type="text/javascript">
	var lpPosY = 100;
	var lpPosX = 100;
</script>
<!-- END Invitation Positioning  -->

<!-- BEGIN Monitor Tracking Variables  -->
<script language="JavaScript1.2">
	if (typeof(tagVars) == "undefined") tagVars = "";
<%	if (permissions.isLoggedIn()) { %>
		tagVars += "&VISITORVAR!UserID=<%=permissions.getUserId()%>&VISITORVAR!UserName=<%=URLEncoder.encode(permissions.getUsername())%>&VISITORVAR!DisplayName=<%=URLEncoder.encode(permissions.getName())%>";
<%	} %>
</script>
<!-- End Monitor Tracking Variables  -->

<!-- BEGIN HumanTag Monitor. DO NOT MOVE! MUST BE PLACED JUST BEFORE THE /BODY TAG -->
<script 
	language='javascript' 
	src='<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/x.js?cmd=file&file=chatScript3&site=90511184&&imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a'> 
</script>
<!-- END HumanTag Monitor. DO NOT MOVE! MUST BE PLACED JUST BEFORE THE /BODY TAG -->
<%@ include file="../includes/statcounter.jsp"%>

<!-- !begin footer -->
<div class="footer">
<% 
	Date startDate = (Date) request.getAttribute("pics_request_start_time"); 
	if( startDate != null ) {
		long totalTime = System.currentTimeMillis() - startDate.getTime();
		%><div class="pageStats">Page generated in: <%= ((float) totalTime )/ ((float) 1000) %> seconds.</div><%
	} 
%>
<div id="footermain">
<div id="footercontent">
Copyright &copy; 2008
<a href="<%= URLUtils.getProtocol( request ) %>://www.picsauditing.com/" class="footer">PICS</a> |
<a href="contact.jsp" class="footer">Contact Us</a>
</div>
</div>
</div>
</body>
</html>
