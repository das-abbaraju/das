<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="allborder">
	<tr>
		<th><s:text name="InvoiceEmbed.ItemAndDescription" /></th>
		<th width="100px"><s:text name="InvoiceEmbed.FeeAmount" /></th>
	</tr>
	<s:iterator value="#i.items">
		<tr>
			<td>
				<s:if test="invoiceFee.fee.length > 0">
					<s:property value="invoiceFee.fee" />
				</s:if>
				<s:else>
					<s:text name="InvoiceFee.%{invoiceFee.id}.fee" />
				</s:else>
				<span style="color: #444; font-style: italic; font-size: 10px;">
				<s:if test="invoiceFee.feeClass == 'Activation'">
					<s:text name="InvoiceEmbed.Effective" >
						<s:param name="%{paymentExpires == null ? invoice.creationDate : paymentExpires}" />
					</s:text>
				</s:if>
				<s:if test="invoiceFee.feeClass == 'Membership' && paymentExpires != null">
					<s:text name="InvoiceEmbed.Expires" >
						<s:param name="%{paymentExpires}" />
					</s:text>
				</s:if>
				</span>
			</td>
			<td class="right">
				<s:property value="#i.currency.symbol" /><s:property value="amount" /> <s:property value="#i.currency" />
			</td>
		</tr>
	</s:iterator>
	<tr>
		<th class="big right"><s:text name="ContractorRegistrationFinish.InvoiceTotal" /></th>
		<td class="big right"><s:property value="#i.currency.symbol" /><s:property value="#i.totalAmount" /> <s:property value="#i.currency" /></td>
	</tr>
</table>
