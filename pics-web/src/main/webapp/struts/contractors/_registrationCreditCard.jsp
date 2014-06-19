<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="credit_card_form">
    <s:form action="%{paymentUrl}" cssClass="make-payment-form" theme="pics" autocomplete="off">
        <input type="hidden" name="redirect" value="<s:property value="requestString"/>?processPayment=true"/>
        <%-- This just adds a credit card and returns us back to the completeRegistration action method --%>
        <%-- We must do it this way, or we are not PCI compliant --%>
        <s:hidden name="hash"></s:hidden>
        <s:hidden name="key_id"></s:hidden>
        <s:hidden name="orderid"></s:hidden>
        <s:hidden name="amount"></s:hidden>
        <s:hidden name="time"></s:hidden>
        <s:hidden name="company"></s:hidden>
        <s:hidden name="customer_vault_id"></s:hidden>

        <s:if test="cc == null">
            <input type="hidden" name="customer_vault" value="add_customer"/>
        </s:if>
        <s:else>
            <input type="hidden" name="customer_vault" value="update_customer"/>
        </s:else>

        <ul id="credit-card-inputs">
            <li>
                <s:select label="CreditCard.Type" list="creditCardTypes" name="ccName" />
            </li>

            <li class="creditcard">
                <s:textfield label="CreditCard.Number" name="ccnumber" autocomplete="off" />
                <s:if test="invoice.currency.USD || invoice.currency.CAD">
                    <img src="images/creditcard.png" class="card" />
                </s:if>
                <s:else>
                    <img src="images/creditcardNoAmex.png" class="card" />
                </s:else>
            </li>

            <li class="creditcard">
                <s:textfield label="CreditCard.CVVNumber" name="cvv" style="width:30px" maxlength="4" autocomplete="off" />
            </li>

            <li class="expiration-date">
                <label>
                    <s:text name="RegistrationMakePayment.ExpirationDate" />
                </label>

                <s:select
                    id="expMonth"
                    headerKey=""
                    headerValue="- %{getText('ReportCsrActivity.label.Month')} -"
                    listKey="number"
                    listValue="%{getText(i18nKey)}"
                    list="@com.picsauditing.jpa.entities.Month@values()"
                    name="ccexpmonth"
                    theme="simple"
                />

                <s:select
                    id="expYear"
                    headerKey=""
                    headerValue="- %{getText('ReportCsrActivity.label.Year')} -"
                    list="#{14:2014,15:2015,16:2016,17:2017,18:2018,19:2019,20:2020,21:2021,22:2022}"
                    name="ccexpyear"
                    theme="simple"
                />

                <s:textfield id="ccexp" name="ccexp" theme="simple" />
            </li>
        </ul>
        
        <s:include value="/struts/contractors/_registrationContractorAgreement.jsp" />
        
        <div class="actions">
            <s:submit
                id="submit_payment_button"
                method="completeRegistration"
                key="button.SubmitPayment"
                cssClass="btn success"
                disabled="true"
            />

            <a href="https://www.braintreegateway.com/merchants/89hr924yx28jmb8g/verified"
                target="_blank"
                class="brain-tree-badge">

                <img src="https://braintree-badges.s3.amazonaws.com/05.png" border="0" />
            </a>

            <div class="processing">
                <img src="images/loading.gif" />
                <p>
                    <s:text name="RegistrationMakePayment.Processing" />
                </p>
            </div>

            <p class="check-note">
                <s:text name="ContractorPaymentOptions.InvoiceEmail" />
            </p>
        </div>
    </s:form>
</div>