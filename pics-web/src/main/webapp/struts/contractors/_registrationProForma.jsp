<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="pro_forma_form">
    <p><s:text name="Registration.ProForma.Email.Details" /></p>

    <s:include value="/struts/contractors/_registrationContractorAgreement.jsp" />

    <s:form action="RegistrationMakePayment.action" method="POST">
        <input type="hidden" name="button" value="email" />
        <input type="hidden" name="invoice.id" value="<s:property value="invoice.id"/>" />

        <s:submit id='pro_forma_button'
            key="Registration.ProForma.Email.Button"
            cssClass="btn success"
            disabled="true"
        />
    </s:form>
</div>