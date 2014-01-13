<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@page import="com.picsauditing.PICS.MainPage" %>
<%@page import="com.picsauditing.access.OpPerms" %>
<%@ page import="com.picsauditing.access.Permissions" %>
<%@page import="com.picsauditing.actions.TranslationActionSupport" %>
<%@page import="com.picsauditing.actions.contractors.ContractorSubmenuDisplay" %>
<%@ page import="com.picsauditing.dao.UserDAO" %>
<%@ page import="com.picsauditing.i18n.service.TranslationService" %>
<%@ page import="com.picsauditing.jpa.entities.User" %>
<%@ page import="com.picsauditing.menu.MenuComponent" %>
<%@ page import="com.picsauditing.menu.builder.MenuBuilder" %>
<%@ page import="com.picsauditing.menu.builder.PicsMenu" %>
<%@ page import="com.picsauditing.model.i18n.LanguageModel" %>
<%@ page import="com.picsauditing.search.Database" %>
<%@ page import="com.picsauditing.security.SessionSecurity" %>
<%@ page import="com.picsauditing.service.i18n.TranslationServiceFactory" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ page import="com.picsauditing.util.AppVersion" %>
<%@ page import="com.picsauditing.util.SpringUtils" %>
<%@ page import="com.picsauditing.util.Strings" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>

<%
    TranslationService translationService = TranslationServiceFactory.getTranslationService();
    Locale locale = TranslationActionSupport.getLocaleStatic();
    String version = AppVersion.current.getVersion();
    MainPage mainPage = new MainPage(request, session);

    String protocol = mainPage.isPageSecure() ? "https" : "http";
    Permissions permissions = mainPage.getPermissions();

    boolean switchToUserIsSet = SessionSecurity.switchToUserIsSet(request);
    boolean debugMode = mainPage.isDebugMode();
    boolean useVersion7Menus = false;

    if (permissions.getUserId() > 0) {
        UserDAO userDao = SpringUtils.getBean("UserDAO");
        User user = userDao.find(permissions.getUserId());

        if (user != null) {
            useVersion7Menus = user.isUsingVersion7Menus();
        }
    }

    MenuComponent menu = new MenuComponent();
    String homePageUrl = "";
    if (useVersion7Menus) {
        MenuBuilder.reportUserDAO = SpringUtils.getBean("ReportUserDAO");
        homePageUrl = MenuBuilder.getHomePage(permissions);
    } else {
        menu = PicsMenu.getMenu(permissions);
        homePageUrl = PicsMenu.getHomePage(menu, permissions);
    }

    LanguageModel languageModel = (LanguageModel) SpringUtils.getBean("LanguageModel");
%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:set var="version"><%= version %></s:set>
<s:set var="has_contractor_menu_class">has-contractor-menu</s:set>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
    <title>PICS - <decorator:title default="PICS"/></title>

    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>

    <link rel="apple-touch-icon" href="images/icons/apple-touch-icon.png"/>
    <link rel="apple-touch-icon" sizes="57x57" href="images/icons/apple-touch-icon-57x57.png"/>
    <link rel="apple-touch-icon" sizes="72x72" href="images/icons/apple-touch-icon-72x72.png"/>
    <link rel="apple-touch-icon" sizes="114x114" href="images/icons/apple-touch-icon-114x114.png"/>
    <link rel="apple-touch-icon" sizes="144x144" href="images/icons/apple-touch-icon-144x144.png"/>

    <link rel="stylesheet" type="text/css" media="screen" href="css/reset.css?v=${version}"/>
    <link rel="stylesheet" type="text/css" href="css/print.css?v=${version}"/>

    <link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=${version}"/>
    <link rel="stylesheet" type="text/css" media="screen"
          href="js/jquery/autocomplete/jquery.autocomplete.css?v=${version}"/>
    <!--[if !IE 6]><!-->
    <link rel="stylesheet" type="text/css" media="screen" href="css/style.css?v=${version}"/>
    <!--<![endif]-->
    <link rel="stylesheet" type="text/css" href="css/insureguard/insureguard.css?v=${version}"/>
    <link rel="stylesheet" type="text/css" href="css/employee-guard/employee-guard.css?v=${version}"/>
    <link rel="stylesheet" type="text/css" media="screen" href="css/environment.css?v=${version}"/>
    <link rel="stylesheet" type="text/css" media="screen" href="js/jquery/tagit/jquery.tagit.css?v=${version}"/>
    <link rel="stylesheet" type="text/css" href="bootstrap3/css/vendor/select2/select2.css?v=${version}"/>
    <link rel="stylesheet" type="text/css" href="bootstrap3/css/vendor/select2/select2-override.css?v=${version}"/>
    <link rel="stylesheet" type="text/css" href="css/timezone.css?v=${version}"/>
    <%-- DO NOT ADD MORE STYLESHEETS TO THIS PAGE, WILL BREAK IE7 --%>

    <link rel="stylesheet" type="text/css" href="css/bootstrap/css/font-awesome.min.css?v=${version}"/>
    <!--[if lt IE 8]>
    <link rel="stylesheet" href="css/bootstrap/css/font-awesome-ie7.min.css"><![endif]-->


    <jsp:include page="/struts/layout/include_javascript.jsp"/>

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
        $(function () {
            $('#debug-menu, #debug-menu_menu').live('click', function (e) {
                e.preventDefault();
                $('body').toggleClass('debugging');
                $.cookie('debugging', $('body').is('.debugging'), { expires: 20 });
            });

            $('a[rel*="facebox"]').facebox({
                loading_image: 'loading.gif',
                close_image: 'closelabel.gif'
            });
        });
    </script>

    <decorator:head/>

    <!--CSS FIXES FOR INTERNET EXPLORER -->
    <!--[if IE]>
    <link rel="stylesheet" href="css/ie.css?v=${version}" type="text/css"/>
    <![endif]-->

    <!--[if IE 7]>
    <link rel="stylesheet" href="css/ie7.css?v=${version}" type="text/css"/>
    <![endif]-->

    <!-- compliance patch for microsoft browsers -->
    <!--[if lt IE 7]>
    <link rel="stylesheet" href="css/ie6.css?v=${version}" type="text/css"/>
    <![endif]-->
