<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="osha">
<tr class="location">
	<td colspan="3"><s:property value="conAudit.auditFor"/> <s:property value="getText('dataHeader.'.concat(type))"/> <s:property value="location"/> <s:property value="description"/>
		<s:if test="verified"><span class="verified" style="font-size: 16px;" title="Verified by <s:property value="conAudit.auditor.name"/> From PICS">Verified</span></s:if>
	</td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td colspan="2" class="label" style="text-align: center; font-size: smaller;">3 year avg.</td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('totalHoursWorked')"/></th>
	<td colspan="2"><s:property value="%{format(manHours,'#,##0')}"/></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td colspan="2"><s:property value="getAverageOsha( type ).manHours"/></td></s:if>
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
	<th class="label"><s:property value="getText('fatalities')"/></th>
	<td><s:property value="fatalities"/></td>
	<td><s:property value="%{format(fatalitiesRate)}"/></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:property value="getAverageOsha( type ).fatalities"/></td><td><s:property value="format(getAverageOsha( type ).fatalitiesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('lostWorkDayCases.'.concat(type))"/></th>
	<td><s:property value="lostWorkCases"/></td>
	<td><s:property value="%{format(lostWorkCasesRate)}"/></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:property value="getAverageOsha( type ).lostWorkCases"/></td><td><s:property value="format(getAverageOsha( type ).lostWorkCasesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('lostWorkDays.'.concat(type))"/></th>
	<td><s:property value="lostWorkDays"/></td>
	<td><s:property value="%{format(lostWorkDaysRate)}"/></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:property value="getAverageOsha( type ).lostWorkDays"/></td><td><s:property value="format(getAverageOsha( type ).lostWorkDaysRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('restrictedCases.'.concat(type))"/></th>
	<td><s:property value="restrictedWorkCases"/></td>
	<td><s:property value="%{format(restrictedWorkCasesRate)}"/></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:property value="getAverageOsha( type ).restrictedWorkCases"/></td><td><s:property value="format(getAverageOsha( type ).restrictedWorkCasesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('injuryAndIllness.'.concat(type))"/></th>
	<td><s:property value="injuryIllnessCases"/></td>
	<td><s:property value="%{format(injuryIllnessCasesRate)}"/></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:property value="getAverageOsha( type ).injuryIllnessCases"/></td><td><s:property value="format(getAverageOsha( type ).injuryIllnessCasesRate)"/></td></s:if>
</tr>

<s:if test="type.toString().equals('OSHA')">
	<tr>
		<th class="label">Dart Rate</th>
		<td><s:property value="%{restrictedWorkCases+lostWorkCases}"/></td>
		<td><s:property value="%{format(restrictedDaysAwayRate)}"/></td>
		<s:if test="corporate"><td><s:property value="%{getAverageOsha( type ).lostWorkCases+getAverageOsha( type ).restrictedWorkCases}"/></td><td><s:property value="format(getAverageOsha( type ).restrictedDaysAwayRate)"/></td></s:if>
	</tr>
</s:if>

<tr>
	<th class="label"><s:property value="getText('totalInjuriesAndIllnesses.'.concat(type))"/></th>
	<td><s:property value="recordableTotal"/></td>
	<td><s:property value="%{format(recordableTotalRate)}"/></td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td><s:property value="getAverageOsha( type ).recordableTotal"/></td><td><s:property value="format(getAverageOsha( type ).recordableTotalRate)"/></td></s:if>
</tr>

<s:if test="category.id == 158">
<tr>
	<th class="label"><s:property value="getText('cad7.'.concat(type))"/></th>
	<td><s:property value="cad7"/></td>
	<td>&nbsp;</td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td>&nbsp;</td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('neer.'.concat(type))"/></th>
	<td><s:property value="neer"/></td>
	<td>&nbsp;</td>
	<s:if test="type.toString().equals('OSHA') && corporate"><td>&nbsp;</td></s:if>
</tr>
</s:if>
<s:if test="!type.toString().equals('COHS') && category.id != 157">	
	<tr>
		<th class="label">Uploaded Log Files</th>
		<th colspan="2"><s:if test="fileUploaded"><a href="#" onclick="openOsha(<s:property value="id"/>); return false;">Download</a></s:if></th>
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
