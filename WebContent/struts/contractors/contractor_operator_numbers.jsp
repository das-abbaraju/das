<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<s:if test="contractor.contractorOperatorNumbers.size > 0">
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
			</tr>
		</thead>
		<tbody>
			<s:iterator value="contractor.contractorOperatorNumbers">
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
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>

<a href="#contractor_operator_numbers_div" class="add" rel="facebox">
	<s:text name="button.Add" />
</a>

<div class="hide" id="contractor_operator_numbers_div">
	<s:form id="contractor_operator_numbers_form">
		<s:hidden name="contractor" />
		<fieldset class="form">
			<h2 class="formLegend">
				<s:text name="ContractorOperatorNumber" />
			</h2>
			<ol>
				<s:if test="permissions.picsEmployee">
					<li>
						<label>
							<s:text name="global.Operator" />
						</label>
						<s:select
							list="contractor.operators"
							listKey="%{operatorAccount.id}"
							listValue="%{operatorAccount.name}"
							headerKey=""
							headerValue="- %{getText('global.Operator')} -"
							name="number.operator"
						/>
					</li>
				</s:if>
				<li>
					<label>
						<s:text name="ContractorOperatorNumberType" />
					</label>
					<s:select
						list="@com.picsauditing.jpa.entities.ContractorOperatorNumberType@values()"
						name="number.type"
						listValue="name()"
						listKey="name()"
						headerKey=""
						headerValue="- %{getText('ContractorOperatorNumber.type')} -"
					/>
				</li>
				<li>
					<label>
						<s:text name="ContractorOperatorNumber.value" />
					</label>
					<s:textfield name="number.value" />
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<s:submit
				method="save"
				action="ManageContractorOperatorNumber"
				value="%{getText('button.Save')}"
				cssClass="picsbutton positive"
			/>
		</fieldset>
	</s:form>
</div>