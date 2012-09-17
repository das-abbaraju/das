<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<s:include value="../actionMessages.jsp" />
<div>
	<s:form id="contractor_operator_numbers_form">
		<s:hidden name="contractor" />
		<s:hidden name="number" />
		<fieldset class="form">
			<ol>
				<s:if test="permissions.picsEmployee || permissions.corporate">
					<li>
						<label>
							<s:text name="global.Operator" />
						</label>
						<s:select
							list="viewableOperators"
							listKey="%{id}"
							listValue="%{name}"
							headerKey=""
							headerValue="- %{getText('global.Operator')} -"
							name="number.operator"
							value="%{number.operator.id}"
                            id="contractor_numbers_client"
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
						headerValue="- %{getText('ContractorOperatorNumberType')} -"
                        id="contractor_numbers_type"
					/>
				</li>
                <li id="operator_user_list"></li>
				<li>
					<label>
						<s:text name="ContractorOperatorNumber.value" />
					</label>
					<s:textfield name="number.value" id="contractor_numbers_value" />
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<input
				type="button"
				class="picsbutton positive"
				value="<s:text name="button.Save" />"
				data-url="ManageContractorOperatorNumber!save.action"
			/>
			<input type="button" class="picsbutton negative closeButton" value="<s:text name="button.Close" />" />
		</fieldset>
	</s:form>
</div>