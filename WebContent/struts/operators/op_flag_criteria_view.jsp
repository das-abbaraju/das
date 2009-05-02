<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="criteriaEditForm">
	<input type="hidden" value="<s:property value="operator.id"/>" name="id"/>
	<input type="hidden" value="<s:property value="question.id"/>" name="question.id"/>
	<div>
		<strong><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></strong>
		
		<s:property value="question.question" /><br/>
	</div>
	<table>
		<tr>
			<td>
				<s:set name="criteria" value="'red'" />
				<s:set name="criteria_handle" value="red"/>
				<label>Red:</label>
			</td>
			<td>
				<s:include value="op_flag_criteria_view_pair.jsp"/>
			</td>
			<td>
				Test: <input type="text" id="red_test" size="10"> <input type="button" onclick="testCriteria('red_comparison','red_value','red_test');return false;" value="Test"/>
			</td>
		</tr>
		<tr>
			<td>
				<s:set name="criteria" value="'amber'" />
				<s:set name="criteria_handle" value="amber"/>
				<label>Amber:</label>
			</td>
			<td>
				<s:include value="op_flag_criteria_view_pair.jsp"/>
			</td>
			<td>
				Test: <input type="text" id="amber_test" size="10"> <input type="button" onclick="testCriteria('amber_comparison','amber_value','amber_test');return false;" value="Test"/>
			</td>
		</tr>
	</table>
	
	<div class="buttons">
		<input type="button" class="picsbutton positive" onclick="saveCriteria(<s:property value="operator.id"/>,<s:property value="question.id"/>); return false;" value="Save"/>
		<input type="button" class="picsbutton negative" onclick="closeCriteriaEdit(); return false;" value="Close" />
	</div>
</form>