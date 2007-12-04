<%	if (pBean.isOperator() || pBean.isCorporate()){ %>
      | <a class=blueMain href=manage_forms.jsp?isSubmitted=No&id=<%=pBean.userID%>>Forms & Docs</a> |
<%		if(pBean.oBean.seesAllContractors() && pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.SearchContractors)){%>
      <a class=blueMain href=contractorsSearch.jsp>Search For New Contractors</a> | 
<%		}//if
		if (pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.EditUsers)){%>
      <a class=blueMain href=accounts_userList.jsp?id=<%=pBean.userID%>>Edit User Accounts</a> |
<%		}//if%>
      <br/>
<%	}//if%>	