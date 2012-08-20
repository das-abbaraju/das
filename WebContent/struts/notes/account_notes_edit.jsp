<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>
			<s:text name="global.Notes" />
		</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<script type="text/javascript">
			function closePage() {
				try {
					<s:if test="embedded">
					opener.refreshNoteCategory('<s:property value="id"/>', '<s:property value="note.noteCategory"/>');
					</s:if>
					<s:else>
					opener.refresh('notes');
					</s:else>
					
					opener.focus();
				} catch(err) {}
				
				self.close();
			}
		</script>
	</head>
	<body>
		<s:include value="../actionMessages.jsp"></s:include>
		
		<s:form id="editNotes" enctype="multipart/form-data" method="POST">
			<s:hidden name="id" />
			<s:hidden name="account" value="%{id}" />
			<s:hidden name="note" />
			<s:hidden name="embedded" />
			
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="NoteEditor.header" /></h2>
				
				<ol>
					<li>
						<label>
							<s:text name="NoteEditor.Note" />:
						</label>
						<s:textfield name="note.summary" maxlength="150" size="60" />
					</li>
					<li>
						<label>
							<s:text name="global.Category" />:
						</label>
						<s:radio list="filter.categoryList" listValue="getText(getI18nKey())" name="note.noteCategory" theme="pics" cssClass="inline" />
					</li>
					
					<s:if test="permissions.requiresOQ || permissions.requiresCompetencyReview">
						<li>
							<label>
								<s:text name="global.Employee" />:
							</label>
							<s:select list="employeeList" listKey="id" name="employeeID" listValue="displayName" 
								headerKey="0" headerValue="- %{getText('Employee')} -" />
						</li>
					</s:if>
					
					<li>
						<label>
							<s:text name="UserOpenNotesAjax.Priority" />:
						</label>
						<s:radio list="filter.priorityList" listValue="getText(getI18nKey())" name="note.priority" theme="pics" cssClass="inline" />
					</li>
					<li>
						<label>
							<s:text name="global.Status" />:
						</label>
						<s:radio list="filter.statusList" listValue="getText(getI18nKey())" name="note.status" theme="pics" cssClass="inline" />
					</li>
					<li>
						<label>
							<s:text name="NoteEditor.CanViewParam">
								<s:param><s:property value="getAccountType()"/> </s:param>
							</s:text>
						</label>
						<s:checkbox name="note.canContractorView"></s:checkbox>
					</li>
					<li>
						<label>
							<s:text name="ContractorNotes.ViewableBy" />:
						</label>
						<s:radio list="viewableByList" name="viewableBy" theme="pics" cssClass="inline" value="3" />
						<s:select list="facilities" listKey="id" listValue="name" name="viewableByOther" 
							value="1100" />
					</li>
					<li id="liAdditionalText">
						<label>
							<s:text name="NoteEditor.AdditionalText" />:
						</label>
						<s:textarea name="note.body" cols="50" rows="6"></s:textarea>
					</li>
					<li>
						<label>
							<s:text name="ContractorFlag.FileAttachment" />
						</label>
						<s:file name="file"></s:file>
						
						<s:if test="note.attachment != null">
							<a href="NoteEditor!attachment.action?note=<s:property value="note"/>" target="_BLANK">
								<s:text name="ContractorNotes.ViewAttachment" />
							</a>
							<s:submit method="remove" value="%{getText('button.Remove')}" cssClass="picsbutton negative" />
						</s:if>
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form submit">
				<s:submit method="save" value="%{getText('button.Save')}" cssClass="picsbutton positive" />
				
				<s:if test="note.id > 0">
					<pics:permission perm="EditNotes" type="Delete">
						<s:submit method="hide" value="%{getText('button.Hide')}" cssClass="picsbutton negative" />
					</pics:permission>
				</s:if>
				
                <button name="button" class="picsbutton" type="button" value="cancel" onclick="closePage()">
                    <s:text name="button.Close" />
                </button>

			</fieldset>
		</s:form>
		
		<br clear="all" /><br />
	</body>
</html>