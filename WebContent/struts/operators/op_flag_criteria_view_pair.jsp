<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="question.questionType == 'Yes/No'">
	<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison" cssClass="%{criteria}"></s:select>
	<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="" headerValue="" name="%{criteria}.value" cssClass="%{criteria}"></s:select>
</s:if>
<s:if test="question.questionType == 'Yes/No/NA'">
	<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison" cssClass="%{criteria}"></s:select>
	<s:select list="#{'Yes':'Yes','No':'No','NA':'NA'}" headerKey="" headerValue="" name="%{criteria}.value" cssClass="%{criteria}"></s:select>
</s:if>
<s:if test="question.questionType == 'Check Box'">
	<s:hidden name="%{criteria}.comparison" value="="/>
	<s:select list="#{'X':'Checked','-':'Not Checked'}" headerKey="" headerValue="" name="%{criteria}.value" cssClass="%{criteria}"></s:select>
</s:if>
<s:if test="question.questionType == 'License'">
	<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison" cssClass="%{criteria}"></s:select>
	<s:textfield name="%{criteria}.value" size="10" cssClass="%{criteria}"/>
</s:if>
<s:if test="question.questionType == 'Text'">
	<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison" cssClass="%{criteria}"></s:select>
	<s:textfield name="%{criteria}.value" size="10" cssClass="%{criteria}"/>
</s:if>
<s:if test="question.questionType == 'Date'">
	<s:select list="#{'<':'Before','>':'After'}" headerKey="" headerValue="" name="%{criteria}.comparison" cssClass="%{criteria}"></s:select>
	<s:textfield name="%{criteria}.value" size="10" cssClass="%{criteria} datepicker"/>
</s:if>
<s:if test="question.questionType == 'Decimal Number'">
	<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="%{criteria}.comparison" cssClass="%{criteria}"></s:select>
	<s:textfield name="%{criteria}.value" size="10" cssClass="%{criteria}"/>
</s:if>
<s:if test="question.questionType == 'Money'">
	<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="%{criteria}.comparison" cssClass="%{criteria}"></s:select>
	<s:textfield name="%{criteria}.value" size="10" cssClass="%{criteria}"/>
</s:if>
<s:if test="question.questionType == 'AMBest'">
	<nobr>
	<s:select list="aMBestRatingsList" headerKey="0" headerValue="-Ratings-" name="%{criteria}.amRatings" value="%{value.aMBestRatings}" cssClass="%{criteria}"></s:select>
	<s:select list="aMBestClassList" headerKey="0" headerValue="-Class-" name="%{criteria}.amClass" value="%{value.aMBestClass}" cssClass="%{criteria}"></s:select>
	</nobr>
</s:if>