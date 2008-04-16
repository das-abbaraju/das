<%@page import="com.picsauditing.jpa.entities.ContractorAudit"%>
<%@page import="com.picsauditing.jpa.entities.User"%>
<%
ContractorAudit conAudit = action.getAudit();
User auditor = conAudit.getAuditor();
%>
<%@page import="com.picsauditing.PICS.Utilities"%>
<%@page import="com.picsauditing.PICS.DateBean"%>
<table border="0" cellspacing="0" cellpadding="1">
	<tr align="center" class="blueMain">
		<td width="676"><%@ include file="secondNav.jsp"%></td>
	</tr>
</table>
<table border="0" cellspacing="0" cellpadding="1">
	<tr align="center" class="blueMain">
		<td class="blueHeader" colspan="5"><%=action.getAudit().getAuditType().getAuditName()%></td>
	</tr>
	<tr class="blueMain">
		<td align="right">Status:</td>
		<td><%=conAudit.getAuditStatus()%></td>
		<td width="20">&nbsp;</td>
		<% if(conAudit.getRequestingOpAccount() != null) { %>
		<td align="right">For:</td>
		<td><%=conAudit.getRequestingOpAccount().getName()%></td>
		<% } else { %>
		<td colspan="2"></td>
		<% } %>
	</tr>
	<tr class="blueMain">
		<% if(conAudit.getAuditType().isHasAuditor()) { %>
		<td align="right">Auditor:</td>
		<td><%= (conAudit.getAuditor() == null) ? "Not Assigned" : conAudit.getAuditor().getName() %></td>
		<% } else { %>
		<td colspan="2"></td>
		<% } %>
		<td width="20">&nbsp;</td>
		<% if(conAudit.getAuditType().isScheduled()) { %>
		<td align="right">Scheduled:</td>
		<td><%=DateBean.toShowFormat(conAudit.getScheduledDate())%> <%=conAudit.getAuditLocation()%></td>
		<% } else { %>
		<td colspan="2"></td>
		<% } %>
	</tr>
	<tr class="blueMain">
		<td align="right">Created:</td>
		<td><%=DateBean.toShowFormat(conAudit.getCreatedDate())%></td>
		<td width="20">&nbsp;</td>
		<td align="right">Completed:</td>
		<td><%=DateBean.toShowFormat(conAudit.getCompletedDate())%> <%=conAudit.getPercentComplete()%>%</td>
	</tr>
	<tr class="blueMain">
		<% if(conAudit.getAuditType().isHasRequirements()) { %>
		<td align="right">Closed:</td>
		<td><%=DateBean.toShowFormat(conAudit.getCompletedDate())%> <%=conAudit.getPercentVerified()%>%</td>
		<% } else { %>
		<td colspan="2"></td>
		<% } %>
		<td width="20">&nbsp;</td>
		<td align="right">Expires:</td>
		<td><%=DateBean.toShowFormat(conAudit.getExpiresDate())%></td>
	</tr>
</table>

<a class="blueMain" href="pqf_view.jsp?auditID=<%=conAudit.getId()%>">View</a> |
<%
	if (permissions.isPicsEmployee()) {
	%>
	<a class="blueMain" href="pqf_editMain.jsp?auditID=<%=conAudit.getId()%>">Edit</a> |
	<a class="blueMain" href="pqf_verify.jsp?auditID=<%=conAudit.getId()%>">Verify</a> |
	<%
}
%>
<a class="blueMain" href="pqf_viewAll.jsp?auditID=<%=conAudit.getId()%>">View All</a> |
<a class="blueMain" href="pqf_printAll.jsp?auditID=<%=conAudit.getId()%>">Print</a>

