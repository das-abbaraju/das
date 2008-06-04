<%@ taglib prefix="s" uri="/struts-tags"%>
<tr class="group1">
	<td class="right"><s:property value="category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>&nbsp;&nbsp;</td>
	<td class="question"><s:property value="question"/>
		<s:if test="linkUrl1 > ''"><a href="http://<s:property value="linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="linkText1"/></a></s:if>
		<s:if test="linkUrl2 > ''"><a href="http://<s:property value="linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="linkText2"/></a></s:if>
		<s:if test="linkUrl3 > ''"><a href="http://<s:property value="linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="linkText3"/></a></s:if>
		<s:if test="linkUrl4 > ''"><a href="http://<s:property value="linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="linkText4"/></a></s:if>
		<s:if test="linkUrl5 > ''"><a href="http://<s:property value="linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="linkText5"/></a></s:if>
		<s:if test="linkUrl6 > ''"><a href="http://<s:property value="linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="linkText6"/></a></s:if>
	</td>
	<td class="answer">
		<s:if test="questionType == 'Text'">
			<s:textfield name="answer.answer" size="30" />
		</s:if>
		<s:if test="questionType == 'Date'">
			<nobr><s:textfield name="answer.answer" size="8" 
			onclick="cal1.select(document.forms('audit_form').answer,'answer.answer','M/d/yy','answer'); return false;" />
			<img src="images/icon_calendar.gif" width="18" height="15" />
			</nobr>
		</s:if>
		<s:if test="questionType == 'Check Box' || questionType == 'Industry' || questionType == 'Main Work'">
			<s:checkbox fieldValue="X" value="answer.answer.length() == 1" name="question_%{questionID}" onchange="" />
		</s:if>
		<s:if test="questionType == 'Yes/No'">
			<s:radio list="#{'Yes':'Yes','No':'No'}" value="answer.answer" name="question_%{questionID}" onchange=""></s:radio>
		</s:if>
		<s:if test="questionType == 'Yes/No/NA'">
			<s:radio list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer.answer" name="question_%{questionID}" onchange=""></s:radio>
		</s:if>
		<s:if test="questionType == 'Office'">
			<s:radio list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer.answer" name="question_%{questionID}" onchange=""></s:radio>
		</s:if>
		<s:if test="questionType == 'Manual'">
			<s:radio list="#{'Yes':'Yes','No':'No'}" value="answer.answer" name="question_%{questionID}" onchange=""></s:radio>
			<br>Comments: <s:textfield name="answer.comment" size="30" />
		</s:if>
		<s:if test="questionType == 'Office Location'">
			<s:radio list="#{'No':'No','Yes':'Yes','Yes with Office':'Yes with Office'}" value="answer.answer" name="question_%{questionID}" onchange=""></s:radio>
		</s:if>
		<s:if test="questionType == 'State'">
			<s:select list="stateList" value="answer.answer" name="question_%{questionID}"></s:select>
		</s:if>
		<s:if test="questionType == 'Country'">
			<s:select list="countryList" value="answer.answer" name="question_%{questionID}"></s:select>
		</s:if>
		<s:if test="questionType == 'Money'">
			<s:textfield name="answer.answer" size="19" onblur="validateNumber('answer.answer','Question 1')" />
		</s:if>
		<s:if test="questionType == 'Decimal Number'">
			<s:textfield name="answer.answer" size="19" onblur="validateNumber('answer.answer','Question 1')" />
		</s:if>
	</td>
</tr>
<s:if test="questionType == 'Text Area'">
	<tr class="group1"><td>&nbsp;</td><td colspan="2" class="right">
	<s:textarea cols="70" rows="4" value="%{answer.answer}" >
	</s:textarea>
	</td>
	</tr>
</s:if>
