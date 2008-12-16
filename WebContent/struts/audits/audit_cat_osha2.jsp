<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="permissions.contractor">
	<span class="redMain">You must input at least your corporate statistics. To further assist your clients, please
	enter additional locations that you maintain OSHA/MSHA logs for that may be needed by your clients.
	</span>
</s:if>
<table class="osha">
<tr class="location">
	<td colspan="3"><s:property value="conAudit.auditFor"/> <s:property value="type"/> Logs - <s:property value="location"/> <s:property value="description"/></td>
	<s:if test="corporate"><td colspan="2" class="label" style="text-align: center; font-size: smaller;">3 year avg.</td></s:if>
	</tr>
<s:if test="applicable">

</s:if>
<s:else>
	<tr>
		<th class="label" colspan="3"><s:property value="contractor.name"/> was EXEMPT from submitting <s:property value="conAudit.auditFor"/> <s:property value="type"/> Logs</th>
	</tr>
	<s:if test="corporate"><th colspan="2" class="label">&nbsp;</th></s:if>
</s:else>
<tr>
	<th class="label">Total Man Hours Worked</th>
	<td colspan="2"><s:property value="%{format(manHours,'#,##0')}"/></td>
	<s:if test="corporate"><td colspan="2"><s:property value="averageOsha.manHours"/></td></s:if>
</tr>
<tr class="group">
	<th>&nbsp;</th>
	<th class="center">#</th>
	<th class="center">Rate</th>
	<th class="center">#</th>
	<th class="center">Rate</th>
</tr>
<tr>
	<th class="label">Number of Fatalities</th>
	<td><s:property value="fatalities"/></td>
	<td><s:property value="%{format(fatalitiesRate)}"/></td>
	<s:if test="corporate"><td><s:property value="averageOsha.fatalities"/></td><td><s:property value="format(averageOsha.fatalitiesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label">Number of Lost Workday Cases - Has lost days AND is <s:property value="descriptionOsMs"/></th>
	<td><s:property value="lostWorkCases"/></td>
	<td><s:property value="%{format(lostWorkCasesRate)}"/></td>
	<s:if test="corporate"><td><s:property value="averageOsha.lostWorkCases"/></td><td><s:property value="format(averageOsha.lostWorkCasesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label">Number of Lost Workdays - All lost workdays (regardless of restricted days) AND is <s:property value="descriptionOsMs"/></th>
	<td><s:property value="lostWorkDays"/></td>
	<td><s:property value="%{format(lostWorkDaysRate)}"/></td>
	<s:if test="corporate"><td><s:property value="averageOsha.lostWorkDays"/></td><td><s:property value="format(averageOsha.lostWorkDaysRate)"/></td></s:if>
</tr>
<tr>
	<th class="label">Injury & Illnesses Medical Cases - No lost OR restricted days AND is <s:property value="descriptionOsMs"/> (non-fatal)</th>
	<td><s:property value="injuryIllnessCases"/></td>
	<td><s:property value="%{format(injuryIllnessCasesRate)}"/></td>
	<s:if test="corporate"><td><s:property value="averageOsha.injuryIllnessCases"/></td><td><s:property value="format(averageOsha.injuryIllnessCasesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label">Restricted Cases - Has restricted days AND no lost days AND is <s:property value="descriptionOsMs"/></th>
	<td><s:property value="restrictedWorkCases"/></td>
	<td><s:property value="%{format(restrictedWorkCasesRate)}"/></td>
	<s:if test="corporate"><td><s:property value="averageOsha.restrictedWorkCases"/></td><td><s:property value="format(averageOsha.restrictedWorkCasesRate)"/></td></s:if>
</tr>
<tr>
	<th class="label">Total <s:property value="descriptionOsMs"/> Injuries and Illnesses</th>
	<td><s:property value="recordableTotal"/></td>
	<td><s:property value="%{format(recordableTotalRate)}"/></td>
	<s:if test="corporate"><td><s:property value="averageOsha.recordableTotal"/></td><td><s:property value="format(averageOsha.recordableTotalRate)"/></td></s:if>
</tr>
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
