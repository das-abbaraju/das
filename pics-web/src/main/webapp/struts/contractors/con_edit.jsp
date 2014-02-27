<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>

<s:set var="subdivisionList" value="getCountrySubdivisionList(contractor.country.isoCode)"/>
<s:set var="iso_code" value="contractor.country.isoCode"/>
<s:set var="country_subdivision" value="contractor.countrySubdivision"/>
<s:set var="billing_country_subdivision" value="contractor.billingCountrySubdivision"/>

<title><s:property value="contractor.name"/></title>

<meta name="help" content="User_Manual_for_Contractors">

<s:include value="conHeader.jsp"/>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
<s:if test="permissions.admin && unpaidInvoices.size() > 0">
    <div class="info">
        Invoices open for this contractor

        <ol>
            <s:iterator value="unpaidInvoices">
                <li>
                    <a href="InvoiceDetail.action?invoice.id=<s:property value="id"/>"><s:property value="id"/></a>
                </li>
            </s:iterator>
        </ol>
    </div>
</s:if>

<s:if test="permissions.admin && contractor.qbSync">
    <div class="alert noprint">
        This contractor is still waiting to be synced with QuickBooks!
    </div>
</s:if>

<s:if test="contractor.accountLevel.bidOnly">
    <div class="alert">
        <s:text name="ContractorView.BidOnlyUpgradeAlert"/>
    </div>
</s:if>

<s:form id="save" method="POST">
<br clear="all"/>

<s:hidden name="id"/>

<table width="100%">
<tr>
<td style="vertical-align: top; width: 50%;">
<fieldset class="form">
    <h2 class="formLegend"><s:text name="ContractorEdit.Details.heading"/></h2>

    <ol>
        <li>
            <label><s:text name="ContractorEdit.Details.Name"/>:</label>
            <s:set var="contractor_name_is_editable"
                   value="%{permissions.picsEmployee || (permissions.contractor && permissions.accountStatus.pending)}"/>
            <s:if test="#contractor_name_is_editable">
                <s:textfield name="contractor.name" size="35"/>
            </s:if>
            <s:else>
                ${contractor.name}
            </s:else>
        </li>
        <li>
            <label><s:text name="ContractorEdit.Details.DBAName"/>: </label>
            <s:if test="#contractor_name_is_editable">
                <s:textfield name="contractor.dbaName" size="35"/>
            </s:if>
            <s:else>
                ${contractor.dbaName}
            </s:else>
        </li>
        <li>
            <label><s:text name="ContractorEdit.Details.DateCreated"/>:</label>
            <s:date name="contractor.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}"/>
        </li>
    </ol>
</fieldset>

<fieldset class="form">
    <h2 class="formLegend"><s:text name="global.primaryAddress"/></h2>

    <ol>
        <li>
            <label><s:text name="global.Address"/>:</label>
            <s:textfield name="contractor.address" size="35"/>
            <br/>
            <s:textfield name="contractor.address2" size="35"/>
        </li>
        <li>
            <label><s:text name="ContractorEdit.PrimaryAddress.City"/>:</label>
            <s:textfield name="contractor.city" size="20"/>
        </li>
        <li class="country">
            <label><s:text name="Country"/>:</label>
            <s:select
                    list="countryList"
                    name="contractor.country.isoCode"
                    id="contractorCountry"
                    listKey="isoCode"
                    listValue="name"
                    value="contractor.country.isoCode"
                    cssClass="select2"/>
        </li>
        <li id="countrySubdivision_li" data="<s:property value="#country_subdivision"/>">
            <s:include value="/struts/contractors/_country_subdivision_select.jsp">
                <s:param name="country_iso_code">${iso_code}</s:param>
                <s:param name="subdivision_id">contractor_countrySubdivision</s:param>
                <s:param name="select_name">countrySubdivision</s:param>
                <s:param name="selected_subdivision_iso_code">${country_subdivision}</s:param>
                <s:param name="select_theme">pics</s:param>
                <s:param name="select_css">select2</s:param>
            </s:include>
        </li>

        <s:if test="contractor.country.isoCode != 'AE'">
            <li id="zip_li">
                <label><s:text name="ContractorEdit.PrimaryAddress.Zip"/>:</label>
                <s:textfield name="contractor.zip" size="7"/>
            </li>
        </s:if>

        <li>
            <s:select
                    name="contractor.timezone"
                    value="contractor.timezone.iD"
                    theme="form"
                    list="@com.picsauditing.util.TimeZoneUtil@timeZones()"
                    cssClass="select2" />
        </li>

        <li>
            <label><s:text name="ContractorEdit.PrimaryAddress.DefaultLanguage"/>:</label>
            <s:select
                    name="contractor.locale"
                    list="supportedLanguages.visibleLocales"
                    listValue="displayName"
                    cssClass="select2" />
        </li>

        <li>
            <label><s:text name="ContractorEdit.PrimaryAddress.CompanyPhone"/>:</label>
            <s:textfield name="contractor.phone"/>
        </li>
        <li>
            <label><s:text name="ContractorEdit.PrimaryAddress.CompanyFax"/>:</label>
            <s:textfield name="contractor.fax"/>
        </li>
        <li>
            <label><s:text name="ContractorEdit.PrimaryAddress.PrimaryContact"/>:</label>
            <s:select
                    list="userList"
                    name="contactID"
                    listKey="id"
                    listValue="name"
                    value="%{contractor.primaryContact.id}"
                    cssClass="select2" />

            <s:if test="permissions.admin">
                <a href="UsersManage!add.action?account=<s:property value="contractor.id"/>&isActive=Yes&isGroup=&userIsGroup=No">
                    <s:text name="UsersManage.addUser"/>
                </a>
            </s:if>
            <s:else>
                <pics:permission perm="ContractorAdmin">
                    <a href="UsersManage!add.action?account=<s:property value="contractor.id"/>&isActive=Yes&isGroup=&userIsGroup=No">
                        <s:text name="UsersManage.addUser"/>
                    </a>
                </pics:permission>
            </s:else>
        </li>
    </ol>
