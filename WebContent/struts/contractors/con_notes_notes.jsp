<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>

<div id="thinking_notesList"></div>

<table class="notes">
	<thead>
		<tr>
			<th colspan="4">
				<ul class="filters">
					<s:if test="filterPriority != null">
						<li><a href="#" onclick="filter('notes', 'filterPriority', ''); return false;" class="remove" >Priority >= <s:property value="filterPriority" /></a></li>
					</s:if>
					<s:if test="filterCategory != null">
						<li><a href="#" onclick="filter('notes', 'filterCategory', ''); return false;" class="remove" >Category = <s:property value="filterCategory" /></a></li>
					</s:if>
					<s:if test="filterUserID > 0">
						<li><a href="#" onclick="filter('notes', 'filterUserID', '0'); return false;" class="remove" >User = <s:property value="filterUserID" /></a></li>
					</s:if>
					<s:if test="filterUserAccountID > 0">
						<li><a href="#" onclick="filter('notes', 'filterUserAccountID', '0'); return false;" class="remove" >Company = <s:property value="filterUserAccountID" /></a></li>
					</s:if>
				</ul>
			</th>
		</tr>
	</thead>
	<tbody>
		<pics:permission perm="EditNotes" type="Edit">
			<tr><td class="center" colspan="4">
				<input type="button" onclick="noteEditor('<s:property value="id"/>');" value="Add Note">
			</td></tr>
		</pics:permission>
		<s:iterator value="notes">
		<tr>
			<td class="center">
				<a href="#" onclick="filter('notes', 'filterUserID', '<s:property value="createdBy.id" />'); return false;"><s:property 
					value="createdBy.name" /></a><br />
				<a href="#" onclick="filter('notes', 'filterUserAccountID', '<s:property value="createdBy.account.id" />'); return false;"><s:property 
					value="createdBy.account.name" /></a><br />
				<s:if test="!createdBy.equals(viewableBy)">
					Seen by: <a href="#" onclick="filter('notes', 'filterViewableByID', '<s:property value="viewableBy.account.id" />'); return false;"><s:property 
						value="viewableBy.account.name" /></a><br />
				</s:if>

				<nobr><s:date name="creationDate" format="M/d/yy h:mm a" /></nobr>
			</td>
			<td class="priority"><a href="#" onclick="filter('notes', 'filterPriority', '<s:property value="priority" />'); return false;"><img src="images/star<s:property value="priority" />.gif" 
				height="25" width="25" title="<s:property value="priority" /> Priority" /></a></td>
			<td>
				<a href="#" onclick="filter('notes', 'filterCategory', '<s:property value="noteCategory" />'); return false;"><s:property value="noteCategory" /></a>: <s:property value="summary" />
				<br />
				<br />
				<s:if test="updateDate != null && updateDate.after(creationDate)">
					<b>Edited:</b> by <s:property value="updatedBy.name" /> at <s:date name="updateDate" format="M/d/yy h:mm a" />
				</s:if>
			</td>
			<td class="center">
				<a href="#edit" onclick="showEditNotes(<s:property value="id" />);" class="edit">Edit</a>
				<a href="javascript: remove('notes', <s:property value="id" />);" class="remove" onclick="return confirm('Are you sure you want to remove this note?');">Hide</a>
			</td>
		</tr>
		</s:iterator>
	</tbody>
</table>
