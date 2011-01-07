<%@ taglib prefix="s" uri="/struts-tags"%>
<table>
	<tr>
		<s:if test="jobCompetencies.size > 0">
			<td>
				<table class="report">
					<thead>
						<tr>
							<td colspan="3">Required HSE Competencies</td>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="jobCompetencies">
							<tr>
								<td>
									<span><s:property value="competency.label"/></span>
								</td>
								<td>
									<img src="images/help.gif" alt="<s:property value="competency.label" />" title="<s:property value="competency.category" />: <s:property value="competency.description" />" />
								</td>
								<td><a href="#" class="compEditor" onclick="removeCompetency(<s:property value="competency.id"/>); return false;"><img alt="Delete" src="images/icon-16-remove.png" border="0"></a></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</td>
			<td>&nbsp;</td>
		</s:if>
		<td>
			<s:if test="jobCompetencies.size == 0">
				<div class="alert">Use the table below to select competencies required for the <s:property value="role.name" /> role.</div>
			</s:if>
			<table class="report">
				<thead>
					<tr>
						<td colspan="2">Potential HSE Competencies</td>
						<td></td>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="otherCompetencies">
					<tr>
						<td>
							<span><s:property value="label"/></span>
						</td>
						<td>
							<img src="images/help.gif" alt="<s:property value="label" />" title="<s:property value="category" />: <s:property value="description" />" />
						</td>
						<td><a href="#" class="compEditor" onclick="addCompetency(<s:property value="id"/>); return false;"><img alt="Add" src="images/plus.png" border="0"></a></td>
					</tr>
					</s:iterator>
				</tbody>
			</table>
		</td>
	</tr>
</table>