</head>
<body onload="<decorator:getProperty property="body.onload" />"
      onunload="<decorator:getProperty property="body.onunload" />" class="${has_contractor_menu_class} <%if (debugMode) {%>debugging<%}%>">
<jsp:include page="/struts/layout/_environment.jsp"/>

<%
    if (useVersion7Menus) {
%>
<link rel="stylesheet" type="text/css" href="css/bootstrap/css/bootstrap-menu.css?v=${version}"/>
<link rel="stylesheet" type="text/css" href="v7/css/vendor/bootstrap-responsive.css?v=${version}"/>
<script type="text/javascript" src="v7/js/vendor/bootstrap.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/layout/menu/menu.js?v=${version}"></script>

<header>
    <s:action name="Menu!menu" executeResult="true"/>

    <%-- include javascript translations --%>
    <s:action name="TranslateJS2" executeResult="true"/>
</header>
<%
    }
%>

<%
    if (mainPage.isDisplaySystemMessage()) {
        if (useVersion7Menus) {
%>
<style>
    body {
        padding-top: 55px;
    }

    #systemMessage {
        margin-bottom: 20px;
    }
</style>
<%
    }
%>
<jsp:include page="/struts/misc/main_system_message.jsp"/>
<%
    }
%>

<%
    if (!useVersion7Menus) {
%>

<div id="bodywrap">
    <table id="header">
        <!-- !begin header -->
        <tr>
            <td id="logo">
                <a href="<%=homePageUrl%>"><img src="images/logo_sm.png" alt="image" width="100" height="31"/></a>
            </td>
            <%
                if (permissions.isActive() && !permissions.isContractor()) {
            %>
            <td id="headersearch">
                <form action="Search.action" method="get">
                    <input type="hidden" value="search" name="button"/>
                    <input name="searchTerm" type="text" id="search_box" onfocus="clearText(this)" tabindex="1"/>
                    <input type="submit" value="<%=translationService.getText("Header.Search", locale)%>"
                           id="search_button"
                           onclick="getResult(null)"/>
                </form>
            </td>
            <%
                }
            %>

            <td id="sidebox">
                <p>
                    <b class="head-phone"
                       title="<%=Strings.isNotEmpty(mainPage.getCountryI18nKey()) ? translationService.getText(mainPage.getCountryI18nKey(), locale) : ""%>"><%=mainPage.getPhoneNumber()%>
                    </b>
                    <% if (permissions.isLoggedIn()) { %>
                                <span id="name">
                                    <%
                                        if (permissions.hasPermission(OpPerms.EditProfile)) {
                                    %>
                                        <%=translationService.getText("Header.WelcomeLink", locale, permissions.getAccountName(),
                                                permissions.getName())%>
                                    <%
                                    } else {
                                    %>
                                        <%=translationService.getText("Header.WelcomeNoLink", locale, permissions.getName())%>
                                    <%
                                        }
                                    %>
                                </span>
                    | <a href="<%=homePageUrl%>"><%=translationService.getText("global.Home", locale)%>
                </a>
                    | <a href="http://www.picsauditing.com">PICS</a>
                    <pics:toggle name="<%=FeatureToggle.TOGGLE_V7MENUS%>">
                        | <a href="/ProfileEdit!version7Menu.action?u=<%=permissions.getUserId()%>"><s:text
                            name="Menu.SwitchToVersion7"/></a>
                    </pics:toggle>
                    | <a href="Login.action?button=logout"><%=translationService.getText("Header.Logout", locale)%>
                </a>
                    <%
                        if (switchToUserIsSet) {
                    %>
                    | <a href="Login.action?button=switchBack">SwitchBack</a>
                    <%
                        }
                    %>
                    <%
                    } else {
                    %>
                    <span id="name"><%=translationService.getText("Header.Welcome", locale)%></span>
                    | <a href="Login.action"><%=translationService.getText("Header.Login", locale)%>
                </a>
                    | <a href="Registration.action"><%=translationService.getText("Header.Register", locale)%>
                </a>
                    <%
                        }
                    %>
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
                        for (MenuComponent item : menu.getChildren()) {
                            if (item.visible()) {
                    %>
                    <li><a<%=item.hasUrl() ? (" href=\"" + item.getUrl() + "\"") : ""%>
                            onmouseover="cssdropdown.dropit(this,event,'menu<%=item.getId()%>')"><span><%=item.getName()%></span></a>
                    </li>
                    <%
                            }
                        }
                    %>
                </ul>
            </div>
        </div>
    </div>
</div>
<!-- !end navigation -->
<%
    }
