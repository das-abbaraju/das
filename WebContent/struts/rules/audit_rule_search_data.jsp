<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:if test="report.allRows == 0">
	<div class="alert">
		No rows found matching the given criteria. Please try again.
	</div>
</s:if>
<s:else>
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	
	<table class="report">
		<thead>
			<tr>
				<td>
					View
				</td>
				<td>
					Include
				</td>
				<td>
					Audit Type
				</td>
				
				<s:if test="categoryRule">
					<td>
						Category
					</td>
				</s:if>
				
				<td>
					Operator
				</td>
				<td>
					Tag
				</td>
				<td>
					Safety Risk
				</td>
				<td>
					Product Risk
				</td>
				<td>
					Type
				</td>
				<td>
					Trade
				</td>
                <td colspan="2">
                    Dependency
                </td>

                <td style="max-width: 200px">
					Question
				</td>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr class="<s:property value="get('include') ? 'on' : 'off'"/>">
					<td class="center">
						<a href="<s:property value="actionUrl"/><s:property value="get('id')"/><s:if test="filter.checkDate!=null">&date=<s:property value="filter.checkDate"/></s:if>" class="preview"></a>
					</td>
					<td class="center">
						<s:property value="get('include') ? 'Include' : 'Exclude'"/>
					</td>
					<td>
						<s:if test="get('auditTypeID') > 0">
							<a href="ManageAuditType.action?id=<s:property value="get('auditTypeID')"/>">
								<s:text name="AuditType.%{get('auditTypeID')}.name" />
							</a>
						</s:if>
						<s:else>
							*
						</s:else>
					</td>
					
					<s:if test="categoryRule">
						<td>
							<s:if test="get('catID') > 0">
								<a href="ManageCategory.action?id=<s:property value="get('catID')"/>"><s:property value="get('category')"/></a>
							</s:if>
							<s:else>
								<b>- <s:property value="get('rootCategory') ? 'Top' : 'Sub'"/> Category -</b>
							</s:else>
						</td>
					</s:if>
					
					<td>
						<s:if test="get('opID') > 0">
							<a href="OperatorConfiguration.action?id=<s:property value="get('opID')"/>" class="account<s:property value="get('operatorStatus')"/>"><s:property value="get('operator')"/></a>
						</s:if>
						<s:else>
							*
						</s:else>
					</td>
					<td>
						<s:property value="get('tag')"/>
					</td>
					<td>
						<s:property value="get('safetyRisk')"/>
					</td>
					<td>
						<s:property value="get('productRisk')"/>
					</td>
					<td>
						<s:property value="get('con_type')"/>
					</td>
					<td>
						<s:if test="get('tradeID') > 0 ">
							<s:text name="Trade.%{get('tradeID')}.name" />
						</s:if>
						<s:else>
							*
						</s:else>
					</td>
                    <td>
                        <s:property value="get('dependentAuditType')"/>
                    </td>
                    <td>
                        <s:property value="get('dependentAuditStatus')"/>
                    </td>

                    <td>
						<s:property value="get('question')"/>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	
	<div class="alphapaging">
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</s:else>
