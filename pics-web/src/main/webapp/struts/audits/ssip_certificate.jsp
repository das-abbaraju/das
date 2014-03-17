<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ContractorCertification" method="issueSsipCertificate" var="issue_certificate_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="ContractorCertificate.SSIP.IssueCertificate.Title" /></s:param>
    <s:param name="subtitle"><s:property value="contractor.name" /></s:param>
</s:include>

<title>
    <s:text name="ContractorCertificate.SSIP.IssueCertificate.Title" />
</title>

<s:if test="hasFieldErrors()">
    <div class="col-md-8">
        <div class="alert alert-danger">
            <s:text name="JS.Validation.Contractor.Certificate.Error" />
            <s:fielderror />
        </div>
    </div>
</s:if>

<div class="col-md-8">
    <form action="${issue_certificate_url}" method="post" class="form-horizontal" role="form">

        <s:set value="%{@com.picsauditing.model.contractor.CertificationMethod@INTERNAL}" var="ssip_certified"/>
        <s:set value="%{@com.picsauditing.model.contractor.CertificationMethod@DEEMS_TO_SATISFY}" var="deems_to_satisfy"/>
        <s:set 
            var="deems_to_satisfy_checked"
            value="(certificationMethod == null || certificationMethod == 'DEEMS_TO_SATISFY') ? 'selected' : ''"
        />
        <s:set 
            var="ssip_certified_checked"
            value="(certificationMethod == 'INTERNAL') ? 'selected' : ''"
        />

        <s:set var="available_scopes" value="cdmScopeItems" />

        <input value="${contractor.id}" hidden name="contractor" />

        <fieldset>
            <div class="form-group">
                <label class="col-md-3 control-label"><strong><s:text name="ContractorCertificate.SSIP.certificateType" /></strong></label>
                <div class="col-md-4 col-xs-11">
                    <select name="certificationMethod" class="form-control select2Min" tabindex="1">
                        <option value="${deems_to_satisfy}" ${deems_to_satisfy_checked}><s:text name="ContractorCertificate.SSIP.DeemsToSatisfy" /></option>
                        <option value="${ssip_certified}" ${ssip_certified_checked}><s:text name="ContractorCertificate.SSIP.SSIPCertified" /></option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label"><s:text name="ContractorCertificate.SSIP.cdmScope" /></label>
                <div class="col-md-4 col-xs-11">
                    <select name="cdmScopes" multiple="true" class="form-control select2"tabindex="2">
                        <s:iterator value="#available_scopes" var="available_scope">

                            <s:set var="is_selected" value="''" />
                            <s:iterator value="#selected_scopes" var="selected_scope">
                                <s:if test="#available_scope == #selected_scope">
                                    <s:set var="is_selected" value="'selected'" />
                                </s:if>
                            </s:iterator>

                            <option value="${available_scope.dbValue}" ${is_selected}>${available_scope.displayValue}</option>
                        </s:iterator>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label"><strong><s:text name="ContractorCertificate.SSIP.issueDate" /></strong></label>
                <div class="col-md-4">
                    <fieldset>
                        <div class="row date">
                            <div class="col-md-4 col-sm-4 col-xs-6">
                                <input name="issueYear" type="text" placeholder="YYYY" maxlength="4" class="form-control year" value="${issueYear > 0 ? issueYear : ''}" tabindex="3" />
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <input name="issueMonth" type="text" placeholder="MM" maxlength="2" class="form-control month" value="${issueMonth > 0 ? issueMonth : ''}" tabindex="4" />
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <input name="issueDay" type="text" placeholder="DD" maxlength="2" class="form-control day" value="${issueDay > 0 ? issueDay : ''}" tabindex="5" />
                            </div>
                            <div class="col-md-1 col-sm-1 col-xs-12">
                                <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd" tabindex="6"><i class="icon-calendar"></i></a>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label"><strong><s:text name="ContractorCertificate.SSIP.expirationDate" /></strong></label>
                <div class="col-md-4">
                    <fieldset>
                        <div class="row date">
                            <div class="col-md-4 col-sm-4 col-xs-6">
                                <input name="expirationYear" type="text" placeholder="YYYY" maxlength="4" class="form-control year" value="${expirationYear > 0 ? expirationYear : ''}" tabindex="7" />
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <input name="expirationMonth" type="text" placeholder="MM" maxlength="2" class="form-control month" value="${expirationMonth > 0 ? expirationMonth : ''}" tabindex="8" />
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <input name="expirationDay" type="text" placeholder="DD" maxlength="2" class="form-control day" value="${expirationDay > 0 ? expirationDay : ''}" tabindex="9" />
                            </div>
                            <div class="col-md-1 col-sm-1 col-xs-12">
                                <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd" tabindex="10"><i class="icon-calendar"></i></a>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>

            <div class="form-group">
                <label labelName="employees" class="col-md-3 control-label"><s:text name="ContractorCertificate.SSIP.IssuedBy" /></label>
                <div class="col-md-4">
                    <p style="padding-top:7px;"><s:property value="currentUserName" /></p>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-9 col-md-offset-3 form-actions">
                    <button type="submit" class="btn btn-success" tabindex="11"><s:text name="ContractorCertificate.SSIP.IssueCertificate" /></button>
                    <a href="${manualAuditUrl}" class="btn btn-default" tabindex="12"><s:text name="ContractorCertificate.SSIP.Cancel" /></a>
                </div>
            </div>
        </fieldset>
    </form>
</div>
