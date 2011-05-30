<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="sitemesh-decorator" prefix="decorator"%>
<%@ page import="java.net.InetAddress"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Locale"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.picsauditing.access.MenuComponent"%>
<%@ page import="com.picsauditing.access.OpPerms"%>
<%@ page import="com.picsauditing.access.PicsMenu"%>
<%@ page import="com.picsauditing.access.Permissions"%>
<%@ page import="com.picsauditing.dao.AppPropertyDAO"%>
<%@ page import="com.picsauditing.jpa.entities.AppProperty"%>
<%@ page import="com.picsauditing.PICS.I18nCache"%>
<%@ page import="com.picsauditing.util.SpringUtils"%>
<%@ page import="com.picsauditing.util.Strings"%>
<%@ page import="com.picsauditing.util.URLUtils"%>
<%@ page import="com.picsauditing.search.Database"%>
<%@ page import="com.picsauditing.actions.TranslationActionSupport"%>
<%
	I18nCache i18nCache = I18nCache.getInstance();

	String version = com.picsauditing.actions.PicsActionSupport.getVersion();
	Permissions permissions = (Permissions)session.getAttribute("permissions");
	if (permissions == null) {
		permissions = new Permissions();
	}

	Locale locale = TranslationActionSupport.getLocaleStatic();

	boolean pageIsSecure = false;
	if (request.isSecure())
		pageIsSecure = true;
	else if (request.getLocalPort() == 443)
		pageIsSecure = true;
	else if (request.getLocalPort() == 81)
		pageIsSecure = true;
	String protocol = pageIsSecure ? "https" : "http";
	MenuComponent menu = PicsMenu.getMenu(permissions);
	AppPropertyDAO appPropertyDAO = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");
	AppProperty appProperty = appPropertyDAO.find("SYSTEM.MESSAGE");
	boolean showMessage = !Strings.isEmpty(appProperty.getValue());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en" >
<head>
<title>PICS - <decorator:title default="PICS" /></title>
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" media="print" href="css/print.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reset.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<%=version%>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/contractorstatistics.css?v=<%=version%>" />

<script type="text/javascript" src="<%= pageIsSecure ? "https" : "http" %>://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery/util/jquery.utils.js"></script>
<script type="text/javascript" src="js/chrome.js"></script>
<script type="text/javascript" src="js/pics_main.js?v=<%=version%>"></script>
<script type="text/javascript" src="js/notes.js?v=<%=version%>"></script>
<script type="text/javascript" src="js/jquery/jquery.form.js"></script>
<script type="text/javascript">
$(function() {
	$(document).ajaxError(function(e, xhr, originalSettings, exception) {
		if (xhr.status == 401) {
			$.facebox(function() {
				$.get('Login!overlay.action', function(response, status, loginXhr) {
					var html = $(response).addClass('overlay');
					html.find('#login').ajaxForm({
						url: 'Login!ajax.action',
						dataType: 'json',
						success: function(response, status, formXhr, $form) {
							if (response.loggedIn) {
								$.facebox.close();
								if (originalSettings.url.indexOf('?') != -1) {
									originalSettings.url = originalSettings.url.replace(/\?.*$/,'');
								}
								$.ajax(originalSettings);
							} else {
								$('#loginMessages').msg('error', response.actionError, true);
								$('#username').focus();
							}
						}
					});
					$.facebox(html);
					html.find('#username').focus();
				});
			});
		}
	});
});
</script>

<decorator:head />
<style>
.searchAction{
	float: right;
	background: red;
	width: 10px;
	margin-top: 5px;
}
</style>

<!--CSS FIXES FOR INTERNET EXPLORER -->
<!--[if IE]>
	<link rel="stylesheet" href="css/ie.css" type="text/css" />
<![endif]-->

<!-- compliance patch for microsoft browsers -->
<!--[if lt IE 7]>
	<link rel="stylesheet" href="css/ie6.css" type="text/css" />
<![endif]-->

<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript" src="js/jquery/jquery.ajaxQueue.js"></script>
<script>
var autoSearch;
var SEARCH_DELAY = 200;
$(function(){
	autoSearch = $('#search_box').autocomplete('HeaderSearchAjax.action', {
		width: 325,
		scroll: false,
		max: 11,
		delay: SEARCH_DELAY*2,
		selectFirst: false,
		highlight : false,
		formatItem: function(data,i,count){
			return format(data, i);
		},
		formatResult: function(data,i,count){
			if(data[0]=='FULL')
				return " ";
			if(data[0]=='NULL')
				return " ";
			return data[3];
		}
	}).result(function(event, data){
		getResult(data);
	}).keydown(function(){
		var tSize = $(this).val().length;
		if(tSize<=1)
			sDelay = SEARCH_DELAY * 2;
		else if(tSize<=2)
			sDelay = SEARCH_DELAY * 1.5;
		else if(tSize<=3)
			sDelay = SEARCH_DELAY * 1.25;
		else
			sDelay = SEARCH_DELAY;
		$(this).setOptions({delay: sDelay});
	});
});
function getResult(data){
	if(data[0]=='FULL'){
		location.href='Search.action?button=search&searchTerm='+data[2];
		return;
	}
	if(data[0]=='NULL'){
		return;
	}
	location.href='HeaderSearchAjax.action?button=getResult&searchID='+data[2]+'&searchType='+data[0];
}
function format(row, i){
	if(row[0]=='account'){
		var rStr = "<div style=\"float: left; margin-right: 5px;\"><strong>"+row[1]+":</strong><br/><span style=\"font-size: .9em\">(ID "+row[2]+
			")</span></div>"+row[3];
		if(row[4]!=null)
			rStr+="<br/> at ("+row[4]+")";
		return rStr;
	}
	if(row[0]=='user'){
		var rStr = "<div style=\"float: left; margin-right: 23px;\"><strong>"+row[1]+":</strong><br/><span style=\"font-size: .9em\">(ID "+row[2]+
			")</span></div><div style=\"\">"+row[3]+"<br/> at ("+row[4]+")</div>";
		return rStr;
	}
	if(row[0]=='employee'){
		var rStr = "<div style=\"float: left; margin-right: 10px;\"><strong>"+row[1]+":</strong><br/><span style=\"font-size: .9em\">(ID "+row[2]+
			")</span></div><div style=\"\">"+row[3]+"<br/> at ("+row[4]+")</div>";
		return rStr;
	}
	if(row[0]=='FULL')
		return row[1];
	if(row[0]=='NULL')
		return row[1];
	return row[0];
}
function buildAction(type, id){
	if(type=='user'){
		return '<div class="searchAction" onclick="location.href("Login.action?button=login&switchToUser='+id+')">S</div>';
	}
}
</script>
</head>
<body onload="<decorator:getProperty property="body.onload" />" onunload="<decorator:getProperty property="body.onunload" />">
<div id="bodywrap">
<% if (showMessage) { %>
	<div id="systemMessage">
		<%= appProperty.getValue()%>
		<div class="clear"></div>
	</div>
<% } %>
<table id="header">
<!-- !begin header -->
<tr>
<td id="logo">
	<a href="<%= PicsMenu.getHomePage(PicsMenu.getMenu(permissions), permissions)%>"><img src="images/logo_sm.png" alt="image" width="100" height="31" /></a>