</fieldset>

<fieldset class="form">
    <h2 class="formLegend"><s:text name="ContractorEdit.BillingDetails.heading"/></h2>

    <ol>
        <li>
            <label><s:text name="ContractorEdit.billingAddress"/>:</label>
            <s:textfield name="contractor.billingAddress" size="35"/><br/>
        </li>
        <li>
            <label><s:text name="ContractorEdit.billingCity"/>:</label>
            <s:textfield name="contractor.billingCity" size="20"/>
        </li>
        <li>
            <label><s:text name="ContractorEdit.billingCountry"/>:</label>
            <input type="text" disabled="disabled" id="billing_country" value="${contractor.country.name}"/>
        </li>
        <li id="billing_countrySubdivision_li" data="<s:property value="#billing_country_subdivision"/>">
            <s:include value="/struts/contractors/_country_subdivision_select.jsp">
                <s:param name="country_iso_code">${iso_code}</s:param>
                <s:param name="subdivision_id">contractor_billing_countrySubdivision</s:param>
                <s:param name="select_name">countrySubdivision</s:param>
                <s:param name="selected_subdivision_iso_code">${billing_country_subdivision}</s:param>
                <s:param name="select_theme">pics</s:param>
                <s:param name="select_css">select2</s:param>
            </s:include>
        </li>
        <li id="billing_zip_li">
            <label><s:text name="ContractorEdit.billingZip"/>:</label>
            <s:textfield name="contractor.billingZip" size="7"/>
        </li>
        <li>
            <s:checkbox name="contractor.hasVatId" value="false"/>
            <s:text name="ContractorEdit.HaveVatId"/>
        </li>
        <li id="vat_wrapper">
            <label><s:text name="ContractorAccount.vatId"/>:</label>
            <s:textfield name="contractor.vatId"/>
        </li>
    </ol>
</fieldset>

