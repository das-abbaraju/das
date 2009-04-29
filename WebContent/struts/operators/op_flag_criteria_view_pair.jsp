<%@ taglib prefix="s" uri="/struts-tags"%>
<label> <s:property value="#label"/>: </label>
<s:if test="question.questionType == 'Yes/No'">
		<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison"></s:select>
		<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="" headerValue="" name="%{criteria}.value"></s:select>
</s:if>
<s:if test="question.questionType == 'Yes/No/NA'">
		<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison"></s:select>
		<s:select list="#{'Yes':'Yes','No':'No','NA':'NA'}" headerKey="" headerValue="" name="%{criteria}.value"></s:select>
</s:if>
<s:if test="question.questionType == 'Check Box'">
		<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison"></s:select>
		<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="" headerValue="" name="%{criteria}.value"></s:select>
</s:if>
<s:if test="question.questionType == 'License'">
		<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison"></s:select>
		<s:textfield name="%{criteria}.value" />
</s:if>
<s:if test="question.questionType == 'Date'">
		<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="%{criteria}.comparison"></s:select>
		<s:textfield name="%{criteria}.value" />
</s:if>
<s:if test="question.questionType == 'Decimal Number'">
		<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="%{criteria}.comparison"></s:select>
		<s:textfield name="%{criteria}.value" />
</s:if>
<s:if test="question.questionType == 'Money'">
		<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="%{criteria}.comparison"></s:select>
		<s:textfield name="%{criteria}.value" />
</s:if>

<s:if test="#criteria_handle != null">
	<a title="Clear Criteria" style="cursor:pointer" onclick="clearRow('<s:property value="#criteria"/>')"><img src="images/notOkCheck.gif"/></a>
</s:if>

<br/>