%>

<%
    String mibew_href = MenuBuilder.getMibewURL(locale, permissions);
    String chat_link_text = translationService.getText("Header.Chat", locale);
    String help_link_text = translationService.getText("Header.HelpCenter", locale);
%>

<div id="main">
    <div id="bodyholder">
        <div id="notify"></div>

        <div id="helpbox">
            <div id="helpcenter" style="float:left;">
                <a href="/HelpCenter.action" target="_BLANK"><%= help_link_text %>
                </a>
            </div>

            <div id="helpchat" style="float:left;">
                <a class="chat-link" href="<%= mibew_href %>" target="_blank"><%= chat_link_text %>
                </a>
            </div>
        </div>

        <div id="content">
            <!-- !begin content -->
            <noscript>
                <div class="error">You must enable JavaScript to use the PICS Organizer. Contact your IT Department if
                    you don't know how.
                </div>
            </noscript>

            <decorator:body/>

            <div><br clear="all"/></div>
            <!-- !end content -->
        </div>
    </div>
</div>

<%
    if (!useVersion7Menus) {
%>
<!-- !begin subnavigation -->
<%
    for (MenuComponent submenu : menu.getChildren()) {
%>
<div id="menu<%=submenu.getId()%>" class="dropmenudiv">
    <ul>
        <%
            for (MenuComponent item : submenu.getChildren()) {
                if (item.visible()) {
        %>
        <li>
            <%
                if (item.getName().equals("Online Chat")) {
            %>
            <a href="<%= mibew_href %>" target="_blank"><%= chat_link_text %>
            </a>
            <%
            } else {
                String dataFields = "";

                for (String dataKey : item.getDataFields().keySet()) {
                    dataFields += "data-" + dataKey + "=\"" + item.getDataFields().get(dataKey) + "\" ";
                }
            %>
            <a
                    <%=item.hasUrl() ? ("href=\"" + item.getUrl() + "\"") : ""%>
                    <%=item.hasHtmlID() ? ("id=\"subMenu_" + item.getHtmlId() + "\"") : ""%>
                    <%=!Strings.isEmpty(item.getTarget()) ? ("target=\"" + item.getTarget() + "\"") : ""%>
                    <%=dataFields%>
                    >
                <span><%=item.getName()%></span>
            </a>
            <%
                }
            %>
        </li>
        <%
                }
            }
        %>
    </ul>
</div>
<%
    }
%>
<!-- !end subnavigation -->
<%
    }
%>

<%
    if (!"1".equals(System.getProperty("pics.debug"))) {
%>
<script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-2785572-4']);
    _gaq.push(['_trackPageview']);

    (function () {
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0];
        s.parentNode.insertBefore(ga, s);
    })();
</script>
<%
    }
%>

<!-- !begin footer -->
<div class="footer">
    <%
        Long page_logger_id = (Long) request.getAttribute("pics_page_logger_id");
        if (page_logger_id != null) {
            Database db = new Database();
            try {
                db.executeUpdate("UPDATE app_page_logger SET endTime = '"
                        + new Timestamp(System.currentTimeMillis()) + "' WHERE id = " + page_logger_id);
            } catch (SQLException e) {
            }
        }

        Date startDate = (Date) request.getAttribute("pics_request_start_time");
        if (startDate != null) {
            long totalTime = System.currentTimeMillis() - startDate.getTime();
    %>
    <div class="pageStats" title="Server: <%=java.net.InetAddress.getLocalHost().getHostName()%>">
        <%=translationService.getText("Footer.Version", locale)%>: <%=version%><br/>
        <%=translationService.getText("Footer.ProcessTime", locale)%>: <%=Math.round(totalTime / 10) / 100f%>s
    </div>
    <%
        }
    %>
    <div id="footermain">
        <div id="footercontent">
            <a href="http://www.picsauditing.com/"
               class="footer"><%=translationService.getText("global.PICSCopyright", locale)%>
            </a> |
            <a href="Contact.action" class="footer"><%=translationService.getText("Footer.Contact", locale)%>
            </a> |
            <a href="PrivacyPolicy.action" rel="facebox"
               class="footer"><%=translationService.getText("Footer.Privacy", locale)%>
            </a>
        </div>
    </div>
</div>
</body>
</html>