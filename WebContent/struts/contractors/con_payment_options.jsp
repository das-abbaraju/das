<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

		<title><s:text name="ContractorPaymentOptions.PaymentMethod"><s:param value="%{contractor.name}" /></s:text></title>
		
		<meta name="help" content="User_Manual_for_Contractors">
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/invoice.css?v=<s:property value="version"/>" />
		
		<script type="text/javascript">
			$(function() {
				$('.cluetip').cluetip({
					closeText: "<img src='images/cross.png' width='16' height='16'>",
					arrows: true,
					cluetipClass: 'jtip',
					local: true,
					clickThrough: false
				});
			});
			
			function validate() {
				return updateExpDate();
			}
			
			function updateExpDate() {
				$('#ccexpError').hide();
				
				if (!$('#expMonth').blank() && !$('#expYear').blank()) {
					$('#ccexp').val($('#expMonth').val() + $('#expYear').val());
					return true;
				}
				
				$('#ccexpError').text(translate('JS.ContractorPaymentOptions.CCExpError')).show();
				
				return false;
			}
		</script>

    <div id="${actionName}_${methodName}_page" class="${actionName}-page page">
       
		<s:include value="conHeader.jsp" />
		<s:include value="../actionMessages.jsp" />
		
		<%-- All criteria are satisfied after contractor has entered CC info --%>
		<s:if test="contractor.operators.size == 0">
			<div class="alert">
				<s:text name="ContractorPaymentOptions.NoFacilitiesSelected">
					<s:param value="%{contractor.id}" />
				</s:text>
			</div>
		</s:if>
		<s:elseif test="contractor.hasFreeMembership">
			<div class="alert">
				<s:text name="ContractorPaymentOptions.HasFreeMembership" />
			</div>
		</s:elseif>
		<s:elseif test="contractor.status.active && !contractor.paymentMethodStatusValid && contractor.mustPayB">
			<div class="info">
				<s:text name="ContractorPaymentOptions.EnterCreditCard">
					<s:param value="getText(\"PicsBillingPhone\", locale)" />
				</s:text>
			</div>
		</s:elseif>
		
		<s:if test="!contractor.paymentMethod.creditCard && contractor.mustPayB">
			<div class="info">
				<s:text name="ContractorPaymentOptions.InvoiceEmail" />
			</div>
		</s:if>
		
		<s:if test="contractor.balance > 0">
			<div class="alert">
				<s:iterator value="contractor.invoices">
					<s:if test="status.unpaid">
						<s:text name="ContractorPaymentOptions.UnpaidInvoice">
							<s:param value="%{id}" />
							<s:param value="%{contractor.country.currency.symbol}" />
							<s:param value="%{balance}" />
							<s:param value="%{dueDate}" />
						</s:text>
					</s:if>
				</s:iterator>
			</div>
		</s:if>
		
		<s:form id="save" method="POST">
			<s:hidden name="id" />
			
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="ContractorPaymentOptions.MembershipDetails" />
				</h2>
				
				<div id="name_status"></div>
				
				<ol>
					<s:if test="contractor.paymentMethodStatusValid && contractor.paymentMethod.creditCard && contractor.mustPayB">
						<li>
							<div class="info">
								<s:text name="ContractorPaymentOptions.CCOnFile">
									<s:param value="getText(\"PicsPhone\", locale)" />
								</s:text>
							</div>
						</li>
					</s:if>
					
					<s:if test="contractor.newMembershipAmount > 0">
						<s:if test="contractor.paymentMethod.creditCard && contractor.newMembershipAmount < 500" >
							<li>
								<i>
									<s:text name="ContractorPaymentOptions.CCRequired">
										<s:param value="%{contractor.country.currency.symbol}" />
									</s:text>
								</i>
							</li>
						</s:if>
					
						<s:if test="contractor.status.activeDemo">
							<li>
								<label><s:text name="ContractorPaymentOptions.NextBillingDate"/></label>
								<s:date name="contractor.paymentExpires" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
							</li>
							<li>
								<label>
									<s:text name="ContractorPaymentOptions.NextBillingAmount"/>
									
									<s:if test="contractor.accountLevel.full">
										(<a href="ContractorPricing.action?con=${contractor.id}" rel="facebox" class="ext"><s:text name="ContractorFacilities.ContractorFacilities.ViewPricing"/></a>)
									</s:if>
								</label>
								
								<table>
									<s:iterator value="contractor.fees.keySet()" var="feeClass">
										<s:if test="!contractor.fees.get(#feeClass).newLevel.free && #feeClass.membership">
											<tr>
												<td colspan="2">
													<s:property value="contractor.fees.get(#feeClass).newLevel.fee" />&nbsp;
												</td>
												<td class="right">
													<s:property value="contractor.country.currency.symbol" />
													<s:property value="contractor.fees.get(#feeClass).newAmount" />
												</td>
												<td>
													&nbsp;<s:property value="contractor.currency"/>
												</td>
											</tr>
										</s:if>
									</s:iterator>
								</table>

								<s:if test="contractor.country.currency.CAD">
									<li>
                                        <s:set var="taxFeeKey" value="%{canadianTaxFeeMsgKey}" />
										<label><s:text name="%{taxFeeKey}"/></label>
										<s:property value="contractor.country.currency.symbol" />
										<s:property value="canadianTaxFee.amount"/>
										<s:property value="contractor.country.currency" />
									</li>
									<li>
										<label><s:text name="ContractorPaymentOptions.Total"/></label>
										<s:property value="contractor.country.currency.symbol" />
										<s:property value="contractor.newMembershipAmount+canadianTaxFee.amount"/>
										<s:property value="contractor.country.currency" />
									</li>
								</s:if>
                                <s:elseif test="contractor.country.currency.gbp">
                                    <li>
                                        <label><s:text name="ContractorPaymentOptions.VAT"/></label>
                                        <s:property value="contractor.country.currency.symbol" />
                                        <s:property value="vatFee.amount"/>
                                        <s:property value="contractor.country.currency" />
                                    </li>
                                    <li>
                                        <label><s:text name="ContractorPaymentOptions.Total"/></label>
                                        <s:property value="contractor.country.currency.symbol" />
                                        <s:property value="contractor.newMembershipAmount+vatFee.amount"/>
                                        <s:property value="contractor.country.currency" />
                                    </li>
                                </s:elseif>
								<s:else>
									<li>
										<label><s:text name="ContractorPaymentOptions.Total"/></label>
										<s:property value="contractor.country.currency.symbol" />
										<s:property value="contractor.newMembershipAmount"/>
										<s:property value="contractor.country.currency" />
									</li>
								</s:else>
							</li>
						</s:if>
						<s:else>
							<s:if test="contractor.balance > 0">
								<li>
									<s:iterator value="contractor.invoices" id="i">
										<s:if test="status.unpaid">
											<s:include value="con_invoice_embed.jsp"/>
											
											<br clear="all"/>
										</s:if>
									</s:iterator>
								</li>
							</s:if>
							<s:else>
								<li>
									<label>
										<s:text name="ContractorPaymentOptions.AnnualMembership" />
		
										<s:if test="contractor.accountLevel.full">
											(<a href="ContractorPricing.action?con=${contractor.id}" rel="facebox" class="ext"><s:text name="ContractorFacilities.ContractorFacilities.ViewPricing"/></a>)
										</s:if>
									</label>
									
									<table>
										<s:iterator value="contractor.fees.keySet()" var="feeClass">
											<s:if test="!contractor.fees.get(#feeClass).newLevel.free && #feeClass.membership">
												<tr>
													<td colspan="2">
														<s:property value="contractor.fees.get(#feeClass).newLevel.fee" />&nbsp;
													</td>
													<td class="right">
														<s:property value="contractor.country.currency.symbol" />
														<s:property value="contractor.fees.get(#feeClass).newAmount" />
													</td>
													<td>
														&nbsp;<s:property value="contractor.currency"/>
													</td>
												</tr>
											</s:if>
										</s:iterator>
									</table>
								</li>
								
								<s:if test="contractor.accountLevel.full">
									<li>
										<label><s:property value="activationFee.fee"/></label>
										<s:property value="contractor.country.currency.symbol"/>
										<s:property value="activationFee.amount"/>
										<s:property value="contractor.country.currency" />
									</li>
								</s:if>
								
								<s:if test="importFee.amount > 0">
									<li>
										<label><s:property value="importFee.fee"/></label>
										<s:property value="contractor.country.currency.symbol"/>
										<s:property value="importFee.amount"/>
										<s:property value="contractor.country.currency" />
									</li>
								</s:if>
								
								<s:if test="contractor.country.currency.CAD">
									<li>
                                        <s:set var="taxFeeKey" value="%{canadianTaxFeeMsgKey}" />
										<label><s:text name="%{taxFeeKey}"/></label>
										<s:property value="contractor.country.currency.symbol"/>
										<s:property value="canadianTaxFee.amount"/>
										<s:property value="contractor.country.currency" />
									</li>
									<li>
										<label><s:text name="ContractorPaymentOptions.Total"/></label>
										<s:property value="contractor.country.currency.symbol"/>
										<s:property value="activationFee.amount+contractor.newMembershipAmount+canadianTaxFee.amount+importFee.amount"/>
										<s:property value="contractor.country.currency" />
									</li>
								</s:if>
                                <s:elseif test="contractor.country.currency.gbp || contractor.country.currency.eur">
                                    <li>
                                        <label><s:text name="ContractorPaymentOptions.VAT"/></label>
                                        <s:property value="contractor.country.currency.symbol" />
                                        <s:property value="vatFee.amount"/>
                                        <s:property value="contractor.country.currency" />
                                    </li>
                                    <li>
                                        <label><s:text name="ContractorPaymentOptions.Total"/></label>
                                        <s:property value="contractor.country.currency.symbol" />
                                        <s:property value="contractor.newMembershipAmount+vatFee.amount"/>
                                        <s:property value="contractor.country.currency" />
                                    </li>
                                </s:elseif>
								<s:else>
									<li>
										<label><s:text name="ContractorPaymentOptions.Total"/></label>
										<s:property value="contractor.country.currency.symbol"/>
										<s:property value="activationFee.amount+contractor.newMembershipAmount+importFee.amount"/>
										<s:property value="contractor.country.currency" />
									</li>
								</s:else>
							</s:else>
						</s:else>
					</s:if>
					<s:else>
						<li>
							<label><s:text name="global.Status" /></label>
							<s:property value="ContractorPaymentOptions.NoPaymentRequired"/>
						</li>
					</s:else>
					
					<li>
						<label><s:text name="BillingDetail.Info.PaymentMethod" /></label>
						<s:text name="%{contractor.paymentMethod.i18nKey}" />
					</li>
					
					<s:if test="contractor.status.active || permissions.admin">
						<li>
							<label><s:text name="ContractorPaymentOptions.ContractorAgreement" /></label>
							<s:checkbox name="contractor.agreed" disabled="true" />
						
							<s:if test="contractor.agreementDate != null">
								<s:text name="ContractorPaymentOptions.AgreementDate" >
									<s:param value="%{contractor.agreementDate}" />
									<s:param value="%{contractor.agreedBy.name}" />
								</s:text>
							</s:if>
						</li>
						
						<s:if test="contractor.agreementDate == null || (contractor.agreementDate != null && !contractor.agreementInEffect)">
							<li>
								<span style="color:grey;">
									<s:if test="!permissions.admin && (
										permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorBilling) || 
										permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorAdmin) || 
										permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorSafety)
									) ">
										<s:text name="ContractorPaymentOptions.AcceptAgreement" >
											<s:param value="%{contractor.id}" />
										</s:text>
										
										<s:submit cssClass="picsbutton" method="acceptContractorAgreement" value="%{getText('button.IAgree')}" />
									</s:if>
									<s:else>
										<s:text name="ContractorPaymentOptions.AdminLoginToAcceptAgreement" >
											<s:param value="%{contractor.id}" />
										</s:text>
									</s:else>
								</span>
							</li>
						</s:if>
					</s:if>
					
					<li>
						<a href="PrivacyPolicy.action" rel="facebox" class="ext"><s:text name="Footer.Privacy" /></a> | 
						<a href="RefundPolicy.action" rel="facebox" class="ext"><s:text name="ContractorPaymentOptions.RefundPolicy" /></a> |
						<a href="ContractorAgreement.action?id=${contractor.id}" rel="facebox" class="ext"><s:text name="ContractorPaymentOptions.ContractorAgreement" /></a>
					</li>
					
					<s:if test="contractor.newMembershipAmount > 500 || permissions.admin">
						<li>
							<div>
								<s:if test="contractor.paymentMethod.creditCard">
									<s:submit cssClass="picsbutton" method="changePaymentToCheck" value="%{getText('button.ChangePaymentToCheck')}" />
								</s:if>
								<s:else>
									<s:submit cssClass="picsbutton" method="changePaymentToCC" value="%{getText('button.ChangePaymentToCC')}" />
								</s:else>
							</div>
							
						</li>
					</s:if>
					
					<pics:permission perm="Billing">
						<s:if test="contractor.ccOnFile">	
							<li>
								<div>
									<s:submit cssClass="picsbutton negative" method="markCCInvalid" value="%{getText('button.MarkCCInvalid')}" />
								</div>
							</li>
						</s:if>
						<s:elseif test="!contractor.ccOnFile && contractor.ccExpiration != null">
							<li>
								<div>
									<s:submit cssClass="picsbutton positive" method="markCCValid" value="%{getText('button.MarkCCValid')}" />
								</div>
							</li>
						</s:elseif>
					</pics:permission>
				</ol>
			</fieldset>
		</s:form>
		
		<s:if test="contractor.paymentMethod.creditCard && !braintreeCommunicationError">
			<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php" onsubmit="return validate();">
				<input type="hidden" name="redirect" value="<s:property value="requestString"/>?id=<s:property value="id"/>"/>
				
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
			
				<s:if test="cc != null">
					<fieldset class="form">
						<h2 class="formLegend"><s:text name="global.CreditCard" /></h2>
						
						<ol>
							<li>
								<label><s:text name="CreditCard.Type" /></label>
								<s:property value="cc.cardType"/>
								
								<s:if test="!contractor.ccOnFile && contractor.ccExpiration != null">
									<span style="color:red;" >&nbsp;&nbsp;( <s:text name="ContractorPaymentOptions.CCInvalid" /> )</span>
								</s:if>
							</li>
							<li>
								<label><s:text name="CreditCard.Number" /></label>
								<s:property value="cc.cardNumber"/>
							</li>
                            <li>
                                <label><s:text name="CreditCard.CVVNumber" /></label>
                                <s:property value="cc.CVVNumber" />
                            </li>
							<li>
								<label><s:text name="CreditCard.Expiration" /></label>
								<s:property value="cc.FormattedExpirationDateString"/>
							</li>
							<li>
								<a href="?id=<s:property value="id"/>&button=delete" class="remove"><s:text name="button.RemoveCreditCard" /></a>
							</li>
						</ol>
					</fieldset>
				</s:if>
			
				<fieldset class="form">
					<h2 class="formLegend">
						<span>
							<s:if test="cc == null">
								<s:text name="button.AddCreditCard" />
							</s:if>
							<s:else>
								<s:text name="button.ReplaceCreditCard" />
							</s:else>
						</span>
					</h2>
					
					<ol>
						<li>
							<label><s:text name="CreditCard.Type" /></label>
							<s:radio
								list="creditCardTypes"
								name="ccName"
								theme="pics"
								cssClass="inline"
							/>
						</li>
						<li class="creditcard">
							<s:textfield label="CreditCard.Number" name="ccnumber" theme="pics" />						
						</li>
						<li class="creditcard">
							<s:textfield label="CreditCard.CVVNumber" name="cvv" maxlength="4" theme="pics" />
						</li>
						<li>
							<label><s:text name="CreditCard.Expiration" /></label>
							<s:select id="expMonth" listKey="number" listValue="%{getText(i18nKey)}" list="@com.picsauditing.jpa.entities.Month@values()" headerKey="" headerValue="- %{getText('ReportCsrActivity.label.Month')} -" />
							<s:select id="expYear" list="#{11:2011,12:2012,13:2013,14:2014,15:2015,16:2016,17:2017,18:2018,19:2019,20:2020}" headerKey="" headerValue="- %{getText('ReportCsrActivity.label.Year')} -"></s:select>
							<s:textfield id="ccexp" name="ccexp" cssStyle="display: none" />
							<span id="ccexpError" class="Red" style="display:none"> </span>
						</li>
					</ol>
				</fieldset>
				
				<fieldset class="form submit">
					<input type="submit" class="picsbutton positive" name="commit" value="<s:if test="cc == null"><s:text name="button.AddCreditCard" /></s:if><s:else><s:text name="button.ReplaceCreditCard" /></s:else>" />
					<br clear="all">
				</fieldset>
			</form>
		</s:if>
    </div>        
