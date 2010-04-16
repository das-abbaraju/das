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
				<s:iterator value="role.competencies">
				<tr>
					<td><s:property value="competency.category"/>: <s:property value="competency.label"/></td>
					<td><a href="#" onclick="removeCompetency(<s:property value="competency.id"/>); return false;"><img alt="Delete" src="images/icon-16-remove.png" border="0"></a></td>
				</tr>
				</s:iterator>
			</tbody>
		</table>
		</td>
		<td>
		<table class="report">
			<thead>
				<tr>
					<td colspan="2">Not Required</td>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="otherCompetencies">
				<tr>
					<td><s:property value="category"/>: <s:property value="label"/></td>
					<td><a href="#" onclick="addCompetency(<s:property value="id"/>); return false;"><img alt="Add" src="images/plus.png" border="0"></a></td>
				</tr>
				</s:iterator>
			</tbody>
		</table>
		</td>
	</tr>
</table>
