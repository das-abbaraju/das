<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="contractor.getVisibleContractorOperatorNumbers(permissions).size > 0">
	<table class="report">
		<thead>
			<tr>
				<s:if test="permissions.picsEmployee || permissions.corporate">
					<th>
						<s:text name="global.Operator" />
					</th>
				</s:if>
				<th>
					<s:text name="global.Type" />
				</th>
				<th>
					<s:text name="ContractorOperatorNumber.value" />
				</th>
				<s:if test="permissions.picsEmployee || permissions.operatorCorporate">
					<th>
						<s:text name="button.Edit" />
					</th>
				</s:if>
				<s:if test="permissions.picsEmployee || permissions.operatorCorporate">
					<th>
						<s:text name="button.Remove" />
					</th>
				</s:if>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="contractor.getVisibleContractorOperatorNumbers(permissions)" var="con_op_num">
				<tr>
					<s:if test="permissions.picsEmployee || permissions.corporate">
						<td>
							<s:property value="operator.name" />
						</td>
					</s:if>
					<td>
						<s:text name="%{type.i18nKey}" />
					</td>
					<td>
						<s:property value="value" />
					</td>
					<s:if test="permissions.picsEmployee || permissions.operatorCorporate">
						<td>
							<s:url action="ManageContractorOperatorNumber" method="edit" var="con_op_numbers_edit" />
							<a
								href="${con_op_numbers_edit}"
								class="edit"
								data-contractor="<s:property value="contractor.id" />"
								data-number="<s:property value="id" />"
							></a>
						</td>
					</s:if>
					<s:if test="permissions.picsEmployee || permissions.operatorCorporate">
						<td class="center">
							<s:url action="ManageContractorOperatorNumber" method="delete" var="con_op_numbers_delete" />
							<a
								href="${con_op_numbers_delete}"
								class="remove"
								data-contractor="<s:property value="contractor.id" />"
								data-number="<s:property value="id" />"
							></a>
						</td>
					</s:if>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>

<s:url action="ManageContractorOperatorNumber" var="con_op_numbers_link" method="edit" />
<a href="${con_op_numbers_link}" class="add" data-contractor="<s:property value="contractor.id" />">
	<s:text name="button.Add" />
</a>