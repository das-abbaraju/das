<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.util.URLUtils"%>
<%@page import="com.sun.syndication.io.XmlReader" %>
<%@page import="com.sun.syndication.io.SyndFeedInput" %>
<%@page import="com.sun.syndication.feed.synd.SyndFeed" %>
<%@page import="com.sun.syndication.feed.synd.SyndEntry" %>
<%@page import="com.sun.syndication.feed.synd.SyndContent"%>
<%@page import="com.picsauditing.PICS.DateBean"%>
<%@page import="java.net.URL" %>
<%
	String url = request.getRequestURL().toString();
	if (url.startsWith("http://pics")) {
		url = url.replaceFirst("http://pics", "http://www.pics");
		response.sendRedirect(url);
		return;
	}
	
	SyndFeed feed = null;
	boolean feedValid;
	try {
		URL feedUrl = new URL("http://blog.picsauditing.com/?feed=rss2");
	
		SyndFeedInput input = new SyndFeedInput();
		feed = input.build(new XmlReader(feedUrl));
	
		feedValid = true;

	} catch (Exception e) {
		feedValid = false;
	}
%>
<html>
<head>
<title>Contractor Screening &amp; Contractor Management</title>
<meta name="color" content="#003366" />
<meta name="flashName" content="HOME" />

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery/jquery.cycle.all.min.js"></script>
<script src="js/AC_RunActiveContent.js" type="text/javascript"></script>
<script type="text/javascript">

$(document).ready(function() {
	$("#logoShow").cycle({
   		fx: 'scrollRight',
   		speed: 700,
   		timeout: 1800,
   		random: 1,
   		pause:  1
   	});
});

</script>

<style type="text/css">
<!--
.style1 {
	font-size: 11px;
	font-weight: bold;
}
-->
#logoShow { height: 150px; width: 150px; text-align: center; vertical-align: middle; position: relative; overflow: hidden; background-color: #FFFFFF; border: 1px solid #6699CC;}
#logoShow img { padding: 5px; }
#logoShow img:hover { }
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td width="200" valign="top">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<div id="logoShow">
								<img src="images/operators/client1.gif" />
								<img src="images/operators/client2.gif" />
								<img src="images/operators/client3.gif" />
								<img src="images/operators/client4.gif" />
								<img src="images/operators/client5.gif" />
								<img src="images/operators/client6.gif" />
								<img src="images/operators/client7.gif" />
								<img src="images/operators/client8.gif" />
								<img src="images/operators/client9.gif" />
								<img src="images/operators/client10.gif" />
								<img src="images/operators/client11.gif" />
								<img src="images/operators/client12.gif" />
							</div>
						</td>
						<td class="blueHome">PICS was established to assist companies with a thorough audit program for all
						contractors working at their facilities. PICS sets the highest standards possible in regard to contractor
						qualifications as they relate to federally or state-mandated regulations and/or specific operator requirements.</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td><br>
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td><img src="images/featured_op_Huntsman.jpg" alt="Featured Operator Huntsman" width="215" height="195"
							border="0" usemap="#Map"></td>
						<td width="13"><img src="images/spacer.gif" width="13" height="1"></td>
						<td><img src="images/feature_contractor.jpg" alt="Featured Contractor PS Environmental" width="215" height="195"
							border="0" usemap="#Map2"></td>
						<td width="13"><img src="images/spacer.gif" width="1" height="1"></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td valign="top">
		<% if(feedValid) { %>
		<table width="200" border="0" align="right" cellpadding="0" cellspacing="0">
			<tr>
				<td colspan="3"><img src="images/header_homeNews.jpg" alt="News" width="200" height="24"></td>
			</tr>
			<tr>
				<td bgcolor="#CCCCCC"><img src="images/spacer.gif" width="1" height="1"></td>
				<td valign="top" bgcolor="#FFFFFF">
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
					<% for (int i=0; i<3; i++) { 
						SyndEntry syndEntry = (SyndEntry) feed.getEntries().get(i);
					%>
						<tr>
							<td valign="top" bgcolor="F8F8F8" class="homeNews">
								<span class="style1">
									<span class="homeNewsDates"><%=DateBean.format(syndEntry.getPublishedDate(), "MM/dd/yy") %></span>
									<%=syndEntry.getTitle() %>
								</span>
							
							<br/>
							<br/>
							<% 
								if (syndEntry.getDescription().getValue() != null || syndEntry.getDescription().getValue().length() > 0)
									out.println(syndEntry.getDescription().getValue());
								else
									for (Object c : syndEntry.getContents()) {
										SyndContent content = (SyndContent) c;
										out.println(content.getValue());
									}
							%>
						</tr>
					<% } %>
					
					<tr>
						<td align="center" class="blueHome">&nbsp;<a href="featured_newsarchive.jsp" target="_self"><img
							src="images/NEWSARCHIVE_button3.gif" width="111" height="27" hspace="5" border="0"></a></td>
					</tr>
				</table>
				</td>
				<td bgcolor="#CCCCCC"><img src="images/spacer.gif" width="1" height="1"></td>
			</tr>
			<tr>
				<td height="7" colspan="3"><img src="images/footer_homeNews.gif" width="200" height="7"></td>
			</tr>
		</table>
		<% } %>
		</td>
	</tr>
</table>

<map name="Map">
	<area shape="rect" coords="5,20,209,191" href="featured_template.jsp" target="_self">
</map>
<map name="Map2">
	<area shape="rect" coords="0,22,216,195" href="featured_contractor.jsp" target="_self">
</map>
</body>
</html>
