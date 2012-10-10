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
					<s:param>
						${noteCategory}
					</s:param>
				</s:text>
			</th>
		</tr>
	</thead>
	<tbody>
		<pics:permission perm="EditNotes" type="Edit">
			<tr class="clickable" onclick="noteEditor('${id}', 0, 'edit', '${noteCategory}');">
				<td colspan="3" class="center">
					<s:text name="Notes.ClickToAddNote" />
				</td>
			</tr>
		</pics:permission>
		
		<s:iterator value="notes" var="note_item">
			<tr class="clickable" onclick="noteEditor('${note_item.account.id}', '${note_item.id}','view', '${note_item.noteCategory}')">
				<td class="center">
					<nobr><s:date name="creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Datetime}" /></nobr>
					<br />
					<nobr>${note_item.createdBy.name}</nobr>
					<nobr>
						<s:text name="BillingDetail.NoteFrom">
							<s:param>${note_item.createdBy.account.name}</s:param>
						</s:text>
					</nobr>
				</td>
				<td class="priority">
					<img src="images/star${note_item.priority}.gif" height="25" width="25" title="${note_item.priority} Priority" />
				</td>
				<td>
					${note_item.noteCategory}:
					${note_item.summary}
					<s:if test="!isStringEmpty(#note_item.body)">
						<br />
						${note_item.getBodyHtml(150)}
					</s:if>
					<s:if test="attachment != null">
						<br /><br />
						<s:url action="NoteEditor" var="note_editor">
							<s:param name="button">
								attachment
							</s:param>
							<s:param name="note">
								${note_item.id}
							</s:param>
						</s:url>
						<a href="${note_editor}" target="_BLANK">
						<s:text name="ContractorNotes.ViewAttachment" /></a>
					</s:if>
				</td>
			</tr>
		</s:iterator>
		
		<s:if test="showMoreNotes">
			<tr>
				<td colspan="3" class="center">
					<a href="${account.type}Notes.action?id=${id}">
						<s:text name="Notes.ShowMoreNotes" />
					</a>
				</td>
			</tr>
		</s:if>
	</tbody>
</table>
