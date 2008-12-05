<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="question">
<s:if test="questionType == 'File'">
	<nobr>
		<s:if test="answer.answer.length() > 0">
			<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&question.id=<s:property value="id"/>" target="_BLANK">View File</a>
		</s:if>
		<s:else>File Not Uploaded</s:else>
		<input id="show_button_<s:property value="id"/>" type="button" 
			value="<s:if test="answer.answer.length() > 0">Edit</s:if><s:else>Add</s:else> File" 
			onclick="showFileUpload(<s:property value="id"/>);"
			title="Opens in new window (please disable your popup blocker)" />
	</nobr>
</s:if>
</s:iterator>
