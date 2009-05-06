<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="criteriaEditForm">
	<input type="hidden" value="<s:property value="operator.id"/>" name="id"/>
	<input type="hidden" value="<s:property value="question.id"/>" name="question.id"/>

	<div>
		<strong><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></strong>
		
		<s:property value="question.question" /><br/>
	</div>
	
	<table>
		<s:iterator value="#{'red':red, 'amber':amber}">
			<tr>
			<td>
				<label><s:property value="key.substring(0,1).toUpperCase()+key.substring(1)"/>:</label>
			</td>
			<td>
				<s:set name="criteria" value="key.toLowerCase()" />
				<s:set name="criteria_handle" value="value"/>
				<s:include value="op_flag_criteria_view_pair.jsp"/>
			</td>
			<td width="20">
				<span id="<s:property value="key"/>_clear"
						<s:if test="value == null">style="display:none"</s:if> 
						>
						<a  style="cursor:pointer" title="Clear Criteria" onclick="clearRow('<s:property value="key"/>');">
					<img src="images/notOkCheck.gif"/></a></span>
			</td>
			<td>
				Test: <input type="text" id="<s:property value="key"/>_test" size="10"> <input type="button" onclick="testCriteria('<s:property value="key"/>');return false;" value="Test"/>
			</td>
			<td style="width:20px; text-align:center">
				<span id="<s:property value="key"/>_test_output"></span>
			</td>
		</tr>
		</s:iterator>
	</table>
	
	<div class="buttons" style="margin-top:20px">
		<input type="button" class="picsbutton positive" onclick="saveCriteria(<s:property value="question.id"/>); return false;" value="Save"/>
		<input type="button" class="picsbutton negative" onclick="closeCriteriaEdit(); return false;" value="Close" />
	</div>
</form>