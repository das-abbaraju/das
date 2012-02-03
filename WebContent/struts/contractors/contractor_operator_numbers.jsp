<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<table class="report">
	<thead>
		<tr>
			<s:if test="permissions.picsEmployee || permissions.contractor">
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
		</tr>
	</thead>
	<tbody>
		<tr>
			<s:if test="permissions.picsEmployee || permissions.operatorCorporate">
				<td colspan="<s:property value="permissions.picsEmployee ? 3 : 2" />">
					<s:form id="contractor_operator_numbers_form">
						<s:if test="permissions.picsEmployee">
							<s:select
								list="contractor.operators"
								listKey="%{operatorAccount.id}"
								listValue="%{operatorAccount.name}"
								headerKey=""
								headerValue="- %{getText('global.Operator')} -"
								name="number.operator"
							/>
						</s:if>
						<s:hidden name="contractor" />
						<s:select
							list="@com.picsauditing.jpa.entities.ContractorOperatorNumberType@values()"
							name="number.type"
							listValue="name()"
							listKey="name()"
							headerKey=""
							headerValue="- %{getText('ContractorOperatorNumber.type')} -"
						/>
						<s:textfield name="number.value" />
						<a href="#" id="contractor_operator_numbers_add" class="add"><s:text name="button.Add" /></a>
					</s:form>
				</td>
			</s:if>
		</tr>
	</tbody>
</table>