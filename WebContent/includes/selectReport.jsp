<%
com.picsauditing.access.Menu reportMenu = new com.picsauditing.access.Menu();
reportMenu.fillPicsMenu(permissions);
%>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td colspan="2" align="center" class="redMain">
		<form name="form100" method="post" action="reports.jsp">
			<select id="report" name="report" class="forms" onChange="this.form.submit()">
				<option>-- Select a report --</option>
			<%
			for(com.picsauditing.access.MenuItem item : reportMenu.getValidItems()) {
				%>
				<option value="<%=item.getUrl()%>"><%=item.getPrompt()%></option>
				<%
			}
			%>
			</select>
		</form>
	</td>
</tr>
</table>
