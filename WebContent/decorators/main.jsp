<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ page import="java.net.InetAddress" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.picsauditing.access.MenuBuilder"%>
<%@ page import="com.picsauditing.access.MenuComponent" %>
<%@ page import="com.picsauditing.access.OpPerms" %>
<%@ page import="com.picsauditing.access.PicsMenu" %>
<%@ page import="com.picsauditing.access.Permissions" %>
<%@ page import="com.picsauditing.dao.UserDAO" %>
<%@ page import="com.picsauditing.jpa.entities.User" %>
<%@ page import="com.picsauditing.PICS.I18nCache" %>
<%@ page import="com.picsauditing.PICS.MainPage" %>
<%@ page import="com.picsauditing.util.PicsOrganizerVersion"%>
<%@ page import="com.picsauditing.util.SpringUtils" %>
<%@ page import="com.picsauditing.util.Strings" %>
<%@ page import="com.picsauditing.util.URLUtils" %>
<%@ page import="com.picsauditing.search.Database" %>
<%@ page import="com.picsauditing.actions.TranslationActionSupport" %>
<%
	I18nCache i18nCache = I18nCache.getInstance();
	Locale locale = TranslationActionSupport.getLocaleStatic();
	String version = PicsOrganizerVersion.getVersion();
	MainPage mainPage = new MainPage(request, session);

	String protocol = mainPage.isPageSecure() ? "https" : "http";
	Permissions permissions = mainPage.getPermissions();

	boolean liveChatEnabled = mainPage.isLiveChatEnabled();
	boolean debugMode = mainPage.isDebugMode();

	UserDAO userDao = SpringUtils.getBean("UserDAO");
	User user = userDao.find(permissions.getUserId());
	boolean useDynamicReports = false;
	if (user != null)
		useDynamicReports = user.isUsingDynamicReports();

	MenuComponent menu = new MenuComponent();
	String homePageUrl = "";
	if (useDynamicReports) {
		menu = MenuBuilder.buildMenubar(permissions);
		homePageUrl = MenuBuilder.getHomePage(menu, permissions);
	} else {
		menu = PicsMenu.getMenu(permissions);
		homePageUrl = PicsMenu.getHomePage(menu, permissions);
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>PICS - <decorator:title default="PICS" /></title>

		<meta http-equiv="Cache-Control" content="no-cache" />
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />

        <link rel="apple-touch-icon" href="images/icons/apple-touch-icon.png" />
        <link rel="apple-touch-icon" sizes="57x57" href="images/icons/apple-touch-icon-57x57.png" />
        <link rel="apple-touch-icon" sizes="72x72" href="images/icons/apple-touch-icon-72x72.png" />
        <link rel="apple-touch-icon" sizes="114x114" href="images/icons/apple-touch-icon-114x114.png" />
        <link rel="apple-touch-icon" sizes="144x144" href="images/icons/apple-touch-icon-144x144.png" />

		<link rel="stylesheet" type="text/css" media="screen" href="css/reset.css?v=${version}" />
		<link rel="stylesheet" type="text/css" href="css/print.css?v=${version}" />

		<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/contractorstatistics.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css?v=${version}" />
		<!--[if !IE 6]><!--><link rel="stylesheet" type="text/css" media="screen" href="css/style.css?v=${version}" /><!--<![endif]-->
		<link rel="stylesheet" type="text/css" media="screen" href="css/form.css?v=${version}" />
        <link rel="stylesheet" type="text/css" href="css/insureguard/insureguard.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/environment.css?v=${version}" />
        <link rel="stylesheet" type="text/css" media="screen" href="js/jquery/tagit/jquery.tagit.css?v=${version}" />
        <link rel="stylesheet" type="text/css" href="v7/css/libs/font-awesome.css?v=${version}" />

		<jsp:include page="/struts/layout/include_javascript.jsp" />

		<script type="text/javascript" src="js/jquery/util/jquery.utils.js?v=${version}"></script>
		<script type="text/javascript" src="js/chrome.js?v=${version}"></script>
		<script type="text/javascript" src="js/pics_main.js?v=${version}"></script>
		<script type="text/javascript" src="js/notes.js?v=${version}"></script>
		<script type="text/javascript" src="js/jquery/jquery.form.js?v=${version}"></script>
		<script type="text/javascript" src="js/jquery/jquery.cookie.js?v=${version}"></script>
		<script type="text/javascript" src="js/jquery/facebox/facebox.js?v=${version}"></script>
		<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js?v=${version}"></script>
		<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js?v=${version}"></script>
		<script type="text/javascript" src="js/jquery/jquery.ajaxQueue.js?v=${version}"></script>
		<script type="text/javascript" src="js/main_search.js?v=${version}"></script>

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

				$('#debug-menu').live('click', function(e) {
					e.preventDefault();
					$('body').toggleClass('debugging');
					$.cookie('debugging', $('body').is('.debugging'), { expires: 20 });
				});

				$('a[rel*="facebox"]').facebox({
					loading_image : 'loading.gif',
					close_image : 'closelabel.gif'
				});
			});
		</script>

		<decorator:head />

		<style>
		.searchAction
		{
			float: right;
			background: red;
			width: 10px;
			margin-top: 5px;
		}
		</style>

		<!--CSS FIXES FOR INTERNET EXPLORER -->
		<!--[if IE]>
			<link rel="stylesheet" href="css/ie.css?v=${version}" type="text/css" />
		<![endif]-->

        <!--[if IE 7]>
            <link rel="stylesheet" href="css/ie7.css?v=${version}" type="text/css" />
        <![endif]-->

		<!-- compliance patch for microsoft browsers -->
		<!--[if lt IE 7]>
			<link rel="stylesheet" href="css/ie6.css?v=${version}" type="text/css" />
		<![endif]-->
	</head>
	<body onload="<decorator:getProperty property="body.onload" />" onunload="<decorator:getProperty property="body.onunload" />"<% if(debugMode) { %>class="debugging"<% } %>>
        <jsp:include page="/struts/layout/environment.jsp" />
        
        <% if (useDynamicReports) { %>
        
        <nav id="site_navigation"></nav>
        
        <link rel="stylesheet" type="text/css" href="v7/js/extjs/pics/resources/css/my-ext-theme-menu.css" />
        <script type="text/javascript" src="v7/js/extjs/pics/extjs/ext-all.js"></script>
        <script type="text/javascript" src="v7/js/pics/layout/menu.js"></script>
        
        <% } %>

        <% if (!useDynamicReports) { %>
        <div id="bodywrap">
            <jsp:include page="/struts/misc/main_system_message.jsp" />
            <table id="header">
                <!-- !begin header -->
                <tr>
                    <td id="logo">
                        <a href="<%= homePageUrl %>"><img src="images/logo_sm.png" alt="image" width="100" height="31" /></a>
                    </td>
                    <% if (permissions.isActive() && !permissions.isContractor()) { %>
                        <td id="headersearch">
                            <form action="Search.action" method="get">
                                <input type="hidden" value="search" name="button" />
                                <input name="searchTerm" type="text" id="search_box" onfocus="clearText(this)" tabindex="1"/>
                                <input type="submit" value="<%=i18nCache.getText("Header.Search", locale)%>" id="search_button" onclick="getResult(null)" />
                            </form>
                        </td>
                    <% } %>
                    <td id="sidebox">
                        <p>
                            <b class="head-phone"><%=i18nCache.getText("PicsPhone", locale)%></b>&emsp;&emsp;
                        <% if (permissions.isLoggedIn()) { %>
                            <span id="name">
                                <% if (permissions.hasPermission(OpPerms.EditProfile)) { %>
                                    <%=i18nCache.getText("Header.WelcomeLink", locale, permissions.getAccountName(), permissions.getName()) %>
                                <% } else { %>
                                    <%=i18nCache.getText("Header.WelcomeNoLink", locale, permissions.getName()) %>
                                <% } %>
                            </span>
                        | <a href="<%= homePageUrl %>"><%=i18nCache.getText("global.Home", locale) %></a> | <a href="http://www.picsauditing.com">PICS</a> | <a href="Login.action?button=logout"><%=i18nCache.getText("Header.Logout", locale) %></a>
                        <% } else { %>
                            <span id="name"><%=i18nCache.getText("Header.Welcome", locale)%></span> | <a href="Login.action"><%=i18nCache.getText("Header.Login", locale)%></a> | <a href="Registration.action"><%=i18nCache.getText("Header.Register", locale)%></a>
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
        <% } %>

		<div id="main">
			<div id="bodyholder">
				<div id="notify"></div>

				<div id="helpbox">
					<%--
						http://solutions.liveperson.com/tagGen/gallery/General3-Blue-fr.asp

						Locales:

						- English (e.g. https://base.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a)
						- French
						- German
						- Hebrew
						- Portuguese
						- Spanish
					--%>

					<%
						String chatIcon = protocol + "://server.iad.liveperson.net/hc/90511184/?" +
							"cmd=repstate" +
							"&amp;site=90511184" +
							"&amp;channel=web" +
							"&amp;ver=1" +
							"&amp;imageUrl=" +
							protocol +
							"://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/" +
							locale.getDisplayLanguage() +
							"/General/3a";

						if ("1".equals(System.getProperty("pics.debug")) || !liveChatEnabled) {
							chatIcon = "";
						}

						String helpUrl = "http://help.picsorganizer.com/login.action?os_destination=homepage.action&";

						if (permissions.isOperatorCorporate()) {
							helpUrl += "os_username=operator&os_password=oper456ator";
						} else if (permissions.isContractor()) {
							helpUrl += "os_username=contractor&os_password=con123tractor";
						} else {
							helpUrl += "os_username=admin&os_password=ad9870mins";
						}
					%>

					<a href="<%= helpUrl %>" target="_BLANK"><%=i18nCache.getText("Header.HelpCenter", locale) %></a>

					<%  if (liveChatEnabled) { %>
						<a href="javascript:;" class="liveperson-chat-toggle"><%= i18nCache.getText("Header.Chat", locale) %></a>

						<a id="_lpChatBtn"
							class="liveperson-chat"
							href="<%= protocol %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/<%=locale.getDisplayLanguage() %>/General/3a"
							target="chat90511184"
							onClick="lpButtonCTTUrl = '<%= protocol %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/<%=locale.getDisplayLanguage() %>/General/3a&amp;referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" >

							<% if (!Strings.isEmpty(chatIcon)) { %>
								<img src="<%= chatIcon %>" />
							<% } else { %>
								<%= i18nCache.getText("Header.Chat", locale) %>
							<% } %>
						</a>
					<% } %>
				</div>

				<div id="content">
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

		<% if (!useDynamicReports) { %>
		<!-- !begin subnavigation -->
		<% for(MenuComponent submenu : menu.getChildren()) { %>
		<div id="menu<%= submenu.getId()%>" class="dropmenudiv">
			<ul>
				<%
				for(MenuComponent item : submenu.getChildren()) {
					if (item.visible()) { %>
					<li>
						<%  if(item.getName().equals("Online Chat"))  {
								if (liveChatEnabled) { %>
								<a id="_lpChatBtn"
									href='<%= protocol %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/<%=locale.getDisplayLanguage() %>/General/3a'
									target='chat90511184'
									onClick="lpButtonCTTUrl = '<%= protocol %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/<%=locale.getDisplayLanguage() %>/General/3a&amp;referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" ><span><%=item.getName()%></span></a>
						<% 		}
							} else {
							String dataFields = "";
							for (String dataKey : item.getDataFields().keySet()) {
								dataFields += "data-" + dataKey + "=\"" + item.getDataFields().get(dataKey) + "\" ";
							} %>
							<a
								<%=item.hasUrl() ? ("href=\""+item.getUrl()+"\"") : "" %>
								<%=item.hasHtmlID() ? ("id=\"subMenu_" + item.getHtmlId() + "\"") : "" %>
								<%=!Strings.isEmpty(item.getTarget()) ? ("target=\"" + item.getTarget() + "\"") : "" %>
								<%=dataFields %>
							>
								<span><%=item.getName()%></span>
							</a>
							<% } %>
					</li><%
					}
				}
				%>
			</ul>
		</div>
		<% } %>
		<!-- !end subnavigation -->
		<% } %>

		<%
			if (!"1".equals(System.getProperty("pics.debug"))) {
				if (liveChatEnabled) {
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
			src='<%= protocol %>://server.iad.liveperson.net/hc/90511184/x.js?cmd=file&file=chatScript3&site=90511184&&imageUrl=<%= protocol %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/<%=locale.getDisplayLanguage() %>/General/3a'>
		</script>
		<!-- END LivePerson -->
		<%	} %>

		<script type="text/javascript">
		  var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', 'UA-2785572-4']);
		  _gaq.push(['_trackPageview']);

		  (function() {
		    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
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
					<a href="http://www.picsauditing.com/" class="footer"><%=i18nCache.getText("global.PICSCopyright", locale) %></a> |
					<a href="Contact.action" class="footer"><%=i18nCache.getText("Footer.Contact", locale) %></a> |
					<a href="PrivacyPolicy.action" rel="facebox" class="footer"><%=i18nCache.getText("Footer.Privacy", locale) %></a>
				</div>
			</div>
		</div>
	</body>
</html>