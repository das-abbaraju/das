<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div style="width: 100%; height: 400px; overflow: auto;">
	<s:if test="!gcContractorOperators.empty && !subcontractors.empty">
		<table class="report">
			<thead>
				<tr>
					<th>
						<s:text name="GeneralContractor.SubContractor" />
					</th>
					<s:iterator value="gcContractorOperators" var="gcOp">
						<th><s:property value="#gcOp.name" /></th>
					</s:iterator>
				</tr>
			</thead>
			<s:iterator value="subcontractors" var="sub">
				<tr>
					<td>
						<a href="ContractorView.action?id=<s:property value="#sub.id" />">
							<s:property value="#sub.name" />
						</a>
					</td>
					<s:iterator value="gcContractorOperators" var="gcOp2">
						<td class="center">
							<s:url var="contractor_flag" action="ContractorFlag">
								<s:param name="id" value="%{#sub.id}" />
								<s:param name="opID" value="%{#gcOp2.id}" />
							</s:url>
							<a href="${contractor_flag}">
								<s:property value="table.get(#gcOp2.id, #sub.id).smallIcon" escape="false" />
							</a>
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
		</table>
	</s:if>
	<s:else>
		<div class="alert">
			<s:text name="Report.message.NoRowsFound" />
		</div>
	</s:else>
</div>