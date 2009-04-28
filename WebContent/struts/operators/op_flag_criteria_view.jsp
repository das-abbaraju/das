<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="criteriaEditForm">
	<div>
		<strong><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></strong>
		
		<s:property value="question.question" /><br/>
	</div>
	
	<s:iterator value="#{'Red':red,'Amber':amber}">
		<label>
			<s:property value="key"/>:
		</label>
		<s:if test="question.questionType == 'Yes/No'">
			<s:select list="#{'!=':'!=','=':'='}" headerKey="0" headerValue="" name="value.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="0" headerValue="" name="value.value"></s:select>
		</s:if>
		<s:if test="question.questionType == 'Yes/No/NA'">
			<s:select list="#{'!=':'!=','=':'='}" headerKey="0" headerValue="" name="value.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No','NA':'NA'}" headerKey="0" headerValue="" name="value.value"></s:select>
		</s:if>
		<s:if test="question.questionType == 'Check Box'">
			<s:select list="#{'!=':'!=','=':'='}" headerKey="0" headerValue="" name="value.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="0" headerValue="" name="value.value"></s:select>
		</s:if>
		<s:if test="question.questionType == 'License'">
			<s:select list="#{'!=':'!=','=':'='}" headerKey="0" headerValue="" name="value.comparison"></s:select>
			<s:textfield name="value.value" />
		</s:if>
		<s:if test="question.questionType == 'Date'">
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="0" headerValue="" name="value.comparison"></s:select>
			<s:textfield name="value.value" />
		</s:if>
		<s:if test="question.questionType == 'Decimal Number'">
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="0" headerValue="" name="value.comparison"></s:select>
			<s:textfield name="value.value" />
		</s:if>
		<s:if test="question.questionType == 'Money'">
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="0" headerValue="" name="value.comparison"></s:select>
			<s:textfield name="value.value" />
		</s:if>
		
		<br/>
	</s:iterator>
	
	<div class="buttons">
		<input type="button" class="picsbutton positive" onclick="saveCriteria(<s:property value="operator.id"/>,<s:property value="question.id"/>)" value="Save"/>
		<input type="button" class="picsbutton negative" onclick="closeEditCriteria()" value="Close" />
	</div>
</form>