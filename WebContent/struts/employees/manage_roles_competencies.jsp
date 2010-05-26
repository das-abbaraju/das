<%@ taglib prefix="s" uri="/struts-tags"%>
<table>
	<tr>
		<td>
		<s:if test="jobCompetencies.size() > 0">
			<table class="report">
				<thead>
					<tr>
						<td colspan="2">Required HSE Competencies</td>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="jobCompetencies">
						<tr>
							<td>
								<s:if test="competency.description.length() > 0">
									<a href="#" onclick="return false;" rel="ManageJobRolesAjax.action?button=Description&competencyID=<s:property value="competency.id"/>" class="description">
										<span><s:property value="competency.category"/>: <s:property value="competency.label"/></span>
									</a>
								</s:if>
								<s:else>
									<span><s:property value="competency.category"/>: <s:property value="competency.label"/></span>
								</s:else>
							</td>
							<td><a href="#" class="compEditor" onclick="removeCompetency(<s:property value="competency.id"/>); return false;"><img alt="Delete" src="images/icon-16-remove.png" border="0"></a></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</s:if>
		<s:else>
			<div class="alert">To add required HSE competencies to the <s:property value="role.name" /> role,
				click on the plus sign next to the HSE competency you would like required from the options in the table on the right.</div>
		</s:else>
		</td>
		<td>&nbsp;</td>
		<td>
		<table class="report">
			<thead>
				<tr>
					<td>Potential HSE Competencies</td>
					<td><span title="Number of times this competency is used for <s:property value="role.name"/>">% Used</span></td>
					<td></td>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="otherCompetencies">
				<tr>
					<td>
						<s:if test="description != null && description.length() > 0">
							<a href="#" onclick="return false;" rel="ManageJobRolesAjax.action?button=Description&competencyID=<s:property value="id"/>" class="description">
								<span><s:property value="category"/>: <s:property value="label"/></span>
							</a>
						</s:if>
						<s:else>
							<span><s:property value="category"/>: <s:property value="label"/></span>
						</s:else>
					</td>
					<td class="right"><s:if test="jobCompentencyStats.percent != null"><s:property value="jobCompentencyStats.percent"/>%</s:if><s:else>&nbsp;</s:else></td>
					<td><a href="#" class="compEditor" onclick="addCompetency(<s:property value="id"/>); return false;"><img alt="Add" src="images/plus.png" border="0"></a></td>
				</tr>
				</s:iterator>
			</tbody>
		</table>
		</td>
	</tr>
</table>