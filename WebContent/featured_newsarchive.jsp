<%@page import="com.sun.syndication.io.XmlReader" %>
<%@page import="com.sun.syndication.io.SyndFeedInput" %>
<%@page import="com.sun.syndication.feed.synd.SyndFeed" %>
<%@page import="com.sun.syndication.feed.synd.SyndEntry" %>
<%@page import="com.sun.syndication.feed.synd.SyndContent"%>
<%@page import="com.picsauditing.PICS.DateBean"%>
<%@page import="java.net.URL" %>
<%	
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
<title>News Archive</title>
<meta name="color" content="#003366" />
<meta name="flashName" content="HOME" />
<meta name="iconName" content="news" />
</head>
<body>
<table border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="7" colspan="3"><img src="images/header_news.gif"
			alt="header" width="657" height="7"></td>
	</tr>
	<tr>
		<td bgcolor="#CCCCCC"><img src="images/spacer.gif" alt="spacer"
			width="1" height="1"></td>
		<td valign="top" bgcolor="#FFFFFF">
		<table border="0" align="center" cellpadding="0" cellspacing="0">
			<% for (Object entry : feed.getEntries()) {
				SyndEntry syndEntry = (SyndEntry) entry;
			%>
				<tr>
					<td class="blueHome2">
						<strong><%=DateBean.format(syndEntry.getPublishedDate(), "MM/dd/yy") %></strong> 
						<strong class="articleTitle"><%=syndEntry.getTitle()%></strong>
						<% for (Object c : syndEntry.getContents()) {
							SyndContent content = (SyndContent) c;
							out.println(content.getValue());
						} 
						%>
						
					</td>
				</tr>
			<% } %>
		</table>
		</td>
		<td bgcolor="#CCCCCC"><img src="images/spacer.gif" alt="spacer"
			width="1" height="1"></td>
	</tr>
	<tr>
		<td height="7" colspan="3"><img src="images/footer_news.gif"
			alt="footer" width="657" height="7"></td>
	</tr>
</table>
</body>
</html>
