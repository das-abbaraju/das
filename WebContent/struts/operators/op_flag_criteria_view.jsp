<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="criteriaEditForm">
	<input type="hidden" value="<s:property value="operator.id"/>" name="id"/>
	<input type="hidden" value="<s:property value="question.id"/>" name="question.id"/>

	<div>
		<strong><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></strong>
		
		<s:property value="question.question" escape="false"/><br/>
	</div>
	
	<table>
		<s:iterator value="#{'red':red, 'amber':amber}">
			<tr>
				<td>
					<label><s:property value="key.substring(0,1).toUpperCase()+key.substring(1)"/>:</label>
				</td>
				<td>
					<s:set name="criteria" value="key" />
					<s:set name="criteria_handle" value="value"/>
					<s:include value="op_flag_criteria_view_pair.jsp"/>
				</td>
				<td width="20">
					<span id="<s:property value="key"/>_clear"
						<s:if test="value == null">style="display:none"</s:if> 
						>
						<a class="remove" title="Clear Criteria" onclick="clearRow('<s:property value="key"/>');">Remove</a>
					</span>
				</td>
			</tr>
		</s:iterator>
	</table>
	
	<div class="buttons" style="margin-top:20px">
		<input type="button" id="save_button" class="picsbutton positive" onclick="saveCriteria(<s:property value="question.id"/>); return false;" value="Save"/>
		<input type="button" id="close_button" class="picsbutton negative" onclick="closeCriteriaEdit(); return false;" value="Close" />
	</div>
	<br/>
	
	<div style="margin-top:20px">
		Test: 
		<input type="text" id="test" size="10"> <input type="button" onclick="testCriteria();return false;" value="Test"/>
		<span id="test_output"></span>
	</div>
</form>