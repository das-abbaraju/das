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
	<tr 
		<s:if test="operator == operator.inheritFlagCriteria">
			onclick="showOshaCriteria('1');" class="clickable" title="Click to open"
		</s:if>
	>
		<td><s:property value="operator.oshaType"/></td>
		<td class="right">LWCR</td>
		<td>
			Lost Workdays Case Rate 
		</td>
		<td><nobr><s:if test="oshaRedFlagCriteria != null && !oshaRedFlagCriteria.lwcr.hurdleFlag.none">
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaRedFlagCriteria.lwcr.time)"/>
			> <s:property value="oshaRedFlagCriteria.lwcr.hurdle"/>
			</s:if>
			<s:else>N/A</s:else>
		</nobr></td>
		<td><nobr><s:if test="oshaAmberFlagCriteria != null && !oshaAmberFlagCriteria.lwcr.hurdleFlag.none">
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaAmberFlagCriteria.lwcr.time)"/>
			> <s:property value="oshaAmberFlagCriteria.lwcr.hurdle"/>
			</s:if>
			<s:else>N/A</s:else>
		</nobr></td>
	</tr>
	<tr 
		<s:if test="operator == operator.inheritFlagCriteria">
			onclick="showOshaCriteria('2');" class="clickable" title="Click to open"
		</s:if>
	>
		<td><s:property value="operator.oshaType"/></td>
		<td class="right">TRIR</td>
		<td>
			Total Recordable Incident Rate 
		</td>
		<td><nobr><s:if test="oshaRedFlagCriteria != null && !oshaRedFlagCriteria.trir.hurdleFlag.none">
			<s:if test="oshaRedFlagCriteria.trir.hurdleFlag.naics"><s:property value="oshaRedFlagCriteria.trir.hurdleFlag"/></s:if>
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaRedFlagCriteria.trir.time)"/>
			> <s:if test="oshaRedFlagCriteria.trir.hurdleFlag.naics">
				<s:property value="format(oshaRedFlagCriteria.trir.hurdle, '#')"/>
			  </s:if>
			  <s:else>
			  	<s:property value="oshaRedFlagCriteria.trir.hurdle"/>
			  </s:else>
			</s:if>
			<s:else>N/A</s:else>
		</nobr></td>
		<td><nobr><s:if test="oshaAmberFlagCriteria != null && !oshaAmberFlagCriteria.trir.hurdleFlag.none">
			<s:if test="oshaAmberFlagCriteria.trir.hurdleFlag.naics"><s:property value="oshaAmberFlagCriteria.trir.hurdleFlag"/></s:if>
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaAmberFlagCriteria.trir.time)"/>
			>  <s:if test="oshaAmberFlagCriteria.trir.hurdleFlag.naics">
				<s:property value="format(oshaAmberFlagCriteria.trir.hurdle, '#')"/>
			  </s:if>
			  <s:else>
			  	<s:property value="oshaAmberFlagCriteria.trir.hurdle"/>
			  </s:else>
			</s:if>
			<s:else>N/A</s:else>
		</nobr></td>
	</tr>
	<tr 
		<s:if test="operator == operator.inheritFlagCriteria">
			onclick="showOshaCriteria('3');" class="clickable" title="Click to open"
			</s:if>
	>
		<td><s:property value="operator.oshaType"/></td>
		<td class="right">Fatalities</td>
		<td>
			Fatalities
		</td>
		<td><nobr><s:if test="oshaRedFlagCriteria != null && !oshaRedFlagCriteria.fatalities.hurdleFlag.none">
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaRedFlagCriteria.fatalities.time)"/>
			> <s:property value="oshaRedFlagCriteria.fatalities.hurdle"/>
			</s:if>
			<s:else>N/A</s:else>
		</nobr></td>
		<td><nobr><s:if test="oshaAmberFlagCriteria != null && !oshaAmberFlagCriteria.fatalities.hurdleFlag.none">
			<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaAmberFlagCriteria.fatalities.time)"/>
			> <s:property value="oshaAmberFlagCriteria.fatalities.hurdle"/>
			</s:if>
			<s:else>N/A</s:else>
		</nobr></td>
	</tr>
	<s:if test="operator.oshaType.toString() == 'COHS'">
		<tr 
			<s:if test="operator == operator.inheritFlagCriteria">
				onclick="showOshaCriteria('4');" class="clickable" title="Click to open"
				</s:if>
		>
			<td><s:property value="operator.oshaType"/></td>
			<td class="right">Cad7</td>
			<td>
				Cad7
			</td>
			<td><nobr><s:if test="oshaRedFlagCriteria != null && !oshaRedFlagCriteria.cad7.hurdleFlag.none">
				<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaRedFlagCriteria.cad7.time)"/>
				> <s:property value="oshaRedFlagCriteria.cad7.hurdle"/>
				</s:if>
				<s:else>N/A</s:else>
			</nobr></td>
			<td><nobr><s:if test="oshaAmberFlagCriteria != null && !oshaAmberFlagCriteria.cad7.hurdleFlag.none">
				<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaAmberFlagCriteria.cad7.time)"/>
				> <s:property value="oshaAmberFlagCriteria.cad7.hurdle"/>
				</s:if>
				<s:else>N/A</s:else>
			</nobr></td>
		</tr>
		<tr 
			<s:if test="operator == operator.inheritFlagCriteria">
				onclick="showOshaCriteria('5');" class="clickable" title="Click to open"
				</s:if>
		>
			<td><s:property value="operator.oshaType"/></td>
			<td class="right">Neer</td>
			<td>
				Neer
			</td>
			<td><nobr><s:if test="oshaRedFlagCriteria != null && !oshaRedFlagCriteria.neer.hurdleFlag.none">
				<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaRedFlagCriteria.neer.time)"/>
				> <s:property value="oshaRedFlagCriteria.neer.hurdle"/>
				</s:if>
				<s:else>N/A</s:else>
			</nobr></td>
			<td><nobr><s:if test="oshaAmberFlagCriteria != null && !oshaAmberFlagCriteria.neer.hurdleFlag.none">
				<s:property value="@com.picsauditing.actions.operators.OperatorFlagCriteria@getTime(oshaAmberFlagCriteria.neer.time)"/>
				> <s:property value="oshaAmberFlagCriteria.neer.hurdle"/>
				</s:if>
				<s:else>N/A</s:else>
			</nobr></td>
		</tr>
	</s:if>
	</s:if>
	<s:if test="questionList.size() == 0">
		<tr><td colspan="5">No flag criteria has been defined for this type</td></tr>
	</s:if>
	
	<s:iterator value="questionList">
		<tr 
			<s:if test="(operator == operator.inheritFlagCriteria && !question.subCategory.category.auditType.classType.policy) 
						|| (operator == operator.inheritInsuranceCriteria && question.subCategory.category.auditType.classType.policy)">
				onclick="showCriteria('<s:property value="question.id" />', '<s:property value="question.subCategory.category.auditType.auditName"/>');" 
				class="clickable" title="Click to open"
			</s:if>	
		>
			<td><s:property value="question.subCategory.category.auditType.auditName" /></td>
			<td class="right">
				<nobr><s:property value="question.expandedNumber"/></nobr>
			</td>
			<td>
				<s:if test="(operator == operator.inheritFlagCriteria && !question.subCategory.category.auditType.classType.policy) 
						|| (operator == operator.inheritInsuranceCriteria && question.subCategory.category.auditType.classType.policy)">
						<s:property value="question.question" />
				</s:if>
				<s:else><s:property value="question.question" escape="false" /></s:else>
			</td>
			<td><nobr><s:if test="red != null"><s:property value="red" escape="false" /></s:if>
			<s:else>N/A</s:else>
			</nobr></td>
			<td><nobr><s:if test="amber != null"><s:property value="amber" escape="false" /></s:if>
			<s:else>N/A</s:else>
			</nobr></td>
		</tr>
	</s:iterator>
</table>

<a href="#" onclick="getAddQuestions('<s:property value="classType"/>');return false;">[+] Add New Criteria</a>
<span id="<s:property value="classType"/>_thinking"></span>
<div id="<s:property value="classType"/>_questions" style="display:none"></div>
