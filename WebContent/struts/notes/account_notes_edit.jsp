<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:text name="global.Notes" /></title>
		
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
			<s:hidden name="id"/>
			<s:hidden name="note.id"/>
			<s:hidden name="embedded"/>
			
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="NoteEditor.header" /></h2>
				
				<ol>
					<li>
						<label><s:text name="NoteEditor.Note" />:</label>
						<s:textfield name="note.summary" maxlength="150" size="60" />
					</li>
					<li>
						<label><s:text name="global.Category" />:</label>
						<span style=""><s:radio list="filter.categoryList" name="note.noteCategory" /></span>
					</li>
					
					<s:if test="permissions.requiresOQ || permissions.requiresCompetencyReview">
						<li>
							<label><s:text name="global.Employee" />:</label>
							<s:select list="employeeList" listKey="id" name="employeeID" listValue="displayName" headerKey="0" headerValue="- Employee -" />
						</li>
					</s:if>
					
					<li>
						<label><s:text name="UserOpenNotesAjax.Priority" />:</label>
						<s:radio list="filter.priorityList" name="note.priority" />
					</li>
					<li>
						<label><s:text name="global.Status" />:</label>
						<s:radio list="filter.statusList" name="note.status" />
					</li>
					<li>
						<label>
							<s:text name="NoteEditor.CanView">
								<s:param><s:property value="account.type"/> </s:param>
							</s:text>
						</label>
						<s:checkbox name="note.canContractorView"></s:checkbox>
					</li>
					<li>
						<label><s:text name="ContractorNotes.ViewableBy" />:</label>
						<s:radio list="viewableByList" name="viewableBy"></s:radio>
						<s:select list="facilities" listKey="id" listValue="name" name="viewableByOther"></s:select>			
					</li>
					<li id="liAdditionalText">
						<label><s:text name="NoteEditor.AdditionalText" />:</label>
						<s:textarea name="note.body" cols="50" rows="6"></s:textarea>
					</li>
					<li>
						<label><s:text name="ContractorFlag.FileAttachment" /></label>
						<s:file name="file"></s:file>
						
						<s:if test="note.attachment != null">
							<a href="NoteEditor.action?button=attachment&note.id=<s:property value="note.id"/>" target="_BLANK">
								<s:text name="ContractorNotes.ViewAttachment" />
							</a>
							<a href="NoteEditor.action?id=<s:property value="id"/>&note.id=<s:property value="note.id"/>&button=remove&embedded=<s:property value="embedded"/>" class="remove">
								<s:text name="global.Remove" />
							</a>
						</s:if>
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form submit">
				<input class="picsbutton positive" name="button" type="submit" value="<s:text name="button.Save" />" />
				
				<s:if test="note.id > 0">
					<pics:permission perm="EditNotes" type="Delete">
						<input class="picsbutton negative" name="button" type="submit" value="<s:text name="button.Hide" />" />
					</pics:permission>
				</s:if>
				
				<input class="picsbutton" name="button" type="button" value="<s:text name="button.Close" />" onclick="closePage();" />
			</fieldset>
		</s:form>
		
		<br clear="all" /><br />
	</body>
</html>