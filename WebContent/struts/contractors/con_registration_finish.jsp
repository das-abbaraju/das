<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ContractorRegistrationFinish.RegistrationCompletion" /></title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" href="css/invoice.css?v=<s:property value="version"/>"/>
</head>
<body>

<s:if test="contractor.trades.size == 0">
	<div class="error"><s:text name="ContractorRegistrationFinish.error.AddTrade" /> <a
		href="ContractorTrades.action"><s:text name="ContractorRegistrationFinish.ClickToAddTrade" /></a></div>
</s:if>
<s:elseif test="contractor.operators.size == 0">
	<div class="error"><s:text name="ContractorRegistrationFinish.error.AddFacility" /> <a
		href="ContractorFacilities.action"><s:text name="ContractorRegistrationFinish.ClickToAddFacility" /></a></div>
</s:elseif>
<s:elseif test="!contractor.paymentMethodStatusValid && contractor.mustPayB">
	<div class="error"><s:text name="ContractorRegistrationFinish.error.AddPaymentMethod" /> <a 
		href="ContractorPaymentOptions.action"><s:text name="ContractorRegistrationFinish.ClickToAddCreditCard" /></a></div>
</s:elseif>
<s:else>
	<s:if test="complete">
		<div class="info">
			<s:text name="ContractorRegistrationFinish.RegistrationSuccess" /><s:if test="contractor.mustPayB"> <s:text name="ContractorRegistrationFinish.EmailSent" /></s:if>
			<s:if test="contractor.status.active">
				<div class="buttons">
					<a href="Home.action" class="picsbutton positive"><s:text name="ContractorRegistrationFinish.ClickForHomePage" /></a>
				</div>
			</s:if>
			<s:else>
				<strong><s:text name="ContractorRegistrationFinish.FullAccessOnPayment" /></strong>
			</s:else>
			<div class="clear"></div>
		</div>
	</s:if>
	<s:else>
		<s:if test="contractor.paymentMethod.check">
			<div class="alert">
				<s:text name="ContractorRegistrationFinish.PaymentByCheck" ><s:param><s:property value="invoice.currency.symbol" /></s:param><s:param><s:property value="invoice.totalAmount"/></s:param></s:text>
				<a href="ContractorPaymentOptions.action"><s:text name="ContractorRegistrationFinish.ClickToAddCreditCard" /></a>.
			</div>
		</s:if>
		<s:if test="contractor.status.pendingDeactivated">
			<div>
				<s:form>
					<s:hidden name="id" value="%{contractor.id}"/>
					<div>
						<s:if test="contractor.safetyRisk.toString().equals('None')">
						<s:text name="ContractorRegistrationFinish.RiskLevel"><s:param><s:property value="contractor.safetyRisk"/></s:param></s:text>	<br/>
						</s:if>
						<s:if test="contractor.acceptsBids">
							<s:text name="ContractorRegistrationFinish.BidOnly" />: <br clear="all"/>
							<ul>
								<s:iterator value="contractor.audits">
									<s:if test="!auditType.pqf">
										<li><s:property value="auditType.name"/> <s:property value="auditFor"/></li>
									</s:if>
								</s:iterator>
							</ul>
						</s:if>
						<s:else>
							<s:text name="ContractorRegistrationFinish.OperatorsSelected" />:
								<s:iterator value="contractor.nonCorporateOperators" status="stat">
									<s:if test="#stat.last">
										<s:text name="ContractorRegistrationFinish.And" />
									</s:if>
									<s:property value="operatorAccount.name"/>,
								</s:iterator>
							<s:text name="ContractorRegistrationFinish.AuditsApply" />: 
	
							<br clear="all"/>
	
							<s:iterator value="auditMenu">
								<s:if test="children.size() > 0">
									<div style="float:left;width: <s:property value="100 / auditMenu.size() * 0.9"/>%">
										<strong style="font-size:16px"><s:property value="name" escape="false"/></strong>
										<ul>
											<s:iterator value="children">
												<li><s:property value="name" escape="false"/></li>
											</s:iterator>
										</ul>
									</div>
								</s:if>
							</s:iterator>
						</s:else>
						<br clear="all"/>
						<s:if test="contractor.newMembershipAmount > 0">
							<h3><s:text name="ContractorRegistrationFinish.InvoiceSummary" /></h3>
							<br clear="all"/>
							<s:if test="contractor.mustPayB">
								<s:set name="i" value="invoice"/>
								<s:include value="con_invoice_embed.jsp"/>
							</s:if>
							<s:else>
								<table class="allborder">
									<tr>
										<th><s:text name="ContractorRegistrationFinish.Item" /> &amp; <s:text name="ContractorRegistrationFinish.Description" /></th>
										<th width="100px"><s:text name="ContractorRegistrationFinish.FeeAmount" /></th>
									</tr>
									<tr>
										<th class="big right"><s:text name="ContractorRegistrationFinish.InvoiceTotal" /></th>
										<td class="big right"><s:text name="ContractorRegistrationFinish.Free" /></td>
									</tr>
								</table>
							</s:else>
							<br clear="all"/>
							<s:if test="contractor.paymentMethod.creditCard">
								<s:if test="contractor.mustPayB">
									<div class="info">
										<s:text name="ContractorRegistrationFinish.ActivateCreditCard"><s:param><s:property value="invoice.currency.symbol" /></s:param><s:param><s:property value="invoice.totalAmount" /></s:param></s:text>
									</div>
								</s:if>
								<s:else>
									<div class="info">
										<s:text name="ContractorRegistrationFinish.ActivateNoPay" />
									</div>
								</s:else>
							</s:if>
						</s:if>
						
						<s:submit action="ContractorRegistrationFinish!completeRegistration" cssClass="picsbutton positive" value="%{getText('ContractorRegistrationFinish.CompleteMyRegistration')}" /> 
					</div>
				</s:form>
			</div>
		</s:if>
	</s:else>
</s:else>
</body>
</html>
									