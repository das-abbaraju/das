<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" type="text/css" media="screen" href="css/pricing_matrix.css?v=${version}" />
<script type="text/javascript">

$(document).ready(function() {
	var docuGUARDNum = '<s:property value="docuGUARDNum" />';
	var insureGUARDNum = '<s:property value="insureGUARDNum" />';
	var auditGUARDNum = '<s:property value="auditGUARDNum" />';
	var employeeGUARDNum = '<s:property value="employeeGUARDNum" />';

	$('#'+docuGUARDNum+'DocuGUARD').css({'background-color':'#FFFF12'});
	$('#'+insureGUARDNum+'InsureGUARD').css({'background-color':'#FFFF12'});
	$('#'+auditGUARDNum+'AuditGUARD').css({'background-color':'#FFFF12'});
	$('#'+employeeGUARDNum+'EmployeeGUARD').css({'background-color':'#FFFF12'});
	
});
</script>

<h2 class="center"><s:text name="ContractorPricing.MatrixTitle" /></h2>
<p>
	<s:if test="con != null">
		<p class="center"><s:text name="ContractorPricing.Description"></s:text></p>
	</s:if>
</p>

<table class="report center">
	<thead>
		<tr>
			<td class="operatorsheader">
				<s:text name="global.Operators" />
			</td>
			<td class="docuguardheader">
				<s:text name="global.DocuGUARD"></s:text>
			</td>
			<td class="insureguardheader">
				<s:text name="global.InsureGUARD"></s:text>
			</td>
			<td class="auditguardheader">
				<s:text name="global.AuditGUARD"></s:text>
			</td>
			<td class="employeeguardheader">
				<s:text name="global.EmployeeGUARD"></s:text>
			</td>
		</tr>
	</thead>
	<tbody>
		<tr id="1">
			<td class="center">
				1
			</td>
			<td class="center" id="1DocuGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('1DocuGUARD')"/>
			</td>
			<td class="center" id="1InsureGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('1InsureGUARD')"/>
			</td>
			<td class="center" id="1AuditGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('1AuditGUARD')"/>
			</td>
			<td class="center" id="1EmployeeGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('1EmployeeGUARD')"/>
			</td>
		</tr>
		<tr id="2">
			<td class="center">
				2-4
			</td>
			<td class="center" id="2DocuGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('2DocuGUARD')"/>
			</td>
			<td class="center" id="2InsureGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('2InsureGUARD')"/>
			</td>
			<td class="center" id="2AuditGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('2AuditGUARD')"/>
			</td>
			<td class="center" id="2EmployeeGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('2EmployeeGUARD')"/>
			</td>
		</tr>
		<tr id="5">
			<td class="center">
				5-8
			</td>
			<td class="center" id="5DocuGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('5DocuGUARD')"/>
			</td>
			<td class="center" id="5InsureGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('5InsureGUARD')"/>
			</td>
			<td class="center" id="5AuditGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('5AuditGUARD')"/>
			</td>
			<td class="center" id="5EmployeeGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('5EmployeeGUARD')"/>
			</td>
		</tr>
		<tr id="9">
			<td class="center">
				9-12
			</td>
			<td class="center" id="9DocuGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('9DocuGUARD')"/>
			</td>
			<td class="center" id="9InsureGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('9InsureGUARD')"/>
			</td>
			<td class="center" id="9AuditGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('9AuditGUARD')"/>
			</td>
			<td class="center" id="9EmployeeGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('9EmployeeGUARD')"/>
			</td>
		</tr>
		<tr id="13">
			<td class="center">
				13-19
			</td>
			<td class="center" id="13DocuGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('13DocuGUARD')"/>
			</td>
			<td class="center" id="13InsureGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('13InsureGUARD')"/>
			</td>
			<td class="center" id="13AuditGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('13AuditGUARD')"/>
			</td>
			<td class="center" id="13EmployeeGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('13EmployeeGUARD')"/>
			</td>
		</tr>
		<tr id="20">
			<td class="center">
				20-49
			</td>
			<td class="center" id="20DocuGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('20DocuGUARD')"/>
			</td>
			<td class="center" id="20InsureGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('20InsureGUARD')"/>
			</td>
			<td class="center" id="20AuditGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('20AuditGUARD')"/>
			</td>
			<td class="center" id="20EmployeeGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('20EmployeeGUARD')"/>
			</td>
		</tr>
		<tr id="50">
			<td class="center">
				50+
			</td>
			<td class="center" id="50DocuGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('50DocuGUARD')"/>
			</td>
			<td class="center" id="50InsureGUARD">
				<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('50InsureGUARD')"/>
			</td>
			<td class="center" id="50AuditGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('50AuditGUARD')"/>
			</td>
			<td class="center" id="50EmployeeGUARD">
				+<s:property value="%{con.country.currency.symbol}" /><s:property value="getPrice('50EmployeeGUARD')"/>
			</td>
		</tr>
	</tbody>
</table>

<p class="redMain center">
	<s:text name="ContractorPricing.ActivationFee">
	<s:param><s:property value="%{con.country.currency.symbol}" /></s:param>
	<s:param><s:property value="getPrice('1Activation')"/></s:param>
	</s:text>
</p>
