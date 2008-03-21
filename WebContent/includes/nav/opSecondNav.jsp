<%	if (permissions.isOperator() || permissions.isCorporate()) { %>
		| <a class=blueMain href=manage_forms.jsp?isSubmitted=No&id=<%=permissions.getAccountId()%>>Forms & Docs</a> |
<%		if (pBean.oBean.seesAllContractors() && permissions.hasPermission(com.picsauditing.access.OpPerms.SearchContractors)) {%>
			<a class=blueMain href=contractorsSearch.jsp>Search For New Contractors</a> | 
<%		}
		if (permissions.hasPermission(com.picsauditing.access.OpPerms.EditUsers)){%>
			<a class=blueMain href=users_manage.jsp>Edit User Accounts</a> |
<%		}%>
      <br/>
<%	}%>