<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Contractor" /></th>
			<th><s:text name="AuditType.232.name" /></th>
			<th><s:text name="%{scope}.label.ImportFeePaid" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="get('accountID')" />"><s:property value="get('name')" /></a></td>
				<td><a href="Audit.action?auditID=<s:property value="get('auditID')" />"><s:text name="ContractorDocuments.header.Audit" /></a></td>
				<td><a href="InvoiceDetail.action?invoice.id=<s:property value="get('invoiceID')" />"><s:property value="get('invoiceStatus')" /></a></td>
			</tr>
		</s:iterator>
		<s:if test="allRows > 10">
			<tr>
				<td colspan="3" class="right">
					<s:text name="Report.message.RowsFound">
						<s:param value="%{allRows}" />
					</s:text>
				</td>
			</tr>
		</s:if>
	</tbody>
</table>