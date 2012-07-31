<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="contractor_id" value="%{contractor.id}" />
<s:set var="general_contractor_id" value="%{operator.id}" />

<s:if test="searchResults.size() == 0">
	<div class="alert">
		<s:text name="ContractorFacilities.NoFoundFacilities" />
	</div>
</s:if>
<s:else>
	<s:if test="state == '' && operator.name == ''">
		<div id="help">
			<s:text name="ContractorFacilities.SuggestedOperators" />:
		</div>
	</s:if>
	<table class="report">
		<thead>
			<tr>
				<th>
					<s:text name="ContractorFacilities.OperatorName" />
				</th>
				<th>
					<s:text name="ContractorFacilities.AddOperator" />
				</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="searchResults" var="operator_result">
				<tr>
					<td class="account${status}">
						<s:if test="permissions.admin">
							<s:url action="FacilitiesEdit" var="facilities_edit">
								<s:param name="operator">
									${id}
								</s:param>
							</s:url>
							<a href="${facilities_edit}">
								<s:property value="fullName" />
							</a>
						</s:if>
						<s:else>
							<s:property value="fullName" />
						</s:else>
						<div class="operatorlocation">
							<s:property value="getShortAddress(permissions.getCountry())" />
						</div>
					</td>
					<td class="center">
						<a href="javascript:;"
							class="add"
							data-contractor="${contractor_id}"
							data-general-contractor="${general_contractor_id}"
							data-operator="${operator_result.id}"
							data-operator-name="${operator_result.name}"
							data-needs-modal="<s:property value="isNeedsGeneralContractorModal(#operator_result)" />">
 							<s:text name="button.Add" />
						</a>
					</td>
				</tr>
			</s:iterator>
			<s:if test="searchResults.size() > 1 && !permissions.corporate">
				<tr>
					<td colspan="2" class="right" style="padding: 10px 0 10px 0;">
						<a href="javascript:;" id="show_all_operators" class="picsbutton positive">
							<s:text name="ContractorFacilities.ShowAllOperators" />
						</a>
					</td>
				</tr>
			</s:if>
		</tbody>
	</table>

	<s:include value="../actionMessages.jsp" />
</s:else>