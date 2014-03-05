<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<%-- Url --%>
<s:url action="ContractorCertification" method="issueSsipCertificate" var="issue_certificate_url" />

<title>
    <s:text name="ContractorCertificate.SSIP.IssueCertificate.Title" />
</title>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">

    <h1>
        <s:text name="ContractorCertificate.SSIP.IssueCertificate.Title" />

        <span class="sub">
            <s:property value="contractor.name" />
        </span>
    </h1>

    <s:if test="hasFieldErrors()">
        <div class="error">
            <s:text name="JS.Validation.Contractor.Certificate.Error" />
            <s:fielderror />
        </div>
    </s:if>

    <form action="${issue_certificate_url}" method="post">
        <s:set value="%{@com.picsauditing.model.contractor.CertificationMethod@INTERNAL}" var="ssip_certified"/>
        <s:set value="%{@com.picsauditing.model.contractor.CertificationMethod@DEEMS_TO_SATISFY}" var="deems_to_satisfy"/>
        <s:set 
            var="deems_to_satisfy_checked"
            value="(certificationMethod == null || certificationMethod == 'DEEMS_TO_SATISFY') ? 'checked' : ''"
        />
        <s:set 
            var="ssip_certified_checked"
            value="(certificationMethod == 'INTERNAL') ? 'checked' : ''"
        />

        <input value="${contractor.id}" hidden name="contractor" />

        <fieldset>
            <legend><s:text name="ContractorCertificate.SSIP.certificateType" /></legend> 
            <input id="deems_to_satisfy" type="radio" name="certificationMethod" value="${deems_to_satisfy}" ${deems_to_satisfy_checked} />
            <label for="deems_to_satisfy"><s:text name="ContractorCertificate.SSIP.DeemsToSatisfy" /></label>
            <input id="ssip_certified" type="radio" name="certificationMethod" value="${ssip_certified}" ${ssip_certified_checked} />
            <label for="ssip_certified"><s:text name="ContractorCertificate.SSIP.SSIPCertified" /></label>
        </fieldset>
        <fieldset>
            <legend><s:text name="ContractorCertificate.SSIP.IssueDate" /></legend>
            <input type="text" name="issueYear" placeholder="YYYY" size="4" maxlength="4" value="${issueYear}" />
            <input type="text" name="issueMonth" placeholder="MM" size="2" maxlength="2" value="${issueMonth}" />
            <input type="text" name="issueDay" placeholder="DD" size="2" maxlength="2" value="${issueDay}" />
        </fieldset>
        <fieldset>
            <legend><s:text name="ContractorCertificate.SSIP.ExpirationDate" /></legend>
            <input type="text" name="expirationYear" placeholder="YYYY" size="4" maxlength="4" value="${expirationYear}" />
            <input type="text" name="expirationMonth" placeholder="MM" size="2" maxlength="2" value="${expirationMonth}" />
            <input type="text" name="expirationDay" placeholder="DD" size="2" maxlength="2" value="${expirationDay}" />
        </fieldset>

        <p>
            <span class="title"><s:text name="ContractorCertificate.SSIP.IssuedBy" /></span><br/>
            <s:property value="currentUserName" />
        </p>

        <button type="submit" class="picsbutton positive"><s:text name="ContractorCertificate.SSIP.IssueCertificate" /></button>
        <a href="${manualAuditUrl}" class="picsbutton"><s:text name="ContractorCertificate.SSIP.Cancel" /></a>
    </form>
</div>
