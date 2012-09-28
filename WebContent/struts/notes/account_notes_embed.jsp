<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<div class="clear"></div>
<div id="thinking_notesList"></div>

<table class="notes">
	<thead>
		<tr>
			<th colspan="3">
				<s:text name="BillingDetail.NotesHeader">
					<s:param><s:property value="noteCategory" /></s:param>
				</s:text>
			</th>
		</tr>
	</thead>
	<tbody>
		<pics:permission perm="EditNotes" type="Edit">
			<tr class="clickable" onclick="noteEditor('<s:property value="id"/>', 0, 'edit', '<s:property value="noteCategory"/>');">
				<td colspan="3" class="center">
					<s:text name="Notes.ClickToAddNote" />
				</td>
			</tr>
		</pics:permission>
		
		<s:iterator value="notes">
			<tr class="clickable" onclick="noteEditor('<s:property value="account.id"/>', '<s:property value="id" />','view', '<s:property value="noteCategory"/>')">
				<td class="center">
					<nobr><s:date name="creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Datetime}" /></nobr>
					<br />
					<nobr><s:property value="createdBy.name" /></nobr>
					<nobr>
						<s:text name="BillingDetail.NoteFrom">
							<s:param><s:property value="createdBy.account.name" /></s:param>
						</s:text>
					</nobr>
				</td>
				<td class="priority">
					<img src="images/star<s:property value="priority" />.gif" height="25" width="25" title="<s:property value="priority" /> Priority" />
				</td>
				<td>
					<s:property value="noteCategory" />:
					<s:property value="summary" />
					<s:if test="body != null">
						<br />
						<s:if test="body.length() > 150">
							<s:property value="bodyHtml.substring(0,150)" escape="false" />
						</s:if>
						<s:else>
							<s:property value="bodyHtml" escape="false" />
						</s:else>
					</s:if>
					<s:if test="attachment != null">
						<br /><br />
						<a href="NoteEditor.action?button=attachment&note=<s:property value="id"/>" target="_BLANK">
						<s:text name="ContractorNotes.ViewAttachment" /></a>
					</s:if>
				</td>
			</tr>
		</s:iterator>
		
		<s:if test="showMoreNotes">
			<tr>
				<td colspan="3" class="center">
					<a href="<s:property value="account.type"/>Notes.action?id=<s:property value="id" />"><s:text name="Notes.ShowMoreNotes" /></a>
				</td>
			</tr>
		</s:if>
	</tbody>
</table>