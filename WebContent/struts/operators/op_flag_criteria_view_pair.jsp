<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="question.questionType == 'Yes/No'">
	<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison" onchange="$('%{criteria}_clear').show()"></s:select>
	<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="" headerValue="" name="%{criteria}.value" onchange="$('%{criteria}_clear').show()"></s:select>
</s:if>
<s:if test="question.questionType == 'Yes/No/NA'">
	<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison" onchange="$('%{criteria}_clear').show()"></s:select>
	<s:select list="#{'Yes':'Yes','No':'No','NA':'NA'}" headerKey="" headerValue="" name="%{criteria}.value" onchange="$('%{criteria}_clear').show()"></s:select>
</s:if>
<s:if test="question.questionType == 'Check Box'">
	<s:hidden name="%{criteria}.comparison" value="="/>
	<s:select list="#{'X':'Checked','-':'Not Checked'}" headerKey="" headerValue="" name="%{criteria}.value" onchange="$('%{criteria}_clear').show()"></s:select>
</s:if>
<s:if test="question.questionType == 'License'">
	<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison" onchange="$('%{criteria}_clear').show()"></s:select>
	<s:textfield name="%{criteria}.value" size="10" onchange="$('%{criteria}_clear').show()"/>
</s:if>
<s:if test="question.questionType == 'Text'">
	<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="%{criteria}.comparison" onchange="$('%{criteria}_clear').show()"></s:select>
	<s:textfield name="%{criteria}.value" size="10" onchange="$('%{criteria}_clear').show()"/>
</s:if>
<s:if test="question.questionType == 'Date'">
	<s:select list="#{'<':'Before','>':'After'}" headerKey="" headerValue="" name="%{criteria}.comparison" onchange="$('%{criteria}_clear').show()"></s:select>
	<s:textfield name="%{criteria}.value" size="10" onchange="$('%{criteria}_clear').show()"/>
	<a id="<s:property value="#criteria"/>_anchor_date" name="anchor_date"
		onclick="cal2.select($('<s:property value="#criteria"/>_value'),'<s:property value="#criteria"/>_anchor_date','M/d/yy');"><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
	<div id="caldiv2" style="top: 20px;position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>  
</s:if>
<s:if test="question.questionType == 'Decimal Number'">
	<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="%{criteria}.comparison" onchange="$('%{criteria}_clear').show()"></s:select>
	<s:textfield name="%{criteria}.value" size="10" onchange="$('%{criteria}_clear').show()"/>
</s:if>
<s:if test="question.questionType == 'Money'">
	<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="%{criteria}.comparison" onchange="$('%{criteria}_clear').show()"></s:select>
	<s:textfield name="%{criteria}.value" size="10" onchange="$('%{criteria}_clear').show()"/>
</s:if>
<s:if test="question.questionType == 'AMBest'">
	<nobr>
	<s:select list="aMBestRatingsList" headerKey="0" headerValue="-Ratings-" name="%{criteria}.amRatings" value="%{value.aMBestRatings}" onchange="$('%{criteria}_clear').show()"></s:select>
	<s:select list="aMBestClassList" headerKey="0" headerValue="-Class-" name="%{criteria}.amClass" value="%{value.aMBestClass}" onchange="$('%{criteria}_clear').show()"></s:select></nobr>
</s:if>