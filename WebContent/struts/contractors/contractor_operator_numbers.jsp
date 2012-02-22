<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<s:if test="contractor.getVisibleContractorOperatorNumbers(permissions).size > 0">
	<table class="report">
		<thead>
			<tr>
				<s:if test="permissions.picsEmployee">
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
			</tr>
		</thead>
		<tbody>
			<s:iterator value="contractor.getVisibleContractorOperatorNumbers(permissions)" var="con_op_num">
				<tr>
					<s:if test="permissions.picsEmployee">
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
							<s:url action="ManageContractorOperatorNumber" var="con_op_numbers_edit" method="edit">
								<s:param name="contractor" value="%{contractor.id}" />
								<s:param name="number" value="%{id}" />
							</s:url>
							<a href="${con_op_numbers_edit}" class="edit" rel="facebox"></a>
						</td>
					</s:if>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>

<s:url action="ManageContractorOperatorNumber" var="con_op_numbers_link" method="edit">
	<s:param name="contractor" value="%{contractor.id}" />
</s:url>
<a href="${con_op_numbers_link}" class="add" rel="facebox">
	<s:text name="button.Add" />
</a>