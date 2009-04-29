<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="criteriaEditForm">
	<input type="hidden" value="<s:property value="operator.id"/>" name="id"/>
	<input type="hidden" value="<s:property value="question.id"/>" name="question.id"/>
	<div>
		<strong><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></strong>
		
		<s:property value="question.question" /><br/>
	</div>
	<div class="forms">
		<s:set name="criteria" value="'red'" />
		<s:set name="criteria_handle" value="red"/>
		<s:set name="label" value="'Red'"/>
		<s:include value="op_flag_criteria_view_pair.jsp"/>
		<s:set name="criteria" value="'amber'" />
		<s:set name="label" value="'Amber'"/>
		<s:set name="criteria_handle" value="amber"/>
		<s:include value="op_flag_criteria_view_pair.jsp"/>
	</div>
	
	<div class="buttons">
		<input type="button" class="picsbutton positive" onclick="saveCriteria(<s:property value="operator.id"/>,<s:property value="question.id"/>); return false;" value="Save"/>
		<input type="button" class="picsbutton negative" onclick="closeCriteriaEdit(); return false;" value="Close" />
	</div>
</form>