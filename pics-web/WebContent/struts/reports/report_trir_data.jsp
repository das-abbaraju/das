<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
	    <th></th>
	    <th><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></th>
	    <th>Type</th>
	    <th><a href="javascript: changeOrderBy('form1','fcc.answer+0');">Rate</a></th>
		<s:if test="showContact">
			<td><s:text name="global.ContactPrimary" /></td>
			<td><s:text name="User.phone" /></td>
			<td><s:text name="User.email" /></td>
			<td><s:text name="global.OfficeAddress" /></td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a></td>
			<td><a href="javascript: changeOrderBy('form1','a.countrySubdivision,a.name');"><s:text name="CountrySubdivision" /></a></td>
			<td><s:text name="global.ZipPostalCode" /></td>
			<td><s:text name="ContractorAccount.webUrl" /></td>
		</s:if>
		<s:if test="showTrade">
			<td><s:text name="Trade" /></td>
		</s:if>	    
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a></td>
			<td><s:property value="get('SHAType')"/></td>
			<td class="right"><s:property value="get('answer')"/></td>
			<s:if test="showContact">
				<td><s:property value="get('contactname')"/></td>
				<td><s:property value="get('contactphone')"/></td>
				<td><s:property value="get('contactemail')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('countrySubdivision')"/></td>
				<td><s:property value="get('zip')"/></td>
				<td><s:property value="get('web_URL')"/></td>
			</s:if>
			<s:if test="showTrade">
				<td><s:property value="get('main_trade')"/></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>