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

		<div id="payment_method_wrapper">
			<s:if test="contractor.country.proforma">
			    <input type="radio" name="payment_method" id="credit_card" checked />
			    <label for="credit_card">Credit Card</label>

			    <input type="radio" name="payment_method" id="pro_forma" />
			    <label for="pro_forma">Pro Forma Email</label>

		    	<s:include value="/struts/contractors/_registrationCreditCard.jsp" />
		    	<s:include value="/struts/contractors/_registrationProForma.jsp" />
			</s:if>

			<s:else>
				<s:include value="/struts/contractors/_registrationCreditCard.jsp" />
			</s:else>
		</div>

	</section>
</div>
