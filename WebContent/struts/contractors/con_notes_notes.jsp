<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<pics:permission perm="EditNotes" type="Edit">
	<div class="buttons">
		<button class="positive" name="button" type="button" 
			onclick="noteEditor('<s:property value="id"/>', 0, 'edit');">Add Note</button>
	</div>
</pics:permission>
<div id="thinking_notesList">&nbsp;</div>
<table class="notes">
	<thead>
		<tr>
			<th colspan="4">
				<s:form id="notesForm">
					<s:hidden name="id"></s:hidden>
					<s:hidden name="returnType" value="notes"></s:hidden>
					<s:select list="filter.priorityList" headerKey="" headerValue="- Priority -"
						onchange="runSearch('notes')" cssClass="forms" name="filter.priority" />
					<s:select list="filter.categoryList" headerKey="" headerValue="- Category -"
						onchange="runSearch('notes')" cssClass="forms" name="filter.category" />
					<s:textfield onchange="runSearch('notes')" cssClass="forms" name="filter.keyword" size="10"></s:textfield>
				</s:form>
				<ul class="filters">
					<s:if test="filter.userID.length > 0">
						<li><a href="#" onclick="filter('notes', 'filter.userID', ''); return false;" class="remove" >User = <s:property value="filter.userID" /></a></li>
					</s:if>
					<s:if test="filter.userAccountID.length > 0">
						<li><a href="#" onclick="filter('notes', 'filter.userAccountID', ''); return false;" class="remove" >Company = <s:property value="filter.userAccountID" /></a></li>
					</s:if>
					<s:if test="filter.viewableBy.length > 0">
						<li><a href="#" onclick="filter('notes', 'filter.viewableBy', ''); return false;" class="remove" >Viewable by = <s:property value="filter.viewableBy" /></a></li>
					</s:if>
				</ul>
			</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="notes">
		<tr>
			<td class="center">
				<nobr><s:date name="creationDate" format="M/d/yy h:mm a" /></nobr><br />
				<a class="filter" href="#" onclick="filter('notes', 'filter.userID', '<s:property value="createdBy.id" />'); return false;"><s:property 
					value="createdBy.name" /></a><br />
				<a class="filter" href="#" onclick="filter('notes', 'filter.userAccountID', '<s:property value="createdBy.account.id" />'); return false;"><s:property 
					value="createdBy.account.name" /></a><br />
				<a class="filter" href="#" onclick="filter('notes', 'filter.viewableBy', '<s:property value="viewableBy.id" />'); return false;"><s:property 
					value="viewableBy.name" /></a> can see<br />
			</td>
			<td class="priority"><a href="#" onclick="filter('notes', 'filter.priority', '<s:property value="priority" />'); return false;"><img src="images/star<s:property value="priority" />.gif" 
				height="25" width="25" title="<s:property value="priority" /> Priority" /></a></td>
			<td>
				<a href="#view" style="float: right; padding: 5px" 
					onclick="noteEditor('<s:property value="account.id"/>', '<s:property value="id" />','view')">Show Details</a>
				<a class="filter" href="#" onclick="filter('notes', 'filter.category', '<s:property value="noteCategory" />'); return false;"><s:property value="noteCategory" /></a>:
					<s:property value="summary" /><br /><br />
				<s:if test="updateDate != null && updateDate.after(creationDate)">
					<b>Edited:</b> by <s:property value="updatedBy.name" /> at <s:date name="updateDate" format="M/d/yy h:mm a" />
				</s:if>
			</td>
			<td class="right">
				<a href="#edit" onclick="noteEditor('<s:property value="account.id"/>', '<s:property value="id" />','edit')" class="edit">Edit</a>
			</td>
		</tr>
		</s:iterator>
	</tbody>
</table>
