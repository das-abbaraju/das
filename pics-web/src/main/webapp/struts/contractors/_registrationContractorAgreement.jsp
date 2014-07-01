<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="contractor_agreement">
    <input class="accept_contractor_agreement" type="checkbox"><s:text name="RegistrationMakePayment.AgreeToTermsAndConditions" /> <s:a href="#" cssClass="contractor-agreement modal-link" data-url="ContractorAgreement.action?id=${contractor.id}"><s:text name="RegistrationMakePayment.ContractorAgreement" /></s:a>
</div>