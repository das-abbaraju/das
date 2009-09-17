<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div class="clear"></div>
<div id="thinking_notesList"></div>
<table class="notes">
	<thead>
		<tr><th colspan="3">Notes (<s:property value="noteCategory"/>)</th>
		</tr>
	</thead>
	<tbody>
		<pics:permission perm="EditNotes" type="Edit">
		<tr class="clickable"
				onclick="noteEditor('<s:property value="id"/>', 0, 'edit', '<s:property value="noteCategory"/>');">
			<td colspan="3" class="center">Click to add a new Note</td>
		</tr>
		</pics:permission>
		<s:iterator value="notes">
		<tr class="clickable"
				onclick="noteEditor('<s:property value="account.id"/>', '<s:property value="id" />','view', '<s:property value="noteCategory"/>')">
			<td class="center">
				<nobr><s:date name="creationDate" format="M/d/yy h:mm a" /></nobr><br />
				<nobr><s:property value="createdBy.name" /></nobr>
				<nobr>from <s:property value="createdBy.account.name" /></nobr>
			</td>
			<td class="priority"><img src="images/star<s:property value="priority" />.gif" 
				height="25" width="25" title="<s:property value="priority" /> Priority" /></td>
			<td>
				<s:property value="noteCategory" />:<s:property value="summary" /><br />
				<s:property value="bodyHtml" escape="false" />
			</td>
		</tr>
		</s:iterator>
		<s:if test="showMoreNotes">
		<tr>
			<td colspan="3" class="center"><a href="<s:property value="account.type"/>Notes.action?id=<s:property value="id" />">Show more Notes</a></td>
		</tr>
		</s:if>
	</tbody>
</table>
