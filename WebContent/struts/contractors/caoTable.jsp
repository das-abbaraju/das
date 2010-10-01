<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="statusOpBox" style="">
	<thead>
		<tr>
			<s:if test="conAudit.getViewableOperators(permissions).size()>1">
				<th>Operator</th>
			</s:if>
			<th>Progress</th>
			<th>Status</th>
			<th>Date</th>
			<s:if test="conAudit.auditType.classType.policy">
				<th>Flag</th>
			</s:if>
			<th><s:if test="">
				<s:select list="actionStatus" headerKey="-1" headerValue="-- Select Action --"
					listKey="value" listValue="key.button" name="multiStatusChange" />
				<s:iterator value="actionStatus">
					<s:hidden name="h_%{key.button}" value="%{key}"/>
				</s:iterator>
				</s:if>
			</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="conAudit.getViewableOperators(permissions)" status="rowStatus" id="currentCao">
				<tr id="cao_<s:property value="#currentCao.id"/>" class="caos">
					<s:if test="conAudit.getViewableOperators(permissions).size()>1">
						<td><s:property value="operator.name" /></td>
					</s:if>								
					<td class="progress nobr">
						<div style="position: relative">
							<table class="progressTable"><tr><td class="progressBar" style="width: <s:property value="percentComplete" />%"></td><td style="width: <s:property value="100 - percentComplete" />%"></td></tr></table>
							<span class="progressPercent"><s:property value="percentComplete" />%</span>
						</div>
					</td>
					<s:if test="hasStatusChanged(status)">
						<td class="caoStatus">
							<a class="cluetip help" rel="#cluetip<s:property value="#currentCao.id"/>" title="<s:property value="status"/>"></a>
							<a title="<s:property value="statusDescription"/>" style="cursor: pointer;" onclick="loadStatus(<s:property value="#currentCao.id"/>)" class="<s:property value="status.color"/>"><s:property value="status"/></a>
							<div id="cluetip<s:property value="#currentCao.id"/>">
								<span title="<s:property value="status"/>">
									<s:property value="statusDescription"/>
								</span>
							</div>
						</td>
					</s:if>
					<s:else>
						<td class="caoStatus">
							<a class="cluetip help" rel="#cluetip<s:property value="#currentCao.id"/>" title="<s:property value="status"/>"></a>
							<s:property value="status"/>
							<div id="cluetip<s:property value="#currentCao.id"/>">
								<span title="<s:property value="status"/>">
									<s:property value="statusDescription"/>
								</span>
							</div>
						</td>
					</s:else>					
					<td><s:property value="formatDate(statusChangedDate, 'MMMMM d, yyyy')" default="N/A" /></td>
					<s:if test="conAudit.auditType.classType.policy">
						<td><s:property value="#currentCao.flag.insuranceStatus" default="N/A"/></td>
					</s:if>
					<td class="buttonAction">
						<s:iterator value="getCurrentCaoStep(#currentCao.id)" id="step">
							<div class="singleButton button <s:property value="#step.newStatus.color"/>" style="cursor: pointer;">
								<s:property value="#step.buttonName" />
								<s:hidden cssClass="bCaoID" name="%{id}_%{#step.id}" value="%{#currentCao.id}"/>
								<s:hidden cssClass="bStepID" name="%{id}_%{#buttonActions.key}_stepID" value="%{#step.id}" />
								<s:hidden cssClass="bStatus" value="%{#step.newStatus}" name="%{id}_%{#step.newStatus}_action" />
							</div> 
						</s:iterator>
					</td>
				</tr>
		</s:iterator>
	</tbody>
</table>