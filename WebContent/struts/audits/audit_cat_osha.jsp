<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="permissions.contractor">
	<span class="redMain">You must input at least your corporate statistics. To further assist your clients, please
	enter additional locations that you maintain OSHA/MSHA logs for that may be needed by your clients.
	</span>
</s:if>
<table class="osha">
<tr class="location"><td colspan="9"><s:property value="type"/> Location - <s:property value="location"/> <s:property value="description"/></td></tr>
<tr>
	<th>Year</td>
	<th class="center" colspan="2">2007</th>
	<th class="center" colspan="2">2006</th>
	<th class="center" colspan="2">2005</th>
</tr>
<tr>
	<th class="label">Were you exempt from submitting <s:property value="type"/> Logs?</th>
	<th class="center" colspan="2"><s:property value="year1.na"/></th>
	<th class="center" colspan="2"><s:property value="year2.na"/></th>
	<th class="center" colspan="2"><s:property value="year3.na"/></th>
</tr>
<tr>
	<th class="label">Total Man Hours Worked</th>
	<td colspan="2"><s:property value="%{format(year1.manHours,'#,##0')}"/></td>
	<td colspan="2"><s:property value="%{format(year2.manHours,'#,##0')}"/></td>
	<td colspan="2"><s:property value="%{format(year3.manHours,'#,##0')}"/></td>
</tr>
<tr class="group">
	<th></th>
	<th class="center">#</th>
	<th class="center">Rate</th>
	<th class="center">#</th>
	<th class="center">Rate</th>
	<th class="center">#</th>
	<th class="center">Rate</th>
</tr>
<tr>
	<th class="label">Number of Fatalities</th>
	<td><s:property value="year1.fatalities"/></td>
	<td><s:property value="%{format(year1.fatalitiesRate)}"/></td>
	<td><s:property value="year2.fatalities"/></td>
	<td><s:property value="%{format(year2.fatalitiesRate)}"/></td>
	<td><s:property value="year3.fatalities"/></td>
	<td><s:property value="%{format(year3.fatalitiesRate)}"/></td>
</tr>
<tr>
	<th class="label">Number of Lost Workday Cases - Has lost days AND is <s:property value="descriptionOsMs"/></th>
	<td><s:property value="year1.lostWorkCases"/></td>
	<td><s:property value="%{format(year1.lostWorkCasesRate)}"/></td>
	<td><s:property value="year2.lostWorkCases"/></td>
	<td><s:property value="%{format(year2.lostWorkCasesRate)}"/></td>
	<td><s:property value="year3.lostWorkCases"/></td>
	<td><s:property value="%{format(year3.lostWorkCasesRate)}"/></td>
</tr>
<tr>
	<th class="label">Number of Lost Workdays - All lost workdays (regardless of restricted days) AND is <s:property value="descriptionOsMs"/></th>
	<td><s:property value="year1.lostWorkDays"/></td>
	<td><s:property value="%{format(year1.lostWorkDaysRate)}"/></td>
	<td><s:property value="year2.lostWorkDays"/></td>
	<td><s:property value="%{format(year2.lostWorkDaysRate)}"/></td>
	<td><s:property value="year3.lostWorkDays"/></td>
	<td><s:property value="%{format(year3.lostWorkDaysRate)}"/></td>
</tr>
<tr>
	<th class="label">Injury & Illnesses Medical Cases - No lost OR restricted days AND is <s:property value="descriptionOsMs"/> (non-fatal)</th>
	<td><s:property value="year1.injuryIllnessCases"/></td>
	<td><s:property value="%{format(year1.injuryIllnessCasesRate)}"/></td>
	<td><s:property value="year2.injuryIllnessCases"/></td>
	<td><s:property value="%{format(year2.injuryIllnessCasesRate)}"/></td>
	<td><s:property value="year3.injuryIllnessCases"/></td>
	<td><s:property value="%{format(year3.injuryIllnessCasesRate)}"/></td>
</tr>
<tr>
	<th class="label">Restricted Cases - Has restricted days AND no lost days AND is <s:property value="descriptionOsMs"/></th>
	<td><s:property value="year1.restrictedWorkCases"/></td>
	<td><s:property value="%{format(year1.restrictedWorkCasesRate)}"/></td>
	<td><s:property value="year2.restrictedWorkCases"/></td>
	<td><s:property value="%{format(year2.restrictedWorkCasesRate)}"/></td>
	<td><s:property value="year3.restrictedWorkCases"/></td>
	<td><s:property value="%{format(year3.restrictedWorkCasesRate)}"/></td>
</tr>
<tr>
	<th class="label">Total <s:property value="descriptionOsMs"/> Injuries and Illnesses</th>
	<td><s:property value="year1.recordableTotal"/></td>
	<td><s:property value="%{format(year1.recordableTotalRate)}"/></td>
	<td><s:property value="year2.recordableTotal"/></td>
	<td><s:property value="%{format(year2.recordableTotalRate)}"/></td>
	<td><s:property value="year3.recordableTotal"/></td>
	<td><s:property value="%{format(year3.recordableTotalRate)}"/></td>
</tr>
<tr>
	<th class="label">Uploaded Log Files</th>
	<th colspan="2"><s:if test="year1.uploaded"><a href="#" onclick="openOsha(<s:property value="id"/>, 1); return false;">Download</a></s:if></th>
	<th colspan="2"><s:if test="year2.uploaded"><a href="#" onclick="openOsha(<s:property value="id"/>, 2); return false;">Download</a></s:if></th>
	<th colspan="2"><s:if test="year3.uploaded"><a href="#" onclick="openOsha(<s:property value="id"/>, 3); return false;">Download</a></s:if></th>
</tr>
</table>
