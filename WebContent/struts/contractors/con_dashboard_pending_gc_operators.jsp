<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<s:include value="../actionMessages.jsp" />
<s:if test="generalContractorsNeedingApproval.size > 0">
	<table class="report">
		<thead>
			<tr>
				<th>
					<s:text name="global.Operator" />
				</th>
				<th colspan="2">
					<s:text name="global.Action" />
				</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="generalContractorsNeedingApproval">
				<tr>
					<td>
						<s:property value="operatorAccount.name" />
					</td>
					<td class="center">
						<input
							type="button"
							class="picsbutton positive"
							value="<s:text name="AuditStatus.Approved.button" />"
							data-contractor="${contractor.id}"
							data-operator="${operatorAccount.id}"
						/>
					</td>
					<td class="center">
						<input
							type="button"
							class="picsbutton negative"
							value="<s:text name="button.Reject" />"
							data-contractor="${contractor.id}"
							data-operator="${operatorAccount.id}"
						/>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<div class="info">
		<s:text name="ContractorView.NoPendingGeneralContractors" />
	</div>
</s:else>