<fieldset class="form">
    <h2 class="formLegend"><s:text name="ContractorEdit.IndustryDetails.heading"/></h2>

    <ol>
        <s:if test="contractor.country.isoCode != 'AE'">
            <li id="tax_li">
                <label><s:div cssClass="taxIdLabel"/></label>
                <s:property value="contractor.taxId"/>
            </li>
        </s:if>

        <li>
            <label><s:text name="ContractorEdit.IndustryDetails.NAICSPrimary"/>:</label>
            <s:property value="contractor.topTradesNaicsCode"/>
        </li>
        <li>
            <label><s:text name="ContractorEdit.SoleProprietor.heading"/>:</label>

            <s:if test="contractor.soleProprietor">
                <s:text name="YesNo.Yes"/>
            </s:if>
            <s:else>
                <s:text name="YesNo.No"/>
            </s:else>
        </li>

        <pics:toggle name="<%= FeatureToggle.TOGGLE_SAFETY_SENSITIVE_ENABLED %>">
            <li>
                <label><s:text name="global.SafetySensitive"/>:</label>
                <s:if test="contractor.safetySensitive">
                    <s:text name="YesNo.Yes"/>
                </s:if>
                <s:else>
                    <s:text name="YesNo.No"/>
                </s:else>
            </li>
            <pics:permission perm="AllContractors">
                <s:if test="contractor.safetyRisk != null">
                    <li>
                        <label><s:text name="global.SafetyRisk"/>:</label>
                        <s:text name="%{contractor.safetyRisk.i18nKey}"/>
                    </li>
                </s:if>
            </pics:permission>
        </pics:toggle>
        <pics:toggleElse>
            <s:if test="contractor.safetyRisk != null">
                <li>
                    <label><s:text name="global.SafetyRisk"/>:</label>
                    <s:text name="%{contractor.safetyRisk.i18nKey}"/>
                </li>
            </s:if>
        </pics:toggleElse>

        <s:if test="contractor.materialSupplier && contractor.productRisk != null">
            <li>
                <label><s:text name="global.ProductRisk"/>:</label>
                <s:text name="%{contractor.productRisk.i18nKey}"/>
            </li>
        </s:if>

        <s:if test="contractor.transportationServices && contractor.transportationRisk != null">
            <li>
                <label><s:text name="global.TransportationRisk"/>:</label>
                <s:text name="%{contractor.transportationRisk.i18nKey}"/>
            </li>
        </s:if>

        <li>
            <label><s:text name="ContractorEdit.IndustryDetails.RequestedBy"/>:</label>
            <s:property value="contractor.requestedBy.name"/>
        </li>
    </ol>
</fieldset>

<fieldset class="form">
    <h2 class="formLegend"><s:text name="ContractorEdit.CompanyIdentification.heading"/></h2>
    <ol>
        <li>
            <label>
                <s:text name="ContractorEdit.CompanyIdentification.WebURL"/>:
            </label>
            <s:textfield name="contractor.webUrl" size="35"/>
        </li>
        <li>
            <label>
                <s:text name="ContractorEdit.CompanyIdentification.CompanyLogo"/>:
            </label>
            <s:file name="logo" size="35" label="Testing"/>
            <br/>
            <s:text name="global.AllowedFormats">
                <s:param>jpg, gif, png</s:param>
            </s:text>
        </li>
        <li>
            <label>
                <s:text name="ContractorEdit.CompanyIdentification.CompanyBrochure"/>:
            </label>
            <s:file name="brochure" size="35"/>
            <br/>
            <s:text name="global.AllowedFormats">
                <s:param>pdf, doc, jpg, gif, png</s:param>
            </s:text>
        </li>
        <li>
            <s:checkbox name="contractor.showInDirectory" theme="formhelp"/>
            <s:if test="contractor.showInDirectory">
                <br/>
                <pics:toggle name="<%= FeatureToggle.TOGGLE_BADGE %>">
                    <a href="ContractorBadge.action?contractor=<s:property value="contractor.id" />" class="preview">
                        <s:text name="ContractorView.ClickToViewContractorBadge"/>
                    </a>
                </pics:toggle>
            </s:if>
        </li>
        <li>
            <s:checkbox
                    name="contractor.autoApproveRelationships"
                    cssClass="checkbox"
                    theme="formhelp"
                    />
        </li>
        <li>
            <label><s:text name="ContractorEdit.CompanyIdentification.Description"/>:</label>
            <s:textarea name="contractor.description" cols="40" rows="15"/>
        </li>
    </ol>
</fieldset>

<fieldset class="form submit">
    <s:if test="permissions.contractor">
        <s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}"/>
    </s:if>
    <s:else>
        <pics:permission perm="ContractorAccounts" type="Edit">
            <s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}"/>
        </pics:permission>
    </s:else>

    <pics:permission perm="RemoveContractors">
        <s:submit cssClass="picsbutton negative" method="delete" value="%{getText('button.Delete')}"
                  onclick="return confirm('Are you sure you want to delete this account?');"/>
    </pics:permission>
</fieldset>
</td>

