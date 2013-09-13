<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<title><s:text name="ContractorRegistration.title" /></title>

<div class="make-payment">
	<div>
		<table>
			<tr>
				<td><div><s:text name="global.BillingAddress" /></div></td>
				
				<td>
				<table class="invoice">
					<tr>
						<th><s:text name="InvoiceDetail.Date"></s:text></th>
						<s:if test="invoice.id > 0">
							<th class="big" style="white-space: nowrap;"><s:text name="InvoiceDetail.InvoiceNumber"></s:text></th>
						</s:if>
					</tr>
					<tr>
						<td class="center"><nobr> <s:date name="invoice.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" /> <s:set
							name="o" value="invoice"></s:set> <s:include value="../who.jsp"></s:include> </nobr></td>
						<s:if test="invoice.id > 0">
							<td class="center"><s:property value="invoice.id" /></td>
						</s:if>
					</tr>
				</table>
		</table>
		<table class="invoice">
			<tr>
				<th><s:text name="InvoiceDetail.BillTo" /></th>
				<th width="16%"><s:text name="InvoiceDetail.PoNumber" /></th>
				<th width="16%"><s:text name="InvoiceDetail.DueDate" /></th>
			</tr>
			<tr>
				<td><s:property value="contractor.name" /><br />
					c/o <s:property value="billingUser.name" />
					<br />
					<s:property value="contractor.address" /><br />
					<s:property value="contractor.city" />, <s:property value="contractor.countrySubdivision.isoCode" />
					<s:property	value="contractor.zip" />
				</td>
				<td>
					<s:property value="invoice.poNumber" />
				</td>
				<td class="center">
					<s:date name="invoice.dueDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
				</td>
			</tr>
		</table>
		<table width="100%" class="invoice">
			<tr>
				<th colspan="2"><s:text name="InvoiceDetail.ItemDescription" /></th>
				<th width="200px"><s:text name="InvoiceDetail.FeeAmount" /></th>
			</tr>
			<s:iterator value="invoice.items" status="stat">
				<tr>
					<td style="border-right: 0"><s:set name="o" value="[0]"></s:set> <s:include value="../who.jsp"></s:include> <s:property
						value="invoiceFee.fee" /> <span style="color: #444; font-style: italic; font-size: 10px;">
						<s:if test="paymentExpires != null">
							<s:if test="invoiceFee.membership"><s:text name="InvoiceDetail.Expires" /></s:if><s:else><s:text name="InvoiceDetail.Effective" /></s:else>
							<s:date name="paymentExpires" />
						</s:if></span></td>
					<td style="border-left: 0"><s:property value="description" /></td>
					<td class="right"><s:property value="amount" /> <s:property value="invoice.currency"/></td>
				</tr>
			</s:iterator>
			<tr>
				<th colspan="2" class="big right"><s:text name="InvoiceDetail.InvoiceTotal" /></th>
				<td class="big right"><s:property value="invoice.totalAmount" /> <s:property value="invoice.currency"/></td>
			</tr>
		</table>
		<s:text name="InvoiceDetail.Comments" />
		<s:property value="invoice.notes" />

		<table width="100%" class="invoice">
			<tr>
				<th width="25%"><s:text name="InvoiceDetail.Phone" /></th>
				<th width="25%"><s:text name="InvoiceDetail.Fax" /></th>
				<th width="25%"><s:text name="InvoiceDetail.Email" /></th>
				<th width="25%"><s:text name="InvoiceDetail.Website" /></th>
			</tr>
			<tr>
				<td class="center"><s:property value="permissions.picsPhone" /></td>
				<td class="center"><s:property value="permissions.picsBillingFax" /></td>
				<td class="center">
					<s:if test="invoice.currency.CAD||invoice.currency.USD">
						billing@picsauditing.com
					</s:if>
					<s:elseif test="invoice.currency.GBP||invoice.currency.EUR">
						eubilling@picsauditing.com
					</s:elseif>
				</td>
				<td class="center">www.picsorganizer.com</td>
			</tr>
		</table>
	</div>
</div>