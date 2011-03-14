<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="osha">
<tr class="location">
	<td colspan="3"><s:property value="conAudit.auditFor"/> <s:text name="%{type}.dataHeader"/> <s:property value="location"/> <s:property value="description"/>
		<s:if test="verified"><span class="verified" style="font-size: 16px;" title="Verified by <s:property value="conAudit.auditor.name"/> From PICS">Verified</span></s:if>
	</td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td colspan="2" class="label" style="text-align: center; font-size: smaller;">3 year avg.</td></s:if>
</tr>
<tr>
	<th class="label"><s:text name="totalHoursWorked"/></th>
	<td colspan="2"><s:text name="format.decimal"><s:param value="manHours" /></s:text></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td colspan="2"><s:text name="format.decimal"><s:param value="getAverageOsha( type ).manHours" /></s:text></td></s:if>
</tr>
<tr class="group">
	<th>&nbsp;</th>
	<th class="center">#</th>
	<th class="center">Rate</th>
	<s:if test="type.toString().equals('OSHA') && corporate">
		<th class="center">#</th>
		<th class="center">Rate</th>
	</s:if>
</tr>
<tr>
	<th class="label"><s:text name="fatalities"/></th>
	<td><s:text name="format.number"><s:param value="fatalities" /></s:text></td>
	<td><s:text name="format.decimal"><s:param value="fatalitiesRate" /></s:text></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).fatalities" /></s:text></td><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).fatalitiesRate" /></s:text></td></s:if>
</tr>
<tr>
	<th class="label"><s:text name="%{type}.lostWorkDayCases"/></th>
	<td><s:text name="format.number"><s:param value="lostWorkCases" /></s:text></td>
	<td><s:text name="format.decimal"><s:param value="lostWorkCasesRate" /></s:text></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).lostWorkCases" /></s:text></td><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).lostWorkCasesRate" /></s:text></td></s:if>
</tr>
<tr>
	<th class="label"><s:text name="%{type}.lostWorkDays"/></th>
	<td><s:text name="format.number"><s:param value="lostWorkDays" /></s:text></td>
	<td><s:text name="format.decimal"><s:param value="lostWorkDaysRate" /></s:text></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).lostWorkDays" /></s:text></td><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).lostWorkDaysRate" /></s:text></td></s:if>
</tr>
<tr>
	<th class="label"><s:text name="%{type}.restrictedCases"/></th>
	<td><s:text name="format.number"><s:param value="restrictedWorkCases" /></s:text></td>
	<td><s:text name="format.decimal"><s:param value="restrictedWorkCasesRate" /></s:text></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).restrictedWorkCases" /></s:text></td><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).restrictedWorkCasesRate" /></s:text></td></s:if>
</tr>
<s:if test="#category.id in { 151, 158 }">
	<tr>
		<th class="label"><s:text name="%{type}.modifiedWorkDay"/></th>
		<td><s:text name="format.number"><s:param value="modifiedWorkDay" /></s:text></td>
		<td><s:text name="format.decimal"><s:param value="modifiedWorkDayRate" /></s:text></td>
		<s:if test="type.toString().equals('OSHA') && corporate"><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).modifiedWorkDay" /></s:text></td><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).modifiedWorkDayRate" /></s:text></td></s:if>
	</tr>
</s:if>
<tr>
	<th class="label"><s:text name="%{type}.injuryAndIllness"/></th>
	<td><s:text name="format.number"><s:param value="injuryIllnessCases" /></s:text></td>
	<td><s:text name="format.decimal"><s:param value="injuryIllnessCasesRate" /></s:text></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).injuryIllnessCases" /></s:text></td><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).injuryIllnessCasesRate" /></s:text></td></s:if>
</tr>

<s:if test="#category.id == 158">
<tr>
	<th class="label"><s:text name="%{type}.firstAidInjuries"/></th>
	<td><s:text name="format.number"><s:param value="firstAidInjuries" /></s:text></td>
	<td>&nbsp;</td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td>&nbsp;</td></s:if>
