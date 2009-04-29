<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="criteriaEditForm">
	<input type="hidden" value="<s:property value="operator.id"/>" name="id"/>
	<input type="hidden" value="<s:property value="question.id"/>" name="question.id"/>
	<div>
		<strong><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></strong>
		
		<s:property value="question.question" /><br/>
	</div>
	<div class="forms">
	<s:if test="question.questionType == 'Yes/No'">
		<label>Red:</label>
			<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="red.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="" headerValue="" name="red.value"></s:select>
		<br/>
		<label>Amber:</label>
			<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="amber.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="" headerValue="" name="amber.value"></s:select>
	</s:if>
	<s:if test="question.questionType == 'Yes/No/NA'">
		<label>Red:</label>
			<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="red.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No','NA':'NA'}" headerKey="" headerValue="" name="red.value"></s:select>
		<br/>
		<label>Amber:</label>
			<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="amber.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No','NA':'NA'}" headerKey="" headerValue="" name="amber.value"></s:select>
	</s:if>
	<s:if test="question.questionType == 'Check Box'">
		<label>Red:</label>
			<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="red.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="" headerValue="" name="red.value"></s:select>
		<br/>
		<label>Amber:</label>
			<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="amber.comparison"></s:select>
			<s:select list="#{'Yes':'Yes','No':'No'}" headerKey="" headerValue="" name="amber.value"></s:select>
	</s:if>
	<s:if test="question.questionType == 'License'">
		<label>Red:</label>
			<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="red.comparison"></s:select>
			<s:textfield name="red.value" />
		<br/>
		<label>Amber:</label>
			<s:select list="#{'!=':'!=','=':'='}" headerKey="" headerValue="" name="amber.comparison"></s:select>
			<s:textfield name="amber.value" />
	</s:if>
	<s:if test="question.questionType == 'Date'">
		<label>Red:</label>
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="red.comparison"></s:select>
			<s:textfield name="red.value" />
		<br/>
		<label>Amber:</label>
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="amber.comparison"></s:select>
			<s:textfield name="amber.value" />
	</s:if>
	<s:if test="question.questionType == 'Decimal Number'">
		<label>Red:</label>
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="red.comparison"></s:select>
			<s:textfield name="red.value" />
		<br/>
		<label>Amber:</label>
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="amber.comparison"></s:select>
			<s:textfield name="amber.value" />
	</s:if>
	<s:if test="question.questionType == 'Money'">
		<label>Red:</label>
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="red.comparison"></s:select>
			<s:textfield name="red.value" />
		<br/>
		<label>Amber:</label>
			<s:select list="#{'<':'<','=':'=','>':'>'}" headerKey="" headerValue="" name="amber.comparison"></s:select>
			<s:textfield name="amber.value" />
	</s:if>
	</div>
	
	<div class="buttons">
		<input type="button" class="picsbutton positive" onclick="saveCriteria(<s:property value="operator.id"/>,<s:property value="question.id"/>); return false;" value="Save"/>
		<input type="button" class="picsbutton negative" onclick="closeCriteriaEdit(); return false;" value="Close" />
	</div>
</form>