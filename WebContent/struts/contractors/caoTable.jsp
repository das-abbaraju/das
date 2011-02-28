<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:form id="caoTableForm">
	<table class="statusOpBox">
		<thead>
			<tr>
				<s:if test="systemEdit && !permissions.operatorCorporate">
					<th><s:text name="Audit.header.Visible" />Visible</th>
				</s:if>
				<th><s:text name="Audit.header.OperatorScope" /></th>
				<th><s:text name="Audit.header.Progress" /></th>
				<th><s:text name="global.Status" /></th>
				<th><s:text name="global.Date" /></th>
				<s:if test="conAudit.auditType.classType.policy">
					<th><s:text name="Audit.header.Suggested" /></th>
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
							<s:property value="operator.name" />
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
							<s:elseif test="!#currentCao.status.name().equals('Complete') && !#currentCao.status.name().equals('Approved') && !#currentCao.status.name().equals('NotApplicable')">
								<div style="position: relative">
									<table class="progressTable" title="Percent Complete"><tr><td class="progressBar" style="width: <s:property value="percentComplete" />%"></td><td style="width: <s:property value="100 - percentComplete" />%"></td></tr></table>
									<span class="progressPercent"><s:property value="percentComplete" />%</span>
								</div>
							</s:elseif>
						</td>
						<td class="caoStatus<s:if test="!systemEdit"> hoverable</s:if><s:else> systemEdit</s:else>">
							<s:if test="!systemEdit">
								<span class="caoDisplay">
									<a onclick="loadStatus(<s:property value="#currentCao.id"/>)" class="showPointer preview <s:property value="status.color"/>"><s:text name="%{status.getI18nKey()}" /></a>
								</span>
							</s:if>
							<s:if test="isCanEditCao(#currentCao)">
								<span class="caoEdit left">
									<s:hidden cssClass="caoID" name="#currentCao.id"/>
									<s:select cssClass="status" list="getValidStatuses(#currentCao.id)" name="caosSave[%{#rowStatus.index}].status" value="#currentCao.status" />
								</span>
								<s:if test="!systemEdit">
									<span class="right"><a href="#" class="edit"></a></span>
								</s:if>
							</s:if>
						</td>					
						<td class="caoDate">
							<s:if test="statusChangedDate == null">
								N/A
							</s:if>
							<s:else>
								<s:date name="statusChangedDate" format="dd MMM yyyy" />
							</s:else>
						</td>
						<s:if test="conAudit.auditType.classType.policy">
							<td style="color : <s:property value="#currentCao.flag.hex"/>"><s:property value="#currentCao.flag.insuranceStatus" default="N/A" /></td>
						</s:if>
						<s:if test="!systemEdit">
							<td class="buttonAction">
								<s:iterator value="getCurrentCaoStep(#currentCao.id)" id="step">
									<s:if test="!(conAudit.auditType.classType.policy && #currentCao.operator.autoApproveInsurance && permissions.admin && #step.newStatus.approved)">
										<div class="singleButton button <s:property value="#step.newStatus.color"/>">
											<s:text name="%{#step.newStatus.getI18nKey('button')}" />
											<s:hidden cssClass="bCaoID" name="%{id}_%{#step.id}" value="%{#currentCao.id}"/>
											<s:hidden cssClass="bStepID" name="%{id}_%{#buttonActions.key}_stepID" value="%{#step.id}" />
											<s:hidden cssClass="bStatus" value="%{#step.newStatus}" name="%{id}_%{#step.newStatus}_action" />
										</div>
									</s:if>
								</s:iterator>
							</td>
						</s:if>
					</tr>
				</s:if>
			</s:iterator>
		</tbody>
	</table>
</s:form>