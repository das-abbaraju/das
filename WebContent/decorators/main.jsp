<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%@page import="com.picsauditing.access.MenuComponent"%>
<%@page import="com.picsauditing.access.PicsMenu"%>
<%@page import="com.picsauditing.access.OpPerms"%>
<%
	MenuComponent menu = PicsMenu.getMenu(permissions);
%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"	prefix="decorator"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>PICS - <decorator:title
	default="PICS" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reset.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics_legacy.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css" title="bluemenu"/>
<script type="text/javascript" src="js/chrome.js"></script>
<script type="text/javascript" src="js/Search.js"></script>
<decorator:head />
</head>
<body>
<div id="header">
<div id="headermain">
<div id="masthead">
<!-- !begin header -->
<div id="smallnav"><p><% if (permissions.isLoggedIn()) { %>
<span id="name">Welcome, <%=permissions.getName() %></span> | <a href="<%= permissions.isContractor() ? "ContractorView" : "Home" %>.action">Home</a> | <a href="logout.jsp">Logout</a>
<% } else { %>
<span id="name">Welcome</span> | <a href="login.jsp">Login</a>
<% } %></p>
</div>
<% if (!permissions.isContractor() && !permissions.hasPermission(OpPerms.StatusOnly)) { %>
<div id="headersearch">
<form action="ContractorSearch.action" method="post">
<input name="accountName" type="text" class="blueMain" size="20" value="- Contractor Search -" onfocus="clearText(this)" onblur="unclearText(this)"/>
<input type="submit" value="Search" class="blueMain" />
</form>
</div>
<% } %>
<div id="logo"> <img src="images/logo_r3.jpg" alt="image" width="105" height="112" /></div>
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
<!-- !end navigation -->

<div id="main">
<div id="bodyholder">
<div id="content">
<!-- !begin content -->
<decorator:body />
<!-- !end content -->
</div>
</div>
</div>

<!-- !begin footer -->
<div class="footer">
<div id="footermain">
<div id="footercontent">
Copyright &copy; 2008
<a href="http://www.picsauditing.com/" class="footer">PICS</a> |
<a href="about.jsp" class="footer">About PICS</a> |
<a href="contact.jsp" class="footer">Contact Us</a>
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

</body>
</html>