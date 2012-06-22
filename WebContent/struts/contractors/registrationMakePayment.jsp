<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

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
				<thead>
					<tr>
						<th class="annual-membership">
							<s:text name="RegistrationMakePayment.AnnualMembership" />
						</th>
						<th class="price">
							<s:text name="RegistrationMakePayment.Price" />
						</th>
					</tr>
				</thead>
				<tfoot>
					<tr>
						<td colspan="2">
							<span class="total"><s:text name="RegistrationMakePayment.Total" />:</span> <s:property value="invoice.currency.symbol"/><s:property value="invoice.totalAmount" /> <s:property value="invoice.currency"/>
						</td>
					</tr>
				</tfoot>
				<tbody>
				
					<%-- Displaying Membership Fees First --%>					
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
									<s:property value="invoice.currency.symbol"/><s:property value="amount" /> <s:property value="invoice.currency"/>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					
					<%-- Displaying Non-Memberships Fees Seperately --%>		
					<tr>
						<th colspan="2">
							<s:text name="RegistrationMakePayment.OneTimeFees" />
						</th>
					</tr>
					
					<%-- one time fees --%>
					<s:iterator value="invoice.items" status="stat">
						<s:if test="!invoiceFee.membership">
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
									<s:property value="invoice.currency.symbol"/><s:property value="amount" /> <s:property value="invoice.currency"/>
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
				</tbody>
	 		</table>
	 		
	 		<div class="policy">
	 			<ul>
		 			<li>
		 				<s:a href="#" cssClass="view-pricing modal-link" data-url="ContractorPricing.action?con=${contractor.id}"><s:text name="RegistrationMakePayment.ViewPricing" /></s:a>
		 			</li>
		 			<li>
		 				<s:a href="#" cssClass="privacy-policy modal-link" data-url="PrivacyPolicy.action"><s:text name="RegistrationMakePayment.PrivacyPolicy" /></s:a>
		 			</li>
		 			<li>
		 				<s:a href="#" cssClass="refund-policy modal-link" data-url="RefundPolicy.action"><s:text name="RegistrationMakePayment.RefundPolicy" /></s:a>
		 			</li>
		 			<li>
		 				<s:a href="#" cssClass="contractor-agreement modal-link" data-url="ContractorAgreement.action"><s:text name="RegistrationMakePayment.ContractorAgreement" /></s:a>
		 			</li>
		 		</ul>
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
	
	<s:if test="contractor.paymentMethod.creditCard">
		<s:form action="https://secure.braintreepaymentgateway.com/api/transact.php" cssClass="make-payment-form" theme="pics">
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
			
			<section>
				<h1><s:text name="RegistrationMakePayment.BillingInformation" /></h1>
				
				<ul>
					<li>
						<s:select label="CreditCard.Type" list="creditCardTypes" name="ccName" />
					</li>
					<li class="creditcard">
						<s:textfield label="CreditCard.Number" name="ccnumber" />
						
						<img src="images/creditcard.png" class="card" />
					</li>
					<li class="expiration-date">
						<label><s:text name="RegistrationMakePayment.ExpirationDate" /></label>
						
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
						
						<s:textfield id="ccexp" name="ccexp" cssStyle="display: none" theme="simple" />
					</li>
					<li class="actions">
						<s:submit 
							method="completeRegistration" 
							key="button.SubmitPayment" 
							cssClass="btn success" 
						/>
                        
                        <a href="https://www.braintreegateway.com/merchants/89hr924yx28jmb8g/verified" target="_blank" class="brain-tree-badge">
                            <img src="https://braintree-badges.s3.amazonaws.com/05.png" border="0" />
                        </a>
						
						<div class="processing">
							<img src="images/loading.gif" />
							<p><s:text name="RegistrationMakePayment.Processing" /></p> 
						</div>
						
						<p class="check-note">
							<s:text name="ContractorPaymentOptions.InvoiceEmail" />
						</p>
					</li>
				</ul>
			</section>
		</s:form>
		
	</s:if>
	<s:if test="contractor.inEuroZone">
	<ul>
		<a class="email"
			href="RegistrationMakePayment.action?invoice.id=<s:property value="invoice.id"/>&button=email">
			<s:text name="button.EmailProformaInvoice" />
		</a>
	</ul>
	</s:if>
</div>