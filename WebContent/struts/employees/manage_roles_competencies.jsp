<%@ taglib prefix="s" uri="/struts-tags"%>
<table>
	<tr>
		<td>
		<table class="report">
			<thead>
				<tr>
					<td colspan="2">Required Competencies</td>
				</tr>
			</thead>
			<tbody>
				<s:if test="jobCompetencies.size() > 0">
					<s:iterator value="jobCompetencies">
					<tr>
						<td><span title="<s:property value="competency.description"/>"><s:property value="competency.category"/>: <s:property value="competency.label"/></span></td>
						<td><a href="#" class="compEditor" onclick="removeCompetency(<s:property value="competency.id"/>); return false;"><img alt="Delete" src="images/icon-16-remove.png" border="0"></a></td>
					</tr>
					</s:iterator>
				</s:if>
				<s:else>
					<tr>
						<td colspan="2">To add required competencies to the <s:property value="role.name" /> role,
							click on the plus sign next to the competency you would like required from the options in the table on the right.</td>
					</tr>
				</s:else>
			</tbody>
		</table>
		</td>
		<td>&nbsp;</td>
		<td>
		<table class="report">
			<thead>
				<tr>
					<td>Not Required</td>
					<td><span title="Number of times this Competency is used for <s:property value="role.name"/>">% Used</span></td>
					<td></td>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="otherCompetencies" >
				<tr>
					<td><span title="<s:property value="description"/>"><s:property value="category"/>: <s:property value="label"/></span></td>
					<td class="right"><s:if test="jobCompentencyStats.percent != null"><s:property value="jobCompentencyStats.percent"/>%</s:if></td>
					<td><a href="#" class="compEditor" onclick="addCompetency(<s:property value="id"/>); return false;"><img alt="Add" src="images/plus.png" border="0"></a></td>
				</tr>
				</s:iterator>
			</tbody>
		</table>
		</td>
	</tr>
</table>
