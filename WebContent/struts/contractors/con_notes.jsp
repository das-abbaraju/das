<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
<s:include value="conHeader.jsp" />
<s:form id="editNotes">
<s:hidden name="id"/>
<s:if test="openTasks.size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Open Task</th>
				<th>FollowUpDate</th>
				<pics:permission perm="EditNotes" type="Edit">
					<td></td>
				</pics:permission>
				<pics:permission perm="EditNotes" type="Delete">
					<td></td>
				</pics:permission>
			</tr>
		</thead>
		<s:iterator value="openTasks">
			<s:hidden name="noteID" value="%{id}"/>
			<tr>
				<td><b>Note Category: </b><s:property value="noteCategory" />
				<b>Priority:</b> <s:property value="priority" /><br /><br/>
				<b>Created By :</b> <s:property value="createdBy.name" /> <b>Created
				On:</b> <s:date name="creationDate" format="M/d/yy" />
				<s:if test="updatedBy.name.length() > 0">
					<br /><br/>
					<b>Updated By :</b> <s:property value="updatedBy.name" /><b>Updated
					On:</b> <s:date name="updateDate" format="M/d/yy" />
				</s:if>
				</td>
				<td><s:property value="summary" /></td>
				<td><s:date name="followupDate" format="M/d/yy" /></td>
				<pics:permission perm="EditNotes" type="Edit">
					<td><div class="buttons"><button name="button" type="submit" value="add">Edit</button></div></td>
				</pics:permission>
				<pics:permission perm="EditNotes" type="Delete">
					<td><div class="buttons"><button name="button" class="negative" type="submit" value="delete">Delete</button></div></td>
				</pics:permission>
			</tr>
		</s:iterator>
	</table>
</s:if>

<s:if test="notes.size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Summary</th>
				<pics:permission perm="EditNotes" type="Edit">
					<td></td>
				</pics:permission>	
				<pics:permission perm="EditNotes" type="Delete">
					<td></td>
				</pics:permission>
			</tr>
		</thead>
		<s:iterator value="notes">
			<tr>
				<td><b>Note Category: </b><s:property value="noteCategory" />
				<b>Priority:</b> <s:property value="priority" /><br /><br />
				<b>Created By :</b> <s:property value="createdBy.name" /> <b>Created
				On:</b> <s:date name="creationDate" format="M/d/yy" />
				<s:if test="updatedBy.name.length() > 0">
					<br /><br/>
					<b>Updated By :</b> <s:property value="updatedBy.name" /> <b>Updated
					On:</b> <s:date name="updateDate" format="M/d/yy" />
				</s:if>
				</td>
				<td><s:property value="summary" /></td>
				<pics:permission perm="EditNotes" type="Edit">
					<td><div class="buttons"><button name="button" type="submit" value="add">Edit</button></div></td>
				</pics:permission>
				<pics:permission perm="EditNotes" type="Delete">
					<td><div class="buttons"><button name="button" class="negative" type="submit" value="delete">Delete</button></div></td>
				</pics:permission>
			</tr>
		</s:iterator>
	</table>
</s:if>
<div>
Priority:
<s:select list="priorityList" value="note.priority"></s:select>
Note Category:
<s:select list="noteCategoryList" value="note.noteCategory"></s:select>
Can Contractor View:
<s:checkbox name="note.canContractorView"></s:checkbox><br/>
Note Status: <s:select list="noteStatus" value="note.status"></s:select>
Followup Date: <input name="followupDate" id="followupDate" 
 type="text" size="10" 
 value="<s:date name="note.followupDate" format="MM/dd/yyyy" />" />
 <a href="#" 
 id="anchorfollowupDate" name="anchorfollowupDate" 
 onclick="cal1.select($('followupDate'), 'anchorfollowupDate','MM/dd/yyyy'); return false;">
 <img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
<br/>
ViewableBy : <span id="operators"><s:select name="note.account.id" 
  list="facilities" listKey="id" listValue="name" /></span>


<br/>
Note: <s:textarea name="note.summary" cols="45" rows="5"></s:textarea><br/>
Body: <s:textarea name="note.body" cols="45" rows="15"></s:textarea>

<pics:permission perm="EditNotes" type="Edit">
	<td><div class="buttons"><button class="positive" name="button" type="submit" value="add">Save</button></div></td>
</pics:permission>

</div>
</s:form>
<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>
</body>
</html>