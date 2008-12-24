<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="answer">
		<s:if test="question.questionType.startsWith('File')">
			<nobr>
				<s:if test="id > 0 && answer.length() > 0">
					<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.id=<s:property value="id"/>" target="_BLANK">View File</a>
				</s:if>
				<s:else>File Not Uploaded</s:else>
				<input id="show_button_<s:property value="question.id"/>" type="button" 
					value="<s:if test="id > 0 && answer.length() > 0">Edit</s:if><s:else>Add</s:else> File" 
					onclick="showFileUpload(<s:property value="question.id"/>, '<s:property value="parentAnswer.id"/>');"
					title="Opens in new window (please disable your popup blocker)" />
			</nobr>
		</s:if>
</s:iterator>
