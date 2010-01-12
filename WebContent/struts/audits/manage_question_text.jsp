<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp"/>
<s:form id="textForm">
<fieldset class="form" style="border: none">
<s:hidden name="id"/>
<s:hidden name="button" value="saveText"/>
<s:hidden name="questionText.id"/>
<ol>
<li><label>Locale:</label>
	<s:select list="localeList" listValue="displayName" name="questionText.locale"/>
</li>
<li><label>Question:</label>
	<s:textarea name="questionText.question" rows="5" cols="65" />
</li>
<li><label>Requirement:</label>
	<s:textarea name="questionText.requirement" rows="5" cols="65" />
</li>
</ol>
</fieldset>
</s:form>