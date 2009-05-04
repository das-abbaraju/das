<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
		<th>Contractor</th>
		<th>Priority</th>
		<th>Note</th>
		</tr>
	</thead>
	<s:iterator value="openNotes">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="account.id"/>"><s:property value="account.name"/></a></td>
			<td class="priority"><img src="images/star<s:property value="priority" />.gif" 
				height="20" width="20" title="<s:property value="priority" /> Priority" />
			<td>
			<a href="#view" style="float: right; padding: 5px" 
			onclick="noteEditor('<s:property value="account.id"/>', '<s:property value="id" />','view')">Show Details</a>
			<s:property value="noteCategory" />:
			<s:property value="summary" /><s:if test="body != null"> ......</s:if>
			</td>
		</tr>
	</s:iterator>
	<s:if test="openNotes.size == 0">
		<tr>
			<td colspan="4" class="center">No open notes currently</td>
		</tr>
	</s:if>

</table>