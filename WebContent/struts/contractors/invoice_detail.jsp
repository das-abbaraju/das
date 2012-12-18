<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
	<head>
		<title>
			<s:property value="contractor.name" /> - Invoice <s:property value="invoice.id" />
		</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="all" href="css/invoice.css?v=<s:property value="version"/>" />
		
		<style type="text/css" media="print">
			h1 {
				display: none;
			}
			
			input[type=submit] {
				display: none;
			}
		</style>
		
		<script type="text/javascript">
			var ccNumber = '${ccNumber}';
			
			$('a.save').live('click', function(event) {
				event.preventDefault();
				
				$('form#save').submit();
			});
			
			$('a.void').live('click', function(event) {
				return confirm(translate('JS.InvoiceDetail.ConfirmVoid'));
			});
			
			$('a.pay.ccValid').live('click', function(event) {
			    event.preventDefault();
				
			    var element = $(this);
			    var href = element.attr('href');
			    
				if (confirm(translate('JS.InvoiceDetail.ConfirmCharge', [ccNumber]))) {
				    element.css({
				        backgroundImage: 'url(images/spinner.gif)'
				    });
				    
				    window.location = href;
				}
			});
		</script>
	</head>
	
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<s:if test="!permissions.contractor || contractor.status.activeDemo">
			<s:include value="conHeader.jsp"></s:include>
		</s:if>
		
		<s:if test="invoice.status.void">
			<div class="alert" class="noprint"><s:text name="InvoiceDetail.InvoiceCanceled"></s:text></div>
		</s:if>
		<s:elseif test="invoice.status.paid">
			<div class="info" class="noprint"><s:text name="InvoiceDetail.InvoicePaid"></s:text></div>
		</s:elseif>
		<s:elseif test="invoice.overdue && contractor.status.active">
			<div class="alert" class="noprint"><s:text name="InvoiceDetail.InvoiceOverdue"></s:text></div>
		</s:elseif>
		<s:if test="invoice.status.unpaid && !contractor.paymentMethodStatusValid && contractor.mustPayB">
			<div class="alert" class="noprint"><s:text name="InvoiceDetail.UpdatePaymentOption"><s:param><s:property value="contractor.id"/></s:param></s:text></div>
		</s:if>
		
		<s:if test="permissions.admin">
			<s:if test="invoice.status.unpaid && invoice.totalAmount == 0">
				<div class="alert" class="noprint">Please post a note after you have modified the invoice!</div>
			</s:if>
			<s:if test="invoice.qbSync">
				<div class="alert" class="noprint"><s:text name="InvoiceDetail.WaitingSync"></s:text></div>
			</s:if>		
		</s:if>
		
		<s:form id="save" name="save" method="POST">
			<s:hidden name="id"></s:hidden>
			<s:hidden name="invoice.id"></s:hidden>
			<s:hidden name="button" value="save"></s:hidden>
		
			<table width="100%">
				<tr>
					<td>
						<table width="100%">
							<tr>
								<td width="100" colspan="2">
									<img src="images/logo_sm.png" alt="image" width="100" height="31" /><br/>
                                    <s:if test="invoice.currency.CAD">
                                        <s:text name="global.PICSCanadaMailingAddress"/>
                                    </s:if>
                                    <s:elseif test="invoice.currency.GBP || invoice.currency.EUR">
                                        <s:text name="global.PICSUnitedKingdomMailingAddress"/>
                                    </s:elseif>
                                    <s:else>
                                        <s:text name="global.PICSUnitedStatesMailingAddress"/>
                                    </s:else>
								</td>
								<td width="400">   
									<table width="100%" border="0" cellspacing="0" cellpadding="4" class="allborder">
										<tr>
											<th>
												<s:text name="InvoiceDetail.Date" />
											</th>
											<th class="big">
												<nobr>
													<s:text name="InvoiceDetail.InvoiceNumber" />
												</nobr>
											</th>
										</tr>
										<tr>
											<td class="center">
												<nobr>
													<s:date name="invoice.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
													<s:set name="o" value="invoice" />
													<s:include value="../who.jsp" />
												</nobr>
											</td>
											<td class="center">
												<s:property value="invoice.id" />
											</td>
										</tr>
									</table>
									<s:set name="urlBase" value="InvoiceDetail.action?invoice.id={%invoice.id}" />
									<s:property value="#urlBase"/>
									<div id="toolbox" class="noprint">
										<ul>
											<pics:permission perm="Billing" type="Edit">
												<s:if test="edit">
													<li>
														<a class="save" href="#">
															<s:text name="button.Save" />
														</a>
													</li>
													<li>
														<a class="exit" href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>">
															<s:text name="button.Return" />
														</a>
													</li>
												</s:if>
												<s:else>
													<s:if test="invoice.status.unpaid">
														<li>
															<a class="pay" href="PaymentDetail.action?id=<s:property value="id"/>&amountApplyMap[<s:property value="invoice.id"/>]=<s:property value="invoice.balance"/>">
																<s:text name="button.Pay" />
															</a>
														</li>
													</s:if>
													<li>
														<a class="edit" href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&edit=true">
															<s:text name="button.Edit" />
														</a>
													</li>
													<pics:permission perm="Billing" type="Delete">
														<li>
															<a class="void" href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&button=cancel">
																<s:text name="button.Void" />
															</a>
														</li>
													</pics:permission>
													<pics:permission perm="InvoiceEdit">
														<li>
															<a class="system_edit" href="ConInvoiceMaintain.action?id=<s:property value="id"/>&invoiceId=<s:property value="invoice.id"/>">
																<s:text name="button.SysEdit" />
															</a>
														</li>
													</pics:permission>
												</s:else>
											</pics:permission>
											<s:if test="!edit">
												<s:if test="permissions.contractor && invoice.status.unpaid && contractor.ccValid">
													<li>
														<a class="pay ccValid" href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&button=pay">
															<s:text name="button.Pay" />
														</a>
													</li>
												</s:if>
												<li>
													<a class="email" href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&button=email">
														<s:text name="button.Email" />
													</a>
												</li>
												<li>
													<a class="print" href="javascript: window.print();">
														<s:text name="button.Print" />
													</a>
												</li>
											</s:if>
										</ul>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top: 15px;">
						<table width="100%" class="allborder">
							<tr>
								<th>
									<s:text name="InvoiceDetail.BillTo" />
								</th>
								<th width="16%">
									<s:text name="InvoiceDetail.PoNumber" />
								</th>
								<th width="16%">
									<s:text name="InvoiceDetail.DueDate" />
								</th>
							</tr>
							<tr>
								<td>
									<s:property value="contractor.name" />
									<br />
									c/o <s:property value="billingUser.name" />
									<br />
									<s:if test="contractor.billingAddress.length() > 0">
										<s:property value="contractor.billingAddress" />
										<br />
										<s:property value="contractor.billingCity" />,
										<s:property value="contractor.billingCountrySubdivision.isoCode" />
										<s:property	value="contractor.billingZip" />
									</s:if>
									<s:else>
										<s:property value="contractor.address" />
										<br />
										<s:property value="contractor.city" />,
										<s:property value="contractor.countrySubdivision.isoCode" />
										<s:property	value="contractor.zip" />
									</s:else>
                                    <s:if test="%{@com.picsauditing.util.Strings@isNotEmpty(contractor.vatId)}">
                                        <br>
                                        <s:text name="FeeClass.VAT" />
                                        <s:property value="contractor.vatId" />
                                    </s:if>
								</td>
								<td>
									<s:if test="edit">
										<s:textfield name="invoice.poNumber" size="20" />
									</s:if>
									<s:else>
										<s:property value="invoice.poNumber" />
									</s:else>
								</td>
								<td class="center">
									<s:if test="edit">
										<s:textfield name="invoice.dueDate" size="10" />
									</s:if>
									<s:else>
										<s:date name="invoice.dueDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
									</s:else>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top: 15px;">
						<s:if test="edit && contractor.hasMembershipChanged" >
							<div class="buttons right" style="padding:7px;">
								<a href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id" />&button=changeto" class="picsbutton positive">
									<s:text name="InvoiceDetail.ChangeMembership" />
								</a>
							</div>
						</s:if>
						<table width="100%" class="allborder">
							<tr>
								<th colspan="2">
									<s:text name="InvoiceDetail.ItemDescription" />
								</th>
								<th width="200px">
									<s:text name="InvoiceDetail.FeeAmount" />
								</th>
							</tr>
							<s:iterator value="invoice.items" status="stat">
								<tr>
									<td style="border-right: 0">
										<s:set name="o" value="[0]" />
										<s:include value="../who.jsp" />
										<s:property value="invoiceFee.fee" />
										<s:if test="paymentExpires != null">
											<span style="color: #444; font-style: italic; font-size: 10px;">
												<s:if test="invoiceFee.membership">
													<s:text name="InvoiceDetail.Expires" />
												</s:if>
												<s:else>
													<s:text name="InvoiceDetail.Effective" />
												</s:else>
												<s:date name="paymentExpires" />
											</span>
										</s:if>
									</td>
									<s:if test="edit">
										<td>
											<s:textfield name="invoice.items[%{#stat.index}].description" value="%{description}" size="30" />
											<s:text name="InvoiceDetail.OptionalDescription" />
										</td>
										<td class="right">
											<s:textfield value="%{amount}" size="6" name="invoice.items[%{#stat.index}].amount" />
											<s:if test="!(invoice.currency.EUR||invoice.currency.GBP)">
												<s:property value="invoice.currency"/>
											</s:if>
										</td>
									</s:if>
									<s:else>
										<td style="border-left: 0">
											<s:property value="description" />
										</td>
										<td class="right">
											<s:property value="amount" />
											<s:if test="!(invoice.currency.EUR||invoice.currency.GBP)">
												<s:property value="invoice.currency"/>
											</s:if>
										</td>
									</s:else>
								</tr>
							</s:iterator>
							<s:if test="edit">
								<tr>
									<td colspan="2">
										<s:select list="feeList" name="newFeeId" headerKey="0" headerValue="%{getText('InvoiceDetail.SelectNewFeeToAdd')}" listKey="id" listValue="fee" />
									</td>
									<td class="right">
										___ <s:property value="invoice.currency"/>
									</td>
								</tr>
							</s:if>
							<tr>
								<th colspan="2" class="big right">
									<s:text name="InvoiceDetail.InvoiceTotal" />
								</th>
								<td class="big right">
									<s:property value="invoice.totalAmount" />
									<s:if test="!(invoice.currency.EUR||invoice.currency.GBP)">
										<s:property value="invoice.currency"/>
									</s:if>
								</td>
							</tr>
							<s:if test="invoice.payments.size() > 0">
								<tr>
									<th colspan="2" class="big right">
										<s:text name="InvoiceDetail.Payments" />
									</th>
									<td class="right">
										<s:iterator value="invoice.payments">
											<pics:permission perm="Billing">
												<a href="PaymentDetail.action?payment.id=<s:property value="payment.id" />">
													<s:date name="payment.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
												</a>
											</pics:permission>
											<pics:permission perm="Billing" negativeCheck="true">
												<s:date name="payment.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
											</pics:permission>
											<br />
											<span class="small">
												<s:if test="payment.paymentMethod.creditCard">
													<s:if test="payment.ccType == null || payment.ccType.length() == 0">
														<s:text name="InvoiceDetail.CreditCard" />
													</s:if>
													<s:else>
														<s:property value="payment.ccType"/>
													</s:else>
												</s:if>
												<s:elseif test="payment.paymentMethod.check">
													<s:text name="InvoiceDetail.Check" /> <s:if test="payment.checkNumber != null && payment.checkNumber.length() > 0"><s:text name="InvoiceDetail.CheckNumber" /><s:property value="payment.checkNumber"/></s:if>
												</s:elseif>
												<s:else>
													<label>EFT</label>
												</s:else>
											</span>
											<span class="big">
												(<s:property value="amount" />
												<s:if test="!(invoice.currency.EUR||invoice.currency.GBP)">
													<s:property value="invoice.currency"/>
												</s:if>)
											</span>
											<br />
										</s:iterator>
									</td>
								</tr>
								<tr>
									<th colspan="2" class="big right">
										<s:text name="InvoiceDetail.Balance" />
									</th>
									<td class="big right">
										<s:property value="invoice.balance" />
										<s:if test="!(invoice.currency.EUR||invoice.currency.GBP)">
											<s:property value="invoice.currency"/>
										</s:if>
									</td>
								</tr>
							</s:if>
						</table>
					</td>
				</tr>
                <s:if test="invoice.currency.GBP">
                    <tr>
                        <td style="padding: 15px;">
                            <s:text name="global.UKRegisteredOffice" />
                        </td>
                    </tr>
                </s:if>
				<tr>
					<td style="padding-top:15px; padding-bottom:15px;">
                		<s:if test="edit">
							<div style="float:left; width:98%; align:center">
                				<s:text name="InvoiceDetail.Comments" /> <s:if test="!invoice.status.void" > <s:text name="Invoice.ThankYou" /> <s:text name="Invoice.ClientSiteText" /> </s:if>
                				<br />
                				<textarea name="invoice.notes" rows="4" cols="60">${invoice.notes}</textarea>
                			</div>				
						</s:if>
						<s:else>
							<s:text name="InvoiceDetail.Comments" />
							<s:if test="!invoice.status.void">
								<s:text name="Invoice.ThankYou" />
								<s:text name="Invoice.ClientSiteText" />
							</s:if>
							<s:property value="invoice.notes"/>
						</s:else>
                	</td>
				</tr> 
				<!-- proforma payment contractor -->
				<s:if test="contractor.paymentMethod.EFT">
                    <tr>
                        <td style="padding: 15px;">
                        	<s:if test="invoice.currency.EUR">
                            	<s:text name="InvoiceDetail.PaymentInstruction" />
                            </s:if>
                            <s:elseif test="invoice.currency.GBP">
                            	<s:text name="InvoiceDetail.PaymentInstruction_UK" />
                            </s:elseif>
                        </td>
                    </tr>
                </s:if>
				<tr>
					<td>
						<table width="100%" class="allborder">
							<tr>
								<th width="25%">
									<s:text name="InvoiceDetail.Phone" />
								</th>
                                <s:if test="!invoice.currency.GBP">
    								<th width="25%">
    									<s:text name="InvoiceDetail.Fax" />
    								</th>
                                </s:if>
								<th width="25%">
									<s:text name="InvoiceDetail.Email" />
								</th>
								<th width="25%">
									<s:text name="InvoiceDetail.Website" />
								</th>
							</tr>
							<tr>
								<td class="center">
                                    <s:if test="invoice.currency.CAD">
                                        <s:text name="global.PicsCanadaBillingPhone"/>
                                    </s:if>
                                    <s:elseif test="invoice.currency.GBP">
                                        <s:text name="global.PicsGreatBritainBillingPhone"/>
                                    </s:elseif>
                                    <s:else>
                                        <s:text name="PicsBillingPhone"/>
                                    </s:else>
								</td>
                                <s:if test="!invoice.currency.GBP">
    								<td class="center">
    									<s:text name="PicsBillingFax" />
    								</td>
                                </s:if>
								<td class="center">
									<s:if test="invoice.currency.CAD||invoice.currency.USD">
										billing@picsauditing.com
									</s:if>
									<s:elseif test="invoice.currency.GBP||invoice.currency.EUR">
										eubilling@picsauditing.com
									</s:elseif>
								</td>
								<td class="center">
									www.picsauditing.com
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<s:if test="invoice.currency.EUR|| invoice.currency.GBP">
					<tr>
						<td>
							Company registered Number: 07660778 – VAT Number: GB126 9246 04
						</td>
					</tr>
				</s:if>
			</table>
		</s:form>
	</div>
	
	<div style="margin-top: 10px">	
		<s:include value="_commission_detail.jsp" />
	</div>
	
</html>
