<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
			<th>Type</th>
			<th class="center">#</th>
			<th>Question</th>
			<th title="Red Flag Criteria"><s:property value="@com.picsauditing.jpa.entities.FlagColor@Red.bigIcon" escape="false"/></th>
			<th title="Yellow Flag Criteria"><s:property value="@com.picsauditing.jpa.entities.FlagColor@Amber.bigIcon" escape="false"/></th>
		</tr>
	</thead>
	
	<s:if test="classType.toString() == 'Audit'">
	<tr onclick="showOshaCriteria('1');" class="clickable" title="Click to open">
		<td>OSHA</td>
		<td class="right"></td>
		<td>
			Lost Workdays Case Rate (LWCR):
		</td>
		<td><nobr><s:if test="oshaRedFlagCriteria != null && oshaRedFlagCriteria.lwcr.flag.toString() == 'Yes'">
			<s:property value="oshaRedFlagCriteria.lwcr.hurdle"/>
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaRedFlagCriteria.lwcr.time)"/>
			</s:if>
		</nobr></td>
		<td><nobr><s:if test="oshaAmberFlagCriteria != null && oshaAmberFlagCriteria.lwcr.flag.toString() == 'Yes'">
			<s:property value="oshaAmberFlagCriteria.lwcr.hurdle"/>
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaAmberFlagCriteria.lwcr.time)"/>
			</s:if>
		</nobr></td>
	</tr>
	<tr onclick="showOshaCriteria('2');" class="clickable" title="Click to open">
		<td>OSHA</td>
		<td class="right"></td>
		<td>
			Total Recordable Incident Rate (TRIR):
		</td>
		<td><nobr><s:if test="oshaRedFlagCriteria != null && oshaRedFlagCriteria.trir.flag.toString() == 'Yes'">
			<s:property value="oshaRedFlagCriteria.trir.hurdle"/>
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaRedFlagCriteria.trir.time)"/>
			</s:if>
		</nobr></td>
		<td><nobr><s:if test="oshaAmberFlagCriteria != null && oshaAmberFlagCriteria.trir.flag.toString() == 'Yes'">
			<s:property value="oshaAmberFlagCriteria.trir.hurdle"/>
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaAmberFlagCriteria.trir.time)"/>
			</s:if>
		</nobr></td>
	</tr>
	<tr onclick="showOshaCriteria('3');" class="clickable" title="Click to open">
		<td>OSHA</td>
		<td class="right"></td>
		<td>
			Fatalities:
		</td>
		<td><nobr><s:if test="oshaRedFlagCriteria != null && oshaRedFlagCriteria.fatalities.flag.toString() == 'Yes'">
			<s:property value="oshaRedFlagCriteria.fatalities.hurdle"/>
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaRedFlagCriteria.fatalities.time)"/>
			</s:if>
		</nobr></td>
		<td><nobr><s:if test="oshaAmberFlagCriteria != null && oshaAmberFlagCriteria.fatalities.flag.toString() == 'Yes'">
			<s:property value="oshaAmberFlagCriteria.fatalities.hurdle"/>
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaAmberFlagCriteria.fatalities.time)"/>
			</s:if>
		</nobr></td>
	</tr>
	</s:if>
	<s:if test="questionList.size() == 0">
		<tr><td colspan="5">No flag criteria has been defined for this type</td></tr>
	</s:if>
	
	<s:iterator value="questionList">
		<tr onclick="showCriteria('<s:property value="question.id" />', '<s:property value="question.subCategory.category.auditType.auditName"/>', <s:property value="question.questionType=='Date'"/>);" 
		class="clickable" title="Click to open">
			<td><s:property value="question.subCategory.category.auditType.auditName" /></td>
			<td class="right">
				<nobr><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></nobr>
			</td>
			<td>
				<s:if test="(operator == operator.inheritFlagCriteria && !question.subCategory.category.auditType.classType.policy) 
						|| (operator == operator.inheritInsuranceCriteria && question.subCategory.category.auditType.classType.policy)">
						<s:property value="question.question" />
				</s:if>
				<s:else><s:property value="question.question" escape="false" /></s:else>
			</td>
			<td><nobr><s:property value="red" escape="false" /></nobr></td>
			<td><nobr><s:property value="amber" escape="false" /></nobr></td>
		</tr>
	</s:iterator>
</table>
