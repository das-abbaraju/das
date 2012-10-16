<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:url action="ManageContractorOperatorNumber" method="save" var="contractor_number_save" />

<s:include value="../actionMessages.jsp" />

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
                        headerValue="- %{getText('global.Operator')} -"
                        headerKey=""
                        name="clientSite"
                        value="%{clientSite}"
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
                    name="clientType"
                    listValue="name()"
                    listKey="name()"
                    headerKey=""
                    headerValue="- %{getText('ContractorOperatorNumberType')} -"
                    id="contractor_numbers_type"
                    value="%{clientType}"
                />
			</li>
			<li>
				<label>
					<s:text name="ContractorOperatorNumber.value" />
				</label>

                <s:if test="(clientType == 'Buyer' || clientType == 'EHS') && clientSite > 0">
                    <s:select list="getUsersList()" listKey="name" listValue="name" id="contractor_numbers_value"
                        name="number.value" value="%{number.value}"
                        headerKey="0" />
                </s:if>
                <s:else>
				    <s:textfield name="number.value" id="contractor_numbers_value" />
                </s:else>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<input
			type="button"
			class="picsbutton positive"
			value="<s:text name="button.Save" />"
			data-url="${contractor_number_save}"
		/>
		<input type="button" class="picsbutton negative closeButton" value="<s:text name="button.Close" />" />
	</fieldset>
</s:form>