</tr>
</s:if>

<s:if test="type.toString().equals('OSHA')">
	<tr>
		<th class="label">Dart Rate</th>
		<td><s:text name="format.number"><s:param value="(restrictedWorkCases+lostWorkCases)" /></s:text></td>
		<td><s:text name="format.decimal"><s:param value="restrictedDaysAwayRate" /></s:text></td>
		<s:if test="corporate"><td><s:text name="format.decimal"><s:param value="(getAverageOsha( type ).lostWorkCases+getAverageOsha( type ).restrictedWorkCases)" /></s:text></td><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).restrictedDaysAwayRate" /></s:text></td></s:if>
	</tr>
</s:if>

<tr>
	<th class="label"><s:text name="%{type}.totalInjuriesAndIllnesses"/></th>
	<td><s:text name="format.number"><s:param value="recordableTotal" /></s:text></td>
	<td><s:text name="format.decimal"><s:param value="recordableTotalRate" /></s:text></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).recordableTotal" /></s:text></td><td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).recordableTotalRate" /></s:text></td></s:if>
</tr>

<s:if test="#category.id in {151,158}">
	<tr>
		<th class="label">Severity Rate</th>
		<td>
			<s:if test="type.toString().equals('OSHA')">
				<s:text name="format.number"><s:param value="(lostWorkDays+modifiedWorkDay)" /></s:text>
			</s:if>
			<s:else>
				<s:text name="format.number"><s:param value="(lostWorkDays)" /></s:text>
			</s:else>
		</td>
		<td><s:text name="format.number"><s:param value="(restrictedOrJobTransferDays)" /></s:text></td>
		<s:if test="type.toString().equals('OSHA') && corporate">
			<td>
				<s:if test="type.toString().equals('OSHA')">
					<s:text name="format.decimal"><s:param value="(getAverageOsha( type ).lostWorkDays + getAverageOsha( type ).modifiedWorkDay)" /></s:text>
				</s:if>
				<s:else>
					<s:text name="format.decimal"><s:param value="getAverageOsha( type ).lostWorkDays" /></s:text>
				</s:else>
			</td>
			<td><s:text name="format.decimal"><s:param value="getAverageOsha( type ).restrictedOrJobTransferDays" /></s:text></td>
		</s:if>
	</tr>
</s:if>

<s:if test="#category.id == 158">
<tr>
	<th class="label"><s:text name="%{type}.vehicleIncidents"/></th>
	<td><s:text name="format.number"><s:param value="vehicleIncidents" /></s:text></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<th class="label"><s:text name="%{type}.totalkmDriven"/></th>
	<td><s:text name="format.number"><s:param value="totalkmDriven" /></s:text></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<th class="label"><s:text name="%{type}.cad7"/></th>
	<td><s:text name="format.decimal"><s:param value="cad7" /></s:text></td>
	<td>&nbsp;</td>
</tr>
<tr>
	<th class="label"><s:text name="%{type}.neer"/></th>
	<td><s:text name="format.decimal"><s:param value="neer" /></s:text></td>
	<td>&nbsp;</td>
</tr>
</s:if>

<s:if test="type.toString().equals('OSHA')">	
	<tr>
		<th class="label">Uploaded Log Files</th>
		<th colspan="2"><s:if test="fileUploaded"><a href="DownloadOsha.action?id=<s:property value="id"/>" target="_BLANK">Download</a></s:if></th>
		<s:if test="type.toString().equals('OSHA') && corporate"><th colspan="2" class="label">&nbsp;</th></s:if>
	</tr>
</s:if>	
	<s:if test="type.toString().equals('OSHA') && corporate">
		<tr>
			<th class="label">Verification Issues</th>
			<td colspan="2"><nobr><s:if test="verified">None</s:if>
			<s:else><s:property value="comment"/></s:else></nobr></td>
			<td colspan="2" class="label">&nbsp;</td>
		</tr>
	</s:if>
</table>
