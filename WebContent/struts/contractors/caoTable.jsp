<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:form id="caoTableForm">
	<table class="statusOpBox">
		<thead>
			<tr>
				<s:if test="systemEdit && !permissions.operatorCorporate">
					<th>Visible</th>
				</s:if>
				<th>Operator Scope</th>
				<th>Progress</th>
				<th>Status</th>
				<th>Date</th>
				<s:if test="conAudit.auditType.classType.policy">
					<th>Suggested</th>
				</s:if>
				<s:if test="!systemEdit">
					<th>
						<s:if test="actionStatus.size()>0">
							<s:select list="actionStatus.asMap()" headerKey="-1" headerValue="-- Select Action --"
								listKey="key" listValue="key.button+' All'" name="multiStatusChange" id="multiStatusChange" />
							<s:iterator value="actionStatus.keySet()" var="status">
								<s:hidden id="h_%{#status}" name="h_%{#status}" value="%{actionStatus.asMap().get(#status)}"/>
							</s:iterator>
						</s:if>
					</th>
				</s:if>
			</tr>
		</thead>
		<tbody>		
			<s:iterator value="getViewableOperators(permissions)" status="rowStatus" var="currentCao">
				<s:if test="#currentCao.visible || systemEdit">
					<tr id="cao_<s:property value="#currentCao.id"/>" class="caos">
						<s:if test="systemEdit && !permissions.operatorCorporate">
							<td class="center">
								<s:checkbox cssClass="vis" value="#currentCao.visible" name="caosSave[%{#rowStatus.index}].visible" />
							</td>
						</s:if>
						<td title="<s:iterator value="getViewableCaops(#currentCao)"><s:property value="name"/>
</s:iterator>">
						<s:if test="systemEdit">
							<s:hidden name="caosSave[%{#rowStatus.index}].id" value="%{#currentCao.id}" />
							<s:property value="operator.name"/>
						</s:if>
						<s:elseif test="getViewableCaops(#currentCao).size() == 1">
							<s:iterator value="getViewableCaops(#currentCao)">
								<s:property value="name" />
							</s:iterator>
						</s:elseif>
						<s:elseif test="operator.id > 10">
							<s:property value="operator.name"/>
						</s:elseif>
						<s:else>
							<s:property value="operator.name.substring(4)" />: <s:property value="getViewableCaops(#currentCao).size()"/> operators
						</s:else>
						</td>
						<td class="progress nobr">
							<s:if test="#currentCao.status.submittedResubmitted">
								<div style="position: relative">
									<table class="progressTable" title="Percent Verified"><tr><td class="progressBar" style="width: <s:property value="percentVerified" />%"></td><td style="width: <s:property value="100 - percentVerified" />%"></td></tr></table>
									<span class="progressPercent"><s:property value="percentVerified" />%</span>
								</div>
							</s:if>
							<s:elseif test="!#currentCao.status.name().equals('Complete') && !#currentCao.status.name().equals('Approved')">
								<div style="position: relative">
									<table class="progressTable" title="Percent Complete"><tr><td class="progressBar" style="width: <s:property value="percentComplete" />%"></td><td style="width: <s:property value="100 - percentComplete" />%"></td></tr></table>
									<span class="progressPercent"><s:property value="percentComplete" />%</span>
								</div>
							</s:elseif>
						</td>
						<td class="caoStatus">
							<s:if test="systemEdit">
								<s:select list="getValidStatuses(#currentCao.id)" name="caosSave[%{#rowStatus.index}].status" value="#currentCao.status" />
							</s:if>
							<s:else>
								<a onclick="loadStatus(<s:property value="#currentCao.id"/>)" class="preview <s:property value="status.color"/>"><s:property value="status"/></a>
							</s:else>
						</td>					
						<td class="caoDate">
							<s:property value="formatDate(statusChangedDate, 'dd MMM yyyy')" default="N/A" />
						</td>
						<s:if test="conAudit.auditType.classType.policy">
							<td style="color : <s:property value="#currentCao.flag.hex"/>"><s:property value="#currentCao.flag.insuranceStatus" default="N/A" /></td>
						</s:if>
						<s:if test="!systemEdit">
							<td class="buttonAction">
								<s:iterator value="getCurrentCaoStep(#currentCao.id)" id="step">
									<div class="singleButton button <s:property value="#step.newStatus.color"/>">
										<s:property value="#step.buttonName" />
										<s:hidden cssClass="bCaoID" name="%{id}_%{#step.id}" value="%{#currentCao.id}"/>
										<s:hidden cssClass="bStepID" name="%{id}_%{#buttonActions.key}_stepID" value="%{#step.id}" />
										<s:hidden cssClass="bStatus" value="%{#step.newStatus}" name="%{id}_%{#step.newStatus}_action" />
									</div> 
								</s:iterator>
							</td>
						</s:if>
					</tr>
				</s:if>
			</s:iterator>
		</tbody>
	</table>
</s:form>