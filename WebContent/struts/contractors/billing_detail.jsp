<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:property value="contractor.name" /> <s:text name="BillingDetail.title" /></title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
		
		<s:include value="../jquery.jsp"/>
	</head>
	<body>
		<s:include value="conHeader.jsp"></s:include>
		
		<s:if test="contractor.qbListID.startsWith('NOLOAD')">
			<div class="alert">
				<s:text name="BillingDetail.ContractorNoSyncQuickbooks" />
			</div>
		</s:if>
		
		<s:if test="contractor.accountLevel.bidOnly">
			<div class="alert">
				<s:text name="ContractorView.BidOnlyUpgradeAlert" />
			</div>
		</s:if>
		
		<table width="100%">
			<tr>
				<td style="vertical-align: top; width: 48%;">
					<fieldset class="form">
						<h2 class="formLegend"><s:text name="BillingDetail.Info.heading" /></h2>
						
						<ol>
							<li>
								<label><s:text name="global.Active" />:</label>
								<s:property value="contractor.status"/>
							</li>
							<li>
								<label title="The Date the Account was Created."><s:text name="BillingDetail.Info.RegistrationDate" />:</label>
								<s:date name="contractor.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
							</li>
							<li>
								<label title="The Date the Activation/Reactivation Fee was Paid."><s:text name="BillingDetail.Info.ActivationDate" />:</label>
								<s:date name="contractor.membershipDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
							</li>
							<li>
								<label><s:text name="BillingDetail.Info.WillBeRenewed" />:</label>
								
								<s:if test="contractor.renew">
									<s:text name="YesNo.Yes" />
								</s:if>
								<s:else>
									<s:text name="YesNo.No" />
								</s:else>
							</li>
							<li>
								<label><s:text name="BillingDetail.Info.RenewalDate" />:</label>
								<s:date name="contractor.paymentExpires" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
							</li>
							<li>
								<label><s:text name="BillingDetail.Info.PaymentMethod" />:</label>
								<s:property value="contractor.paymentMethod.description" />
							</li>
							<li>
								<label><s:text name="BillingDetail.Info.CreditCardOnFile" />?</label>
								
								<s:if test="contractor.ccOnFile">
									<s:text name="YesNo.Yes" />
								</s:if>
								<s:elseif test="!contractor.ccOnFile && contractor.ccExpiration != null">
									<span style="color:red;" ><s:text name="Filters.status.Invalid" /></span>
								</s:elseif>
								<s:else>
									<s:text name="YesNo.No" />
								</s:else>
							</li>
						</ol>
					</fieldset>
		
					<fieldset class="form bottom">
						<h2 class="formLegend"><s:text name="BillingDetail.Facilities.heading" /></h2>
						
						<ol>
							<li>
								<label><s:text name="BillingDetail.Facilities.RequestedBy" />:</label>
								<s:property value="requestedBy.name" />
							</li>
							
							<s:if test="!contractor.materialSupplierOnly">
								<li>
									<label><s:text name="global.SafetyRisk" />:</label>
									<s:text name="%{contractor.safetyRisk.i18nKey}" />
								</li>
							</s:if>
							
							<s:if test="contractor.materialSupplier && contractor.productRisk != null">
								<li>
									<label><s:text name="global.ProductRisk" />:</label>
									<s:text name="%{contractor.productRisk.i18nKey}" />
								</li>
							</s:if>
							
							<li>
								<label><s:text name="BillingDetail.Facilities.Facilities" />:</label>
                                <s:if test="nonCorporatePayingOperators.size() > 0">
                                    <div>
                                    <s:text name="BillingDetail.PayingOperators">
                                        <s:param><s:property value="nonCorporatePayingOperators.size()" /></s:param>
                                    </s:text>
    								<ul style="position: relative; left: 1em; list-style-type: disc;">
    									<s:iterator value="nonCorporatePayingOperators">
											<li style="padding: 5px 0 5px 0;">
												<s:if test="permissions.admin">
													<a href="OperatorConfiguration.action?id=<s:property value="operatorAccount.id" />">
														<s:property value="operatorAccount.name" />
													</a>
												</s:if>
												<s:else>
													<a href="ContractorFlag.action?opID=<s:property value="operatorAccount.id" />">
														<s:property value="operatorAccount.name" />
													</a>
												</s:else>
											</li>
    									</s:iterator>
    								</ul>
                                    </div>
                                </s:if>
                                <s:if test="nonCorporateFreeOperators.size() > 0">
                                    <div style="float: left; padding-top: 5px;">
                                    <s:text name="BillingDetail.NonPayingOperators">
                                        <s:param><s:property value="nonCorporateFreeOperators.size()" /></s:param>
                                    </s:text>
                                    <ul style="position: relative; left: 1em; list-style-type: disc;">
                                        <s:iterator value="nonCorporateFreeOperators">
                                            <li style="padding: 5px 0 5px 0;">
                                                <s:if test="permissions.admin">
                                                    <a href="OperatorConfiguration.action?id=<s:property value="operatorAccount.id" />">
                                                        <s:property value="operatorAccount.name" />
                                                    </a>
                                                </s:if>
                                                <s:else>
                                                    <a href="ContractorFlag.action?opID=<s:property value="operatorAccount.id" />">
                                                        <s:property value="operatorAccount.name" />
                                                    </a>
                                                </s:else>
                                            </li>
                                        </s:iterator>
                                    </ul>
                                    </div>
                                </s:if>
							</li>
							<li>
								<label><s:text name="BillingDetail.Facilities.ViewOperators" />:</label>
								<a href="ContractorFacilities.action?id=<s:property value="id" />"><s:text name="BillingDetail.Facilities.Facilities" /></a>
							</li>
							<li>
								<label><s:text name="BillingDetail.Facilities.LastUpgradeDate" />:</label>
								<s:date name="contractor.lastUpgradeDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
							</li>
						</ol>
					</fieldset>
				</td>
				<td style="width: 5px;"></td>
				<td style="vertical-align: top; width: 48%;">
					<fieldset class="form">
						<h2 class="formLegend"><s:text name="BillingDetail.Invoicing.heading" /></h2>
						
						<ol>
							<li>
								<label><s:text name="BillingDetail.Invoicing.CurrentBalance" />:</label>
								<s:property value="contractor.balance" />
								<s:property value="contractor.country.currency"/>
								
								<s:if test="contractor.balance > 0">
									<pics:permission perm="Billing" type="Edit">
										<a href="PaymentDetail.action?id=<s:property value="id" />" class="add"><s:text name="BillingDetail.MakePayment" /></a>
									</pics:permission>
								</s:if>
							</li>
							<li>
								<label><s:text name="BillingDetail.Invoicing.BillingStatus" />:</label>
								<s:property value="contractor.billingStatus" />
							</li>
							<li>
								<label><s:text name="BillingDetail.Invoicing.MustPay" />:</label>
								<s:property value="contractor.mustPay" />
							</li>
							<li>
								<label><s:text name="BillingDetail.Invoicing.CurrentLevel" />:</label>
								
								<table>
									<s:iterator value="contractor.fees.keySet()" var="feeClass">
										<s:if test="!contractor.fees.get(#feeClass).currentLevel.free && #feeClass.membership">
											<tr>
												<td colspan="2">
													<s:property value="contractor.fees.get(#feeClass).currentLevel.fee" />:&nbsp;
												</td>
												<td class="right">
													<s:property value="contractor.country.currency.symbol" />
													<s:property value="contractor.fees.get(#feeClass).currentAmount" />
												</td>
												<td>
													&nbsp;<s:property value="contractor.currency"/>
												</td>
											</tr>
										</s:if>
									</s:iterator>
								</table>
							</li>
							
							<s:if test="contractor.hasUpgrade">
								<li>
									<label><s:text name="BillingDetail.NewLevel" />:</label>
									
									<table>
										<s:iterator value="contractor.fees.keySet()" var="feeClass">
											<s:if test="!contractor.fees.get(#feeClass).newLevel.free && #feeClass.membership">
												<tr>
													<td colspan="2">
														<s:property value="contractor.fees.get(#feeClass).newLevel.fee" />:&nbsp;
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
							</s:if>
						</ol>
					</fieldset>
			
					<s:if test="permissions.admin">
						<fieldset class="form">
							<h2 class="formLegend"><s:text name="BillingDetail.CreateInvoice" /></h2>
							
							<s:form id="save" method="POST" enctype="multipart/form-data">
								<s:hidden name="id" />
								
								<ol>
									<s:iterator value="invoiceItems">
										<s:if test="invoiceFee != null">
											<li>
												<label><s:property value="invoiceFee.fee" />:</label>
												<s:property value="amount" />
												<s:property value="contractor.country.currency"/>
											</li>
										</s:if>
										<s:else>
											<li>
												<label><s:property value="description" />:</label>
												<s:property value="amount" />
												<s:property value="contractor.country.currency"/>
											</li>
										</s:else>
									</s:iterator>
									
									<li>
										<label><s:text name="BillingDetail.Total" />:</label>
										<s:property value="invoiceTotal" />
										<s:property value="contractor.country.currency"/>
									</li>
									
									<pics:permission perm="Billing">
										<li>
											<div>
												<input type="submit" class="picsbutton positive" name="button" value="<s:text name="global.Create" />" />
											</div>
										</li>
									</pics:permission>
								</ol>
							</s:form>
							
							<s:if test="contractor.billingStatus == 'Current' && !contractor.status.activeDemo">
								<s:form>
									<s:hidden name="id" />
									<div><input type="submit" class="picsbutton positive" name="button" value="<s:text name="global.Activate"/>" /></div>
								</s:form>
							</s:if>
						</fieldset>
					</s:if>
					
					<fieldset class="form bottom">
						<h2 class="formLegend"><s:text name="BillingDetail.TransactionHistory.heading" /></h2>
						
						<ol>
							<li>
								<table class="report">
									<thead>
										<tr>
											<th><s:text name="BillingDetail.Transaction" /></th>
											<th>#</th>
											<th><s:text name="global.Date" /></th>
											<th><s:text name="global.Amount" /></th>
											<th><s:text name="BillingDetail.Outstanding" /></th>
											
											<s:if test="permissions.admin">
												<th><s:text name="global.Status" /></th>
											</s:if>	
										</tr>
									</thead>
									<tbody>
										<s:iterator value="transactions">
											<s:if test="!(permissions.contractor && status.void)">
												<s:set name="url" value="" />
												
												<s:if test="class.simpleName == 'Invoice'">
													<s:set name="url" value="'InvoiceDetail.action?invoice.id='+id" />
												</s:if>
												<s:elseif test="class.simpleName == 'Payment'">
													<pics:permission perm="Billing">
														<s:set name="url" value="'PaymentDetail.action?payment.id='+id" />
													</pics:permission>
												</s:elseif>
												
												<tr>
													<td>
														<s:property value="class.simpleName" />
													</td>
													<td class="right">
														<s:if test="#url.length() > 0">
															<a href="<s:property value="#url" />"><s:property value="id" /></a>
														</s:if>
														<s:else>
															<s:property value="id" />
														</s:else>
													</td>
													<td class="right">
														<s:text name="short_dates">
															<s:param value="%{creationDate}" />
														</s:text>
													</td>
													<td class="right">
														<s:property value="totalAmount" />
														<s:property value="currency"/>
													</td>
													<td class="right">
														<s:if test="class.simpleName.equals('Payment') && status.toString() == 'Unpaid' && balance > 0">
															-
														</s:if>
														
														<s:property value="balance" />
														<s:property value="contractor.country.currency"/>
													</td>
													
													<s:if test="permissions.admin">
														<td>
															<s:property value="status"/>
														</td>
													</s:if>
												</tr>
											</s:if>
										</s:iterator>
									</tbody>
								</table>
							</li>
						</ol>
					</fieldset>
				</td>
			</tr>
		</table>
		 
		<div id="notesList">
			<s:include value="../notes/account_notes_embed.jsp"></s:include>
		</div>
	</body>
</html>