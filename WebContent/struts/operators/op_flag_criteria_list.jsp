<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
<tr><td>show the list here</td></tr>
<s:iterator value="questionList">
<tr><td><s:property value="question.question"/></td></tr>
</s:iterator>
</table>