<s:if test="permissions.admin">
<td style="vertical-align: top; width: 50%; padding-left: 10px;">
<fieldset class="form">
    <h2 class="formLegend">PICS Admin Fields</h2>
    <ol>
        <li>
            <label>Status:</label>
            <input type="hidden" name="currentStatus" value="<s:property value="contractor.status" />"/>
            <s:select
                list="statusList"
                name="contractor.status"
                value="%{contractor.status}"
                cssClass="select2Min"/>
        </li>
        <li>
            <label>Will Renew:</label>

            <s:if test="contractor.renew">
                Yes - <s:submit method="deactivate" value="Cancel Account"/>
                <pics:fieldhelp></pics:fieldhelp>
            </s:if>
            <s:else>
                No - <s:submit method="reactivate" value="Reactivate"/>
            </s:else>
        </li>
        <li>
            <label><s:text name="ContractorEdit.SoleProprietor.heading"/></label>
            <s:checkbox name="contractor.soleProprietor"/>
        </li>
        <li>
            <label>Account Level:</label>
            <s:select
                list="@com.picsauditing.jpa.entities.AccountLevel@values()"
                name="contractor.accountLevel"
                cssClass="select2Min"/>
        </li>
        <li>
            <label>Reason:</label>
            <s:select
                list="deactivationReasons"
                name="contractor.reason"
                headerKey=""
                headerValue="- %{getText('Filters.header.DeactivationReason')} -"
                listKey="key" listValue="value"
                cssClass="select2Min"/>
        </li>

        <s:if test="canEditRiskLevel">
            <li>
                <label>Risk Levels:</label>
                <a href="ContractorEditRiskLevel.action?id=<s:property value="contractor.id" />" class="edit">Edit Risk
                    Levels</a>
            </li>
        </s:if>

        <s:if test="contractor.country.isoCode != 'AE'">
            <li id="taxIdItem">
                <label><s:div cssClass="taxIdLabel"/></label>
                <s:textfield id="contractorTaxId" name="contractor.taxId" size="15" maxLength="15"/>
                <s:div cssClass="fieldhelp" id="taxIdLabelHelp"/>
            </li>
        </s:if>

        <li>
            <label>Must Pay?</label>
            <s:radio
                    list="#{'Yes':'Yes','No':'No'}"
                    name="contractor.mustPay"
                    value="contractor.mustPay"
                    theme="pics"
                    cssClass="inline"
                    />
        </li>
        <li>
            <label>Upgrade Date:</label>
            <input name="contractor.lastUpgradeDate" type="text" class="forms datepicker" size="10"
                   value="<s:date name="contractor.lastUpgradeDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />"/>
        </li>
        <li>
            <label>Contractor Type:</label>
            <s:if test="getContractorTypeHelpText() != ''">
                <div class="alert-error alert-message warning">
                    <span class="icon warn"></span>

                    <p><s:property value="getContractorTypeHelpText()" escape="false"/></p>
                </div>
            </s:if>
            <s:checkbox
                    name="conTypes"
                    id="onSite"
                    fieldValue="Onsite"
                    label="ContractorAccount.onsiteServices"
                    value="contractor.onsiteServices"
                    cssClass="checkbox"
                    disabled="contractor.isContractorTypeRequired(@com.picsauditing.jpa.entities.ContractorType@Onsite) && contractor.onsiteServices"
                    /><s:text name="ContractorAccount.onsiteServices"/>
            <s:checkbox
                    name="conTypes"
                    id="offSite"
                    fieldValue="Offsite"
                    label="ContractorAccount.offsiteServices"
                    value="contractor.offsiteServices"
                    cssClass="checkbox"
                    disabled="contractor.isContractorTypeRequired(@com.picsauditing.jpa.entities.ContractorType@Offsite) && contractor.offsiteServices"
                    /><s:text name="ContractorAccount.offsiteServices"/>
            <s:checkbox
                    name="conTypes"
                    id="materialSupplier"
                    fieldValue="Supplier"
                    label="ContractorAccount.materialSupplier"
                    value="contractor.materialSupplier"
                    cssClass="checkbox"
                    disabled="contractor.isContractorTypeRequired(@com.picsauditing.jpa.entities.ContractorType@Supplier) && contractor.materialSupplier"
                    /><s:text name="ContractorAccount.materialSupplier"/>
            <s:checkbox
                    name="conTypes"
                    id="transportation"
                    fieldValue="Transportation"
                    label="ContractorAccount.transportationServices"
                    value="contractor.transportationServices"
                    cssClass="checkbox"
                    disabled="contractor.isContractorTypeRequired(@com.picsauditing.jpa.entities.ContractorType@Transportation) && contractor.transportationServices"
                    /><s:text name="ContractorAccount.transportationServices"/>
            <pics:fieldhelp title="Contractor Type">
                <s:iterator value="@com.picsauditing.jpa.entities.ContractorType@values()" id="conType">
                    <h5><s:property value="#conType.type"/></h5>
                    <s:text name="%{'ContractorType.' + #conType + '.Description'}"></s:text>
                    <br/>
                </s:iterator>
            </pics:fieldhelp>
        </li>
        <li>
            <s:if test="hasImportPQFAudit">
                <s:submit method="expireImportPQF" cssClass="picsbutton negative" id="removeImportPQFButton"
                          value="Remove Import PQF"/>
            </s:if>
            <s:elseif test="contractor.eligibleForImportPQF">
                <s:submit method="createImportPQF" cssClass="picsbutton positive" value="Create Import PQF"/>
            </s:elseif>

            <div class="fieldhelp">
                <h3>Competitor Membership</h3>

                <p>
                    Clicking on "Remove Import PQF" will only expire any existing Import PQF. This will NOT remove the
                    invoice/fee. Voiding the invoice/fee will have to be done manually.
                </p>
            </div>
        </li>
        <pics:permission perm="EditContractorSafeHarbor">
            <li>
                <label><s:text name="ContractorEdit.AutoAdd"/></label>
                <s:radio
                        name="contractor.autoAddClientSite"
                        list="#{true:getText('ContractorEdit.AutoAdd.Yes'), false:getText('ContractorEdit.AutoAdd.No')}"
                        theme="pics"
                        labelposition="left"/>
            </li>
        </pics:permission>

        <s:set var="disabled" value="permissions.hasGroup(User.GROUP_MANAGER) ? '' : 'disabled'" />
        <s:set var="salesforceIdHelpTextTitle"><s:text name="contractorEdit.salesforceIdHelpTextTitle" /></s:set>
        <li>
            <label><s:text name="contractorEdit.salesforceIdLabel" /></label>
            <input type="text" name="contractor.salesRepId" value="${contractor.salesRepId}" ${disabled} size="15" maxlength="15" />
            <pics:fieldhelp title="${salesForceIdHelpTextTitle}">
                <h5><s:text name="contractorEdit.salesforceIdHelpTextHeader" /></h5>
                <s:text name="contractorEdit.salesforceIdHelpTextBody" />
                <br />
            </pics:fieldhelp>
        </li>
    </ol>
