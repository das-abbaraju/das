<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">

$(document).ready(function() {
	
	var docuGUARDNum = '<s:property value="docuGUARDNum" />';
	var auditGUARDNum = '<s:property value="auditGUARDNum" />';
	var employeeGUARDNum = '<s:property value="employeeGUARDNum" />';
	
	$('#'+docuGUARDNum+'docuGUARD').css({'background-color':'#FFFF12'});
	$('#'+auditGUARDNum+'auditGUARD').css({'background-color':'#FFFF12'});
	$('#'+employeeGUARDNum+'employeeGUARD').css({'background-color':'#FFFF12'});
	
});
</script>

<h2>PICS Annual Membership Price</h2>
<p>
	The cells highlighted in yellow are the prices that apply to your account.
</p>

<table class="report center"
	style="margin-left: auto; margin-right: auto;">

	<thead>
		<tr>
			<td class="center" style="background-color: #062541">
				<s:text name="global.Operators" />
			</td>
			<td class="center" style="background-color: #79b7e0">
				DocuGUARD<br />
				InsureGUARD
			</td>
			<td class="center" style="background-color: #a75025">
				AuditGUARD
			</td>
			<td class="center" style="background-color: #838486">
				Employee<br />
				GUARD
			</td>
		</tr>
	</thead>
	<tbody>
		<tr id="1">
			<td class="center">
				1
			</td>
			<td class="center" id="1docuGUARD">
				$99
			</td>
			<td class="center" id="1auditGUARD">
				+$399
			</td>
			<td class="center" id="1employeeGUARD">
				+$99
			</td>
		</tr>
		<tr id="2">
			<td class="center">
				2-4
			</td>
			<td class="center" id="2docuGUARD">
				$99
			</td>
			<td class="center" id="2auditGUARD">
				+$799
			</td>
			<td class="center" id="2employeeGUARD">
				+$199
			</td>
		</tr>
		<tr id="5">
			<td class="center">
				5-8
			</td>
			<td class="center" id="5docuGUARD">
				$99
			</td>
			<td class="center" id="5auditGUARD">
				+$1,199
			</td>
			<td class="center" id="5employeeGUARD">
				+$299
			</td>
		</tr>
		<tr id="9">
			<td class="center">
				9-12
			</td>
			<td class="center" id="9docuGUARD">
				$99
			</td>
			<td class="center" id="9auditGUARD">
				+$1,499
			</td>
			<td class="center" id="9employeeGUARD">
				+$399
			</td>
		</tr>
		<tr id="13">
			<td class="center">
				13-19
			</td>
			<td class="center" id="13docuGUARD">
				$99
			</td>
			<td class="center" id="13auditGUARD">
				+$1,899
			</td>
			<td class="center" id="13employeeGUARD">
				+$599
			</td>
		</tr>
		<tr id="20">
			<td class="center">
				20-49
			</td>
			<td class="center" id="20docuGUARD">
				$99
			</td>
			<td class="center" id="20auditGUARD">
				+$2,899
			</td>
			<td class="center" id="20employeeGUARD">
				+$799
			</td>
		</tr>
		<tr id="50">
			<td class="center">
				50+
			</td>
			<td class="center" id="50docuGUARD">
				$99
			</td>
			<td class="center" id="50auditGUARD">
				+$3,899
			</td>
			<td class="center" id="50employeeGUARD">
				+$999
			</td>
		</tr>
	</tbody>
</table>

<p class="redMain">
	* There is an account activation/reactivation fee of $199
</p>
