<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<pics:permission perm="EditNotes" type="Edit">
	<div>
		<input type="button" class="picsbutton positive" name="button" id="addNote" onclick="noteEditor('<s:property value="id"/>', 0, 'edit');" value="<s:text name="VerifyView.button.AddNote" />" />
	</div>
</pics:permission>

<table class="notes">
	<thead>
		<tr>
			<th colspan="3">
				<s:form id="notesForm" onsubmit="runNoteSearch('notes'); return false;">
					<s:hidden name="id"></s:hidden>
					<s:hidden name="returnType" value="notes"></s:hidden>
					
					<s:select list="filter.priorityList" headerKey="" headerValue="- Priority -" onchange="runNoteSearch('notes')" cssClass="forms" name="filter.priority" />
					<s:select list="filter.categoryList" headerKey="" headerValue="- Category -" onchange="runNoteSearch('notes')" cssClass="forms" name="filter.category" />
					<s:textfield onchange="runNoteSearch('notes')" cssClass="forms" name="filter.keyword" size="10"></s:textfield>
					
					<s:if test="countRows > 0">
						<span style="color :#FFFFFF;">
							<s:if test="previous">
								<a href="#" onclick="updateNotePage(<s:property value="id"/>,'hasPrevious','notes', <s:property value="filter.firstResult"/>); return false;" style="color:#FFFFFF">
									<s:text name="Filters.paging.Newer" />
								</a>
								<
							</s:if>
							
							<s:text name="Filters.paging.ShowResults">
								<s:param><s:property value="filter.firstResult + 1"/></s:param>
								<s:param>
									<s:if test="!next">
										<s:property value="filter.firstResult + countRows" />
									</s:if>
									<s:else>
										<s:property value="filter.limit + filter.firstResult" />
									</s:else>
								</s:param>
							</s:text>
							
							<s:if test="next">
								>
								<a href="#" onclick="updateNotePage(<s:property value="id"/>,'hasNext','notes', <s:property value="filter.firstResult"/>); return false;" style="color:#FFFFFF">
									<s:text name="Filters.paging.Older" />
								</a>
							</s:if>
						</span>
					</s:if> 
				</s:form>
				
				<ul class="filters">
					<s:if test="filter.userID.length > 0">
						<li>
							<a href="#" onclick="filter('notes', 'filter.userID', ''); return false;" class="remove">
								<s:text name="User" /> = <s:property value="filter.userID" />
							</a>
						</li>
					</s:if>
					
					<s:if test="filter.userAccountID.length > 0">
						<li>
							<a href="#" onclick="filter('notes', 'filter.userAccountID', ''); return false;" class="remove">
								<s:text name="global.Company" /> = <s:property value="filter.userAccountID" />
							</a>
						</li>
					</s:if>
					
					<s:if test="filter.viewableBy.length > 0">
						<li>
							<a href="#" onclick="filter('notes', 'filter.viewableBy', ''); return false;" class="remove">
								<s:text name="ContractorNotes.ViewableBy" /> = <s:property value="filter.viewableBy" />
							</a>
						</li>
					</s:if>
				</ul>
			</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="activity" var="bean">
			<tr>
				<td class="center">
					<nobr>
						<s:date name="creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Datetime}" />
					</nobr><br />
					
					<nobr>
						<a class="filter" href="#" onclick="filter('notes', 'filter.userID', '<s:property value="createdBy.id" />'); return false;">
							<s:property value="createdBy.name" />
						</a> - 
						<a class="filter" href="#" onclick="filter('notes', 'filter.userAccountID', '<s:property value="createdBy.account.id" />'); return false;">
							<s:property value="createdBy.account.name" />
						</a>
					</nobr>
				</td>
				<td class="priority">
					<s:if test='"None".equals(#bean.priority.name())'>
					</s:if>
					<s:else>
					<a href="#" onclick="filter('notes', 'filter.priority', '<s:property value="priority" />'); return false;">
						<img src="images/star<s:property value="priority" />.gif" height="25" width="25" title="<s:property value="priority" /> Priority" />
					</a>
					</s:else>
				</td>
				<td>
					<s:if test='#bean.hasDetails()'>
					<a href="#view" style="float: right; padding: 5px" onclick="noteEditor('<s:property value="account.id"/>', '<s:property value="id" />','view')">
						<s:text name="UserOpenNotesAjax.ShowDetails" />
					</a>
					</s:if>
					
					<a class="filter" href="#" onclick="filter('notes', 'filter.category', '<s:property value="noteCategory" />'); return false;">
						<s:property value="noteCategory" />
					</a>:

					
					<s:property value="summary" escape="false"/>
					
					<s:if test="body != null">
						<br />
						<s:property value="#bean.getBodyHtml(150)" escape="false"/>
					</s:if>
					
					<br />
					
					<s:if test="attachment != null">
						<br /><a href="NoteEditor!attachment.action?note=<s:property value="id"/>" target="_BLANK">
							<s:text name="ContractorNotes.ViewAttachment" />
						</a>
						
						<br />
					</s:if>
					
					<s:if test="updateDate != null && updateDate.after(creationDate)">
						<br /><s:text name="ContractorNotes.EditedBy">
							<s:param><s:property value="updatedBy.name" /></s:param>
							<s:param><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Datetime}" /></s:param>
						</s:text>
					</s:if>
				</td>
			</tr>
		</s:iterator>
		
		<tr>
			<th colspan="3">
				<s:if test="countRows > 0">
					<span style="color :#FFFFFF;">
						<s:if test="previous">
							<a href="#" onclick="updateNotePage(<s:property value="id"/>,'hasPrevious','notes', <s:property value="filter.firstResult"/>); return false;" style="color:#FFFFFF">
								<s:text name="Filters.paging.Newer" />
							</a>
							<
						</s:if>
						
						<s:text name="Filters.paging.ShowResults">
							<s:param><s:property value="filter.firstResult + 1"/></s:param>
							<s:param>
								<s:if test="!next">
									<s:property value="countRows" />
								</s:if>
								<s:else>
									<s:property value="filter.limit + filter.firstResult" />
								</s:else>
							</s:param>
						</s:text>
						
						<s:if test="next">
							>
							<a href="#" onclick="updateNotePage(<s:property value="id"/>,'hasNext','notes', <s:property value="filter.firstResult"/>); return false;" style="color:#FFFFFF">
								<s:text name="Filters.paging.Older" />
							</a>
						</s:if>
					</span>
				</s:if> 
			</th>
		</tr>
	</tbody>
</table>