</td>
<% if (permissions.isActive() && !permissions.isContractor()) { %>
	<td id="headersearch">
		<form action=Search.action method="get">
			<input type="hidden" value="search" name="button" />
			<input name="searchTerm" type="text" id="search_box" onfocus="clearText(this)" tabindex="1"/>
			<input type="submit" value="<%=i18nCache.getText("Header.Search", locale)%>" id="search_button" onclick="getResult(null)" />
		</form>
	</td>
<% } %>
<td id="sidebox">
	<p>
	<%
	String phone = "1-800-506-PICS (7427)";
	if (permissions.isLoggedIn()) {
		String countryCode = permissions.getCountry();
		if (countryCode != null && !countryCode.equals("US") && !countryCode.equals("CA"))
			phone = "1-949-936-4500";
	}
	%>
		<b class="head-phone"><%=phone%></b>&emsp;&emsp;
	<% if (permissions.isLoggedIn()) { %>
		<span id="name">
			<% if (permissions.hasPermission(OpPerms.EditProfile)) { %>
				<%=i18nCache.getText("Header.WelcomeLink", locale, permissions.getAccountName(), permissions.getName()) %>
			<% } else { %>
				<%=i18nCache.getText("Header.WelcomeNoLink", locale, permissions.getName()) %>
			<% } %>
		</span>
	| <a href="<%= PicsMenu.getHomePage(PicsMenu.getMenu(permissions), permissions)%>"><%=i18nCache.getText("global.Home", locale) %></a> | <a href="http://www.picsauditing.com">PICS</a> | <a href="Login.action?button=logout"><%=i18nCache.getText("Header.Logout", locale) %></a>
	<% } else { %>
		<span id="name"><%=i18nCache.getText("Header.Welcome", locale)%></span> | <a href="Login.action"><%=i18nCache.getText("Header.Login", locale)%></a> | <a href="ContractorRegistration.action"><%=i18nCache.getText("Header.Register", locale)%></a>
	<% } %>
	</p>
</td>
</tr>
</table>
</div>

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
<div id="mainStatus"></div>
<div id="helpbox">
<%
	String chatIcon = protocol +
		"://server.iad.liveperson.net/hc/90511184/?cmd=repstate&amp;site=90511184&amp;channel=web&amp;ver=1&amp;imageUrl=" +
		protocol + "://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a";
	if ("1".equals(System.getProperty("pics.debug")))
		chatIcon = "";
%>
	<a href="http://help.picsauditing.com/wiki/<decorator:getProperty property="meta.help" default="Help_Center" />" target="_BLANK"><%=i18nCache.getText("Header.HelpCenter", locale) %></a>
	<a id="_lpChatBtn"
	onmouseover="showChat();"
	onmouseout="hideChat();"
	href='<%= protocol %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a'
	target='chat90511184'
	onClick="lpButtonCTTUrl = '<%= protocol %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a&amp;referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" ><%=i18nCache.getText("Header.Chat", locale) %></a>
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
<div><br clear="all" /></div>
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
						href='<%= protocol %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a'
						target='chat90511184'
						onClick="lpButtonCTTUrl = '<%= protocol %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a&amp;referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" ><span><%=item.getName()%></span></a>
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
	src='<%= protocol %>://server.iad.liveperson.net/hc/90511184/x.js?cmd=file&file=chatScript3&site=90511184&&imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a'>
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
			<%=i18nCache.getText("Footer.Version", locale) %>: <%=version%><br />
			<%=i18nCache.getText("Footer.ProcessTime", locale)%>: <%= Math.round(totalTime/10)/100f%>s
		</div><%
	}
%>
<div id="footermain">
<div id="footercontent">
Copyright &copy; 2011
<a href="http://www.picsauditing.com/" class="footer">PICS</a> |
<a href="Contact.action" class="footer"><%=i18nCache.getText("Footer.Contact", locale) %></a> |
<a href="#" onclick="return openWindow('privacy_policy.jsp','PRIVACY');"
	title="<%=i18nCache.getText("global.NewWindow", locale)%>" class="footer"><%=i18nCache.getText("Footer.Privacy", locale) %></a>
</div>
</div>
</div>
</body>
</html>