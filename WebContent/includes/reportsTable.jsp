<%
com.picsauditing.access.Menu reportMenu = new com.picsauditing.access.Menu();
reportMenu.fillPicsMenu(permissions);
%>
<table border="0" cellpadding="1" cellspacing="1">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan="2">Click Report Name to Access</td>
	</tr>
<%
int rowCount = 0;
for(com.picsauditing.access.MenuItem item : reportMenu.getValidItems()) {
	rowCount++;
	%>
	<tr class="blueMain" <%=Utilities.getBGColor(rowCount)%>>
		<td align="right"><%=rowCount%>.</td>
		<td><a href="<%=item.getUrl()%>"><%=item.getPrompt()%></a></td>
	</tr>
	<%
}
%>
</table>
