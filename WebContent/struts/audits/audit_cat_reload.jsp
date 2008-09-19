<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="question">
<s:if test="questionType == 'File'">
	<nobr>
		<s:if test="answer.answer.length() > 0">
			<a href="#" onClick="openQuestion('<s:property value="questionID"/>', '<s:property value="answer.answer"/>'); return false;">View File</a>
		</s:if>
		<s:else>File Not Uploaded</s:else>
		<input id="show_button_<s:property value="questionID"/>" type="button" 
			value="<s:if test="answer.answer.length() > 0">Edit</s:if><s:else>Add</s:else> File" 
			onclick="showFileUpload(<s:property value="questionID"/>);"
			title="Opens in new window (please disable your popup blocker)" />
	</nobr>
</s:if>
</s:iterator>
