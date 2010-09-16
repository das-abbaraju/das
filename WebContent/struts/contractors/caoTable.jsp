<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="statusOpBox" style="">
	<thead>
		<tr>
			<s:if test="conAudit.operators.size()>1">
				<th>Operator</th>
			</s:if>
			<th>Progress</th>
			<th>Status</th>
			<th>Date</th>
			<th>Button</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="conAudit.operators" status="rowStatus" id="currentCao">
			<s:if test="visible && isVisibleTo(permissions)">
				<tr id="cao_<s:property value="id"/>" class="caos">
					<s:if test="conAudit.operators.size()>1">
						<td><s:property value="operator.name" /></td>
					</s:if>								
					<td class="progress nobr">
						<div style="position: relative">
							<table class="progressTable"><tr><td class="progressBar" style="width: <s:property value="percentComplete" />%"></td><td></td></tr></table>
							<span class="progressPercent"><s:property value="percentComplete" />%</span>
						</div>
					</td>
					<td><s:property value="status"/></td>
					<td><s:property value="formatDate(statusChangedDate, 'MMMMM d, yyyy')" default="N/A" /></td>
					<td class="buttonAction">
						<s:iterator value="getValidButtons(status, #currentCao)" id="buttonActions">
							<span class="singleButton">
								<s:property value="#buttonActions.key" />
								<s:hidden cssClass="bCaoID" name="%{id}_%{#buttonActions.key}" value="%{id}"/>
								<s:hidden cssClass="bStepID" name="%{id}_%{#buttonActions.key}_stepID" value="%{#buttonActions.value}" />
								<s:hidden cssClass="bAction" value="%{#buttonActions.key}" name="%{id}_%{#buttonActions.key}_action" />
							</span> 
						</s:iterator>
					</td>
				</tr>
			</s:if>
		</s:iterator>
	</tbody>
</table>