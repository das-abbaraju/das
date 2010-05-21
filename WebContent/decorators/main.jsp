<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page import="java.net.InetAddress"%><html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en" >
<%@page import="com.picsauditing.dao.AppPropertyDAO"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="com.picsauditing.jpa.entities.AppProperty"%>
<%@page import="com.picsauditing.util.Strings"%>
<%@ page language="java" %>
<%@ page import="com.picsauditing.access.MenuComponent"%>
<%@ page import="com.picsauditing.access.PicsMenu"%>
<%@ page import="com.picsauditing.access.OpPerms"%>
<%@ page import="com.picsauditing.util.URLUtils"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.Date"%>
<%@ page import="com.picsauditing.search.Database"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="java.sql.SQLException"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
	String version = com.picsauditing.actions.PicsActionSupport.getVersion();
	MenuComponent menu = PicsMenu.getMenu(permissions);
	AppPropertyDAO appPropertyDAO = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");
	AppProperty appProperty = appPropertyDAO.find("SYSTEM.MESSAGE");
	boolean showMessage = !Strings.isEmpty(appProperty.getValue());
%>
<%@ taglib uri="sitemesh-decorator" prefix="decorator"%>
<head>
<title>PICS - <decorator:title default="PICS" /></title>
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" media="print" href="css/print.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reset.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/contractorstatistics.css?v=<%=version%>" />

<script src="js/chrome.js" type="text/javascript" ></script>
<script type="text/javascript" src="js/pics_main.js?v=<%=version%>"></script>
<script type="text/javascript" src="js/notes.js?v=<%=version%>"></script>

<decorator:head />

<!--CSS FIXES FOR INTERNET EXPLORER -->
<!--[if IE]>
	<link rel="stylesheet" href="css/ie.css" type="text/css" />
<![endif]-->

<!-- compliance patch for microsoft browsers -->
<!--[if lt IE 7]>
	<link rel="stylesheet" href="css/ie6.css" type="text/css" />
<![endif]-->

</head>
<body onload="<decorator:getProperty property="body.onload" />" onunload="<decorator:getProperty property="body.onunload" />">
<div id="bodywrap">
<% if (showMessage) { %>
	<div id="systemMessage">
		<%= appProperty.getValue()%>
		<div class="clear"></div>
	</div>
<% } %>
<div id="header">
<div id="headermain">

<div id="masthead">
<!-- !begin header -->
<div id="sidebox">
	<p><b style="color: #80b3da">1-800-506-PICS (7427)</b>&emsp;&emsp;
	<% if (permissions.isLoggedIn()) { %>
		<span id="name">Welcome, <%= permissions.hasPermission(OpPerms.EditProfile) ? 
			"<a href='ProfileEdit.action' title='" + permissions.getAccountName().replaceAll("'", "\'") + "'>"+permissions.getName()+"</a>"
			: permissions.getName() %></span>
| <a href="<%= PicsMenu.getHomePage(PicsMenu.getMenu(permissions), permissions)%>">PICS Organizer</a> | <a href="http://www.picsauditing.com">PICS Home</a> | <a href="Login.action?button=logout">Logout</a>
<% } else { %>
<span id="name">Welcome</span> | <a href="Login.action">Login</a>
<% } %>
	</p>
</div>
<% if (permissions.isActive() && !permissions.isContractor()) { %>

<div id="headersearch"<% if (permissions.isLoggedIn()) { %> style="padding-left: 120px;"<% } %>>
<form action="ContractorSearch.action" method="get">
<input name="filter.accountName" type="text" id="search_box" onfocus="clearText(this)" tabindex="1"/>
<input type="submit" value="Search" id="search_button" />
</form>
</div>
<% } %>
<div id="logo"<% if (permissions.isLoggedIn()) { %> style="position: absolute;"<% } %>><a href="<%= PicsMenu.getHomePage(PicsMenu.getMenu(permissions), permissions)%>"><img src="images/logo_sm.png" alt="image" width="100" height="31" /></a></div>
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
<div id="helpbox">
<%
	String chatIcon = URLUtils.getProtocol( request ) + 
		"://server.iad.liveperson.net/hc/90511184/?cmd=repstate&amp;site=90511184&amp;channel=web&amp;ver=1&amp;imageUrl=" + 
		URLUtils.getProtocol(request) + "://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a";
	if ("1".equals(System.getProperty("pics.debug")))
		chatIcon = "";
