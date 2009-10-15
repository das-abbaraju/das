<%@ taglib prefix="s" uri="/struts-tags"%>
<s:form enctype="multipart/form-data" method="POST">
	<div style="background-color: #F9F9F9;"><s:hidden name="auditID" />
	<s:hidden name="fileID" />
	<div class="question"><label>File:</label> <s:file name="file"
		value="%{file}" size="50"></s:file><br />
	</div>
	<s:if test="file != null && file.exists()">
		<div class="question"><a
			href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>&fileID=<s:property value="fileID"/>&button=download"
			target="_BLANK">Open Existing <s:property value="fileSize" />
		File</a></div>
	</s:if>
	<div class="question shaded"><label>Description:</label> <s:textfield
		name="fileName" value="%{contractorAuditFile.description}" size="50" /><br />
	</div>
	<div><s:if test="file != null && file.exists()">
		<button class="picsbutton negative" name="button" value="Delete"
			type="submit"
			onclick="return confirm('Are you sure you want to delete this file?');">DeleteFile</button>
	</s:if>
	<button class="picsbutton positive" name="button" value="Save"
		type="submit">Save</button>
	</div>
	</div>
</s:form>
