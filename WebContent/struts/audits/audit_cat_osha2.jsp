<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="osha">
<tr class="location">
	<td colspan="3"><s:property value="conAudit.auditFor"/> <s:property value="getText('dataHeader.'.concat(type))"/> <s:property value="location"/> <s:property value="description"/></td>
	<s:if test="corporate"><td colspan="2" class="label" style="text-align: center; font-size: smaller;">3 year avg.</td></s:if>
	</tr>
<s:if test="applicable == false && category.id in {151,157} ">
	<tr>
		<th class="label" colspan="3"><s:property value="contractor.name"/> was EXEMPT from submitting <s:property value="conAudit.auditFor"/> <s:property value="type"/> Logs</th>
		<s:if test="corporate"><th colspan="2" class="label">&nbsp;</th></s:if>
	</tr>
</s:if>
<tr>
	<th class="label"><s:property value="getText('totalHoursWorked')"/></th>
	<td colspan="2"><s:property value="%{format(manHours,'#,##0')}"/></td>
	<s:if test="corporate"><td colspan="2"><s:property value="getAverageOsha( type ).manHours"/></td></s:if>
</tr>
<tr class="group">
	<th>&nbsp;</th>
	<th class="center">#</th>
	<th class="center">Rate</th>
	<th class="center">#</th>
	<th class="center">Rate</th>
</tr>
<tr>
	<th class="label"><s:property value="getText('fatalities')"/></th>
	<td><s:property value="fatalities"/></td>
	<td><s:property value="%{format(fatalitiesRate)}"/></td>
	<s:if test="corporate"><td><s:property value="getAverageOsha( type ).fatalities"/></td><td><s:property value="format(getAverageOsha( type ).fatalitiesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('lostWorkDayCases.'.concat(type))"/></th>
	<td><s:property value="lostWorkCases"/></td>
	<td><s:property value="%{format(lostWorkCasesRate)}"/></td>
	<s:if test="corporate"><td><s:property value="getAverageOsha( type ).lostWorkCases"/></td><td><s:property value="format(getAverageOsha( type ).lostWorkCasesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('lostWorkDays.'.concat(type))"/></th>
	<td><s:property value="lostWorkDays"/></td>
	<td><s:property value="%{format(lostWorkDaysRate)}"/></td>
	<s:if test="corporate"><td><s:property value="getAverageOsha( type ).lostWorkDays"/></td><td><s:property value="format(getAverageOsha( type ).lostWorkDaysRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('injuryAndIllness.'.concat(type))"/></th>
	<td><s:property value="injuryIllnessCases"/></td>
	<td><s:property value="%{format(injuryIllnessCasesRate)}"/></td>
	<s:if test="corporate"><td><s:property value="getAverageOsha( type ).injuryIllnessCases"/></td><td><s:property value="format(getAverageOsha( type ).injuryIllnessCasesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('restrictedCases.'.concat(type))"/></th>
	<td><s:property value="restrictedWorkCases"/></td>
	<td><s:property value="%{format(restrictedWorkCasesRate)}"/></td>
	<s:if test="corporate"><td><s:property value="getAverageOsha( type ).restrictedWorkCases"/></td><td><s:property value="format(getAverageOsha( type ).restrictedWorkCasesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('totalInjuriesAndIllnesses.'.concat(type))"/></th>
	<td><s:property value="recordableTotal"/></td>
	<td><s:property value="%{format(recordableTotalRate)}"/></td>
	<s:if test="corporate"><td><s:property value="getAverageOsha( type ).recordableTotal"/></td><td><s:property value="format(getAverageOsha( type ).recordableTotalRate)"/></td></s:if>
</tr>
<s:if test="category.id == 158">
<tr>
	<th class="label"><s:property value="getText('cad7.'.concat(type))"/></th>
	<td><s:property value="cad7"/></td>
	<td>&nbsp;</td>
	<s:if test="corporate"><td>&nbsp;</td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('neer.'.concat(type))"/></th>
	<td><s:property value="neer"/></td>
	<td>&nbsp;</td>
	<s:if test="corporate"><td>&nbsp;</td></s:if>
</tr>
</s:if>
	<tr>
		<th class="label">Uploaded Log Files</th>
		<th colspan="2"><s:if test="fileUploaded"><a href="#" onclick="openOsha(<s:property value="id"/>, 1); return false;">Download</a></s:if></th>
		<th colspan="2" class="label">&nbsp;</th>
	</tr>
	<s:if test="corporate">
		<tr>
			<th class="label">Verification Issues</th>
			<td colspan="2"><nobr><s:if test="verified">None</s:if>
			<s:else><s:property value="comment"/></s:else></nobr></td>
			<td colspan="2" class="label">&nbsp;</td>
		</tr>
	</s:if>
</table>