%>
	<a href="http://help.picsauditing.com/wiki/<decorator:getProperty property="meta.help"
		default="Help_Center" />" target="_BLANK">Help Center</a>
	<a id="_lpChatBtn"
	onmouseover="showChat();"
	onmouseout="hideChat();"
	href='<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a' 
	target='chat90511184'
	onClick="lpButtonCTTUrl = '<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a&amp;referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" >Chat</a>
</div>
<div id="content">
<% if (!Strings.isEmpty(chatIcon)) { %>
<div id="chatIcon" style="display: none;"><img src='<%=chatIcon%>'/></div>
<% } %>
<!-- !begin content -->
<noscript>
	<div class="error">You must enable JavaScript to use the PICS Organizer. Contact your IT Department if you don't know how.</div>
</noscript>
<decorator:body />
<div><br clear="both"></br></div>
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
		<li>
			<% if(item.getName().equals("Online Chat"))  { %>
					<a id="_lpChatBtn"
						href='<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a' 
						target='chat90511184'
						onClick="lpButtonCTTUrl = '<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a&amp;referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" ><span><%=item.getName()%></span></a>
			<% } else { %>
				<a <%=item.hasUrl() ? ("href=\""+item.getUrl()+"\"") : "" %>><span><%=item.getName()%></span></a>
				<% } %>
		</li><%
			}
		}
		%>
	</ul>
</div>
<% } %>
<!-- !end subnavigation -->

<%
	if (!"1".equals(System.getProperty("pics.debug"))) {
%>

<!-- BEGIN LivePerson -->
<script type="text/javascript">
	var lpPosY = 100;
	var lpPosX = 100;
	
	if (typeof(tagVars) == "undefined") tagVars = "";
<%	if (permissions.isLoggedIn()) { %>
		tagVars += "&VISITORVAR!UserID=<%=permissions.getUserId()%>&VISITORVAR!UserName=<%=URLEncoder.encode(permissions.getUsername())%>&VISITORVAR!DisplayName=<%=URLEncoder.encode(permissions.getName())%>";
<%	} %>
</script>
<!-- End Monitor Tracking Variables  -->

<script 
	type="text/javascript" 
	src='<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/x.js?cmd=file&file=chatScript3&site=90511184&&imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a'> 
</script>
<!-- END LivePerson -->

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? 
	"https://ssl." : "http://www.");
	document.write(unescape("%3Cscript src='" + gaJsHost + 
	"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
try {
	var pageTracker = _gat._getTracker("UA-2785572-1");
	pageTracker._initData();
	pageTracker._trackPageview();
<%	if (permissions.isLoggedIn()) { %>
	pageTracker._setVar('<%= permissions.getAccountType() %>');
<%	} %>
} catch(err) {}
</script>

<% } %>

<!-- !begin footer -->
<div class="footer">
<% 
	Long page_logger_id = (Long) request.getAttribute("pics_page_logger_id");
	if( page_logger_id != null ) {
		Database db = new Database();
		try {
			db.executeUpdate("UPDATE app_page_logger SET endTime = '"+new Timestamp(System.currentTimeMillis())+"' WHERE id = "+page_logger_id);
		} catch (SQLException e) {
		}
	}		
	Date startDate = (Date) request.getAttribute("pics_request_start_time"); 
	if( startDate != null ) {
		long totalTime = System.currentTimeMillis() - startDate.getTime();
		%><div class="pageStats" title="Server: <%= java.net.InetAddress.getLocalHost().getHostName() %>">
			App Version: <%=version%><br />
			Process Time: <%= Math.round(totalTime/10)/100f%>s
		</div><%
	}
%>
<div id="footermain">
<div id="footercontent">
Copyright &copy; 2009
<a href="<%= URLUtils.getProtocol( request ) %>://new.picsorganizer.com/" class="footer">PICS</a> |
<a href="Contact.action" class="footer">Contact Us</a> |
<a href="#" onclick="return openWindow('privacy_policy.jsp','PRIVACY');"
	title="Opens in new window" class="footer">Privacy Policy</a>
</div>
</div>
</div>
</div>
</body>
</html>
