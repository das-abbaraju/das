<%@ taglib prefix="s" uri="/struts-tags"%>
<div id="criteriaContent">
<form id="criteriaEditForm"><input type="hidden"
	value="<s:property value="operator.id"/>" name="id" /> <input
	type="hidden" value="<s:property value="question.id"/>"
	name="question.id" />

<div><strong><s:property value="question.expandedNumber" /></strong>

<s:property value="question.question" escape="false" /><br />
</div>

<div>
<table>
	<s:iterator value="#{'red':red, 'amber':amber}">
		<tr>
			<td><label><s:property
				value="key.substring(0,1).toUpperCase()+key.substring(1)" />:</label></td>
			<td><s:set name="criteria" value="key" /> <s:include
				value="op_flag_criteria_view_pair.jsp" /></td>
			<s:if test="question.id == 2034">
				<td><s:select list="multiYearScopeList"
					name="%{criteria}.multiYearScope"></s:select></td>
			</s:if>
			<td width="20"><span id="<s:property value="key"/>_clear"
				<s:if test="value == null">style="display:none"</s:if>>
			<a href="#" class="remove" title="Clear Criteria"
				onclick="clearRow('<s:property value="key"/>');return false;">Remove</a> </span></td>
		</tr>
	</s:iterator>
</table>
</div>

<div><input type="button" id="save_button"
	class="picsbutton positive" onclick="saveCriteria(); return false;"
	value="Save" /> <input type="button" id="close_button"
	class="picsbutton negative"
	onclick="closeCriteriaEdit(); return false;" value="Close" /></div>

<s:if test="question.questionType != 'AMBest'">
	<div class="test">Test: <input type="text" id="test" size="10">
	<input type="button" onclick="testCriteria();return false;" value="Test" />
	<span id="test_output"></span></div>
</s:if>
</form>
</div>