<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<th>Date</th>
		</tr>
	</thead>
	<s:iterator value="pendingApprovalBiddingContractors">
		<tr>
			<td><a href="BiddingContractorSearch.action"><s:property value="contractorAccount.name" /></a></td>
			<td class="center"><s:date name="creationDate" format="%{getText('MonthAndDayTime')}" /></td>
		</tr>
	</s:iterator>
</table>
