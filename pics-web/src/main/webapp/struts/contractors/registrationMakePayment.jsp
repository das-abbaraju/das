<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="currency"><s:property value="invoice.currency.display"/></s:set>

<s:url action="ContractorPricing" var="contractor_pricing">
    <s:param name="con">
        ${contractor.id}
    </s:param>
</s:url>

<div class="registration-header">
	<section>
		<s:include value="/struts/contractors/registrationStep.jsp">
			<s:param name="step_current" value="3" />
			<s:param name="step_last" value="getLastStepCompleted()" />
		</s:include>
	</section>
</div>

<s:if test="hasActionErrors()">
	<s:actionerror cssClass="action-error alert-message error" />
</s:if>

<div class="make-payment">
	<s:if test="contractor.paymentMethod.check">
		<h1><s:text name="RegistrationMakePayment.Check" /></h1>
		<div class="membership">
			<p class="process-check-info alert-message">
				<s:text name="ContractorRegistrationFinish.FullAccessOnPayment"/>
			</p>
		</div>
	</s:if>
	<s:else>
		<h1><s:text name="RegistrationMakePayment.MembershipInvoice" /></h1>
	</s:else>

	<div class="membership">
		<section>
			<table class="invoice" cellpadding="0" cellspacing="0" border="0">
				<tbody>
					<%-- Membership Fees --%>

					<tr>
						<th class="annual-membership">
							<s:text name="RegistrationMakePayment.AnnualMembership" />
						</th>
						<th class="price">
							<s:text name="RegistrationMakePayment.Price" />
						</th>
					</tr>

					<s:iterator value="invoice.items" status="stat">
						<s:if test="invoiceFee.membership">
							<tr>
								<td>
									<s:property	value="invoiceFee.fee" />
									<a
										href="javascript:;"
										class="help"
										data-title="<s:text	name="%{invoiceFee.feeClass.i18nKey}" />"
										data-content="<s:text name="%{invoiceFee.feeClass.getI18nKey('help')}" />"
									><img src="images/help-icon.png" /></a>
								</td>
								<td class="price">
									<s:property value="amount" /> ${currency}
								</td>
							</tr>
						</s:if>
					</s:iterator>

					<%-- One Time Fees --%>

					<tr>
						<th colspan="2">
							<s:text name="RegistrationMakePayment.OneTimeFees" />
						</th>
					</tr>

					<s:iterator value="invoice.items" status="stat">
						<s:if test="!invoiceFee.membership && !invoiceFee.tax">
							<tr>
								<td>
									<s:property	value="invoiceFee.fee" />
									<a
										href="javascript:;"
										class="help"
										data-title="<s:text	name="%{invoiceFee.feeClass.i18nKey}" />"
										data-content="<s:text name="%{invoiceFee.feeClass.getI18nKey('help')}" />"
									><img src="images/help-icon.png" /></a>

									<%-- remove import fee --%>
									<s:if test="invoiceFee.feeClass == importFee.feeClass">
										<s:form cssClass="data-import-form">
											<s:submit method="removeImportFee" cssClass="btn error" value="Remove" />
										</s:form>
									</s:if>
								</td>
								<td class="price">
									<s:property value="amount" /> ${currency}
								</td>
							</tr>
						</s:if>
					</s:iterator>

					<%-- Import Fee being hard coded to toggle on/off --%>
					<s:if test="contractor.eligibleForImportPQF
									&& (!contractor.fees.get(importFee.feeClass) || contractor.fees.get(importFee.feeClass).newLevel.free)">
						<tr>
							<td>
								<s:property	value="importFee.fee" />
								<a
									href="javascript:;"
									class="help"
									data-title="<s:text	name="%{importFee.feeClass.i18nKey}" />"
									data-content="<s:text name="%{importFee.feeClass.getI18nKey('help')}" />"
								><img src="images/help-icon.png" /></a>

								<%-- add import fee --%>
								<s:form cssClass="data-import-form">
									<s:submit method="addImportFee" cssClass="btn success" value="%{getText('button.Add')}" />
								</s:form>
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
					</s:if>

					<%-- Taxes --%>

					<s:if test="invoice.hasTax()">

						<%-- Subtotal (i.e., pre-tax total) --%>
						<s:set var="subtotal_label"><s:text name="RegistrationMakePayment.Subtotal" /></s:set>
						<s:set var="subtotal_amount" value="invoice.taxlessSubtotal" />
						<s:set var="taxes_label"><s:text name="RegistrationMakePayment.Taxes" /></s:set>

						<tr class="total">
							<td colspan="2">
								<span class="total">${subtotal_label}:</span> ${subtotal_amount} ${currency}
							</td>
						</tr>

						<%-- List of Tax Items --%>

						<tr>
							<th colspan="2">
								${taxes_label}
							</th>
						</tr>

						<s:iterator value="invoice.items" status="stat">
							<s:if test="invoiceFee.isTax()">

								<s:set var="fee_name" value="invoiceFee.fee" />
								<s:set var="fee_amount" value="amount" />

								<tr>
									<td>
										${fee_name}
										<a
											href="javascript:;"
											class="help"
											data-title="<s:text	name="%{invoiceFee.feeClass.i18nKey}" />"
											data-content="<s:text name="%{invoiceFee.feeClass.getI18nKey('help')}" />"
										><img src="images/help-icon.png" /></a>
									</td>
									<td class="price">
										${fee_amount} ${currency}
									</td>
								</tr>
							</s:if>
						</s:iterator>
					</s:if>

					<%-- Grand Total --%>

					<s:set var="total_label"><s:text name="RegistrationMakePayment.Total" /></s:set>
					<s:set var="total_amount"><s:property value="invoice.totalAmount" /></s:set>

					<tr class="total">
						<td colspan="2">
							<span class="total">${total_label}:</span> ${total_amount} ${currency}
						</td>
					</tr>
				</tbody>
	 		</table>

	 		<div class="policy">
                <a href="${contractor_pricing}" target="_blank">
                    <s:text name="ContractorFacilities.ViewPricing" />
                </a>
                <p>
                    <s:text name="ContractorRegistration.title" />:
                    ${contractor.country.isrPhone}
                </p>
                <div id="contractor_agreement">
                    <input id="accept_contractor_agreement" type="checkbox"><s:text name="RegistrationMakePayment.AgreeToTermsAndConditions" /> <s:a href="#" cssClass="contractor-agreement modal-link" data-url="ContractorAgreement.action?id=${contractor.id}"><s:text name="RegistrationMakePayment.ContractorAgreement" /></s:a>
                </div>
	 		</div>

	 		<div class="modal hide fade">
				<div class="modal-header">
					<a href="#" class="close">×</a>
					<h3><s:text name="RegistrationMakePayment.ModalHeading" /></h3>
				</div>
				<div class="modal-body">
					<p><s:text name="RegistrationMakePayment.ModalBody" /></p>
				</div>
				<div class="modal-footer"></div>
			</div>
		</section>
	</div>
	<div class="membership-help">
		<section>
			<h1><s:text name="RegistrationMakePayment.DocuGUARD" /></h1>
			<p><s:text name="RegistrationMakePayment.MembershipHelp" /></p>
		</section>
	</div>

	<div class="separator"></div>

	<section>
		<h1>
			<s:text name="RegistrationMakePayment.BillingInformation" />
		</h1>

		<div id='payment-method-wrapper'>

		<!-- Credit Card Payment Method -->
		<div id='credit-card-payment-method'>
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
							list="#{12:2012,13:2013,14:2014,15:2015,16:2016,17:2017,18:2018,19:2019,20:2020}"
							name="ccexpyear"
							theme="simple"
						/>

						<s:textfield id="ccexp" name="ccexp" theme="simple" />
					</li>

					<li class="actions">
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
					</li>
				</ul>
			</s:form>
		</div>

		<s:if test="contractor.country.proforma">

			<!-- -OR- -->

			<div id='or'>
				- <s:text name="Registration.Payment.Or" /> -
			</div>

			<!-- Pro Forma Invoice Email Button -->
			<s:form action="RegistrationMakePayment.action" method="POST">
				<input type="hidden" name="button" value="email" />
				<input type="hidden" name="invoice.id" value="<s:property value="invoice.id"/>" />

				<s:submit id='pro-forma-button'
					key="Registration.ProForma.Email.Button"
					cssClass="btn success"
                    disabled="true"
				/>
			</s:form>

		</s:if>

	</div>

	</section>
</div>