</fieldset>

<pics:permission perm="UserZipcodeAssignment">
    <fieldset class="form">
        <h2 class="formLegend">Specific Assignment</h2>
        <ol>
            <li>
                <label>CSR Assignment</label>
                <s:if test="contractor.dontReassign">
                    <s:set var="default" value="contractor.currentCsr.id"/>
                </s:if>
                <s:else>
                    <s:set var="default" value="0"/>
                </s:else>
                <s:select
                        name="csrId"
                        list="csrList"
                        listKey="id"
                        listValue="name"
                        multiple="false"
                        headerKey="0"
                        headerValue="AUTO ASSIGNED"
                        value="%{(contractor.dontReassign)?contractor.currentCsr.id:0}"
                        cssClass="select2" />
            </li>
            <s:if test="showISRAssginment()">
                <li>
                    <label>Inside Sales Assignment</label>
                    <s:select
                            name="insideSalesId"
                            list="insideSalesList"
                            listKey="id"
                            listValue="name"
                            multiple="false"
                            headerKey="0"
                            headerValue="- Please Select -"
                            value="%{contractor.currentInsideSalesRepresentative.id}"
                            cssClass="select2" />
                </li>
            </s:if>
        </ol>
    </fieldset>
</pics:permission>

<fieldset class="form submit">
    <ol>
        <li>
            <s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}"/>
        </li>
    </ol>
</fieldset>

<pics:permission perm="EmailOperators">
    <fieldset class="form bottom">
        <h2 class="formLegend">De-activation Email</h2>

        <ol>
            <li>
                <s:submit
                        cssClass="picsbutton positive"
                        method="sendDeactivationEmail"
                        value="%{getText(scope + '.button.SendDeactivationEmail')}"/>
            </li>
            <li>
                <s:select
                        list="operatorList"
                        name="operatorIds"
                        listKey="id"
                        listValue="name"
                        multiple="true"
                        cssClass="select2" />
            </li>
        </ol>
    </fieldset>
</pics:permission>
</td>
</s:if>
</tr>
</table>
</s:form>
</div>
