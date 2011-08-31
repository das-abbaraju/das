<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th><s:text name="global.Contractor" /></th>
		<th><s:text name="UserOpenNotesAjax.Priority" /></th>
		<th><s:text name="global.Notes" /></th>
		</tr>
	</thead>
	<s:iterator value="openNotes">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="account.id"/>"><s:property value="account.name"/></a></td>
			<td class="priority"><img src="images/star<s:property value="priority" />.gif" 
				height="20" width="20" title="<s:property value="priority" /> Priority" />
			<td>
			<a href="#view" style="float: right; padding: 5px" 
				onclick="noteEditor('<s:property value="account.id"/>', '<s:property value="id" />','view')">
				<s:text name="UserOpenNotesAjax.ShowDetails" />
			</a>
			<s:property value="noteCategory" />:
			<s:property value="summary" /><s:if test="body != null"> ......</s:if>
			</td>
		</tr>
	</s:iterator>
	<s:if test="openNotes.size == 0">
		<tr>
			<td colspan="4" class="center"><s:text name="UserOpenNotesAjax.NoOpenNotesCurrently" /></td>
		</tr>
	</s:if>

</table>