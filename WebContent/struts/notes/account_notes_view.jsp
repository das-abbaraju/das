<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:text name="global.Notes" /></title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
		
		<style>
		div.auditHeader fieldset {
			width: 40%;
		}
		</style>
	</head>
	<body>
		<h1><s:property value="account.name"/></h1>
		
		<div class="buttons" style="float: right">
			<a class="picsbutton" onclick="window.close();" href="#">
				<s:text name="button.Close" />
			</a>
			
			<s:if test="permissions.admin || permissions.userId == note.createdBy.id">
				<pics:permission perm="EditNotes" type="Edit">
					<a class="picsbutton" href="?id=<s:property value="id"/>&note=<s:property value="note"/>&mode=edit&embedded=<s:property value="embedded"/>">
						<s:text name="button.Edit" />
					</a>
				</pics:permission>
			</s:if>
		</div>
		
		<div id="auditHeader" class="auditHeader">
			<fieldset>
				<ul>
					<li>
						<label><s:text name="global.Category" />:</label>
						<s:property value="note.noteCategory"/>
					</li>
					
					<s:if test="note.employee != null && (permissions.requiresOQ || permissions.requiresCompetencyReview)">
						<li>
							<label><s:text name="global.Employee" />:</label>
							<s:property value="note.employee.displayName"/>
						</li>
					</s:if>
					
					<li>
						<label><s:text name="UserOpenNotesAjax.Priority" />:</label>
						<s:property value="note.priority"/>
					</li>
					<li>
						<label><s:text name="global.Status" />:</label>
						<s:property value="note.status"/>
					</li>
				</ul>
			</fieldset>
			
			<fieldset>
				<ul>
					<s:if test="note.canContractorView">
						<li>
							<label><s:property value="account.type"/>:</label>
							<s:text name="NoteEditor.Viewable" />
						</li>
					</s:if>
					
					<li>
						<label><s:text name="ContractorNotes.ViewableBy" />:</label>
						<s:property value="note.viewableBy.name"/>
						
						<s:if test="note.viewableBy.id == 1 && !note.canContractorView">
							<s:text name="NoteEditor.Except">
								<s:param><s:property value="account.type"/></s:param>
							</s:text>
						</s:if>
					</li>
					<li>
						<label><s:text name="global.CreatedBy" />:</label>
						<s:property value="note.createdBy.name"/> - <s:property value="note.createdBy.account.name"/>
					</li>
					<li>
						<label><s:text name="Filters.label.CreatedDate" />:</label>
						<s:date name="note.creationDate"/>
					</li>
				</ul>
			</fieldset>
			
			<div class="clear"></div>
		</div>
		
		<div>
			<s:property value="note.summary"/>
		</div>
		<div>
			<s:property value="note.bodyHtml" escape="false"/>
		</div>
		
		<s:if test="note.attachment != null">
			<div>
				<a href="NoteEditor!attachment.action?note=<s:property value="note"/>" target="_BLANK">
					<s:text name="ContractorNotes.ViewAttachment" />
				</a>
			</div>
		</s:if>
		
		<div>
			<button name="button" class="picsbutton" type="button" value="cancel" onclick="window.close();">
				<s:text name="button.Close" />
			</button>
		</div>
	</body>
</html>