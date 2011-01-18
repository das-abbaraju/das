<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="allborder">
	<tr>
		<th>Item &amp; Description</th>
		<th width="100px">Fee Amount</th>
	</tr>
	<s:iterator value="#i.items">
		<tr>
			<td>
				<s:property value="invoiceFee.fee" />
				<span style="color: #444; font-style: italic; font-size: 10px;">
				<s:if test="invoiceFee.feeClass == 'Activation'">effective
					<s:if test="paymentExpires == null"><s:date name="invoice.creationDate" format="MMM d, yyyy" /></s:if>
					<s:else><s:date name="paymentExpires" /></s:else>
				</s:if>
				<s:if test="invoiceFee.feeClass == 'Membership' && paymentExpires != null">
					expires <s:date name="paymentExpires" format="MMM d, yyyy"/>
				</s:if>
				</span>
			</td>
			<td class="right">
				$<s:property value="amount" /> <s:property value="invoice.account.currencyCode" />
			</td>
		</tr>
	</s:iterator>
	<tr>
		<th class="big right">Invoice Total</th>
		<td class="big right">$<s:property value="#i.totalAmount" /> <s:property value="i.account.currencyCode" /></td>
	</tr>
</table>
