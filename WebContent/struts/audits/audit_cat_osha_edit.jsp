<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="permissions.admin || permissions.contractor">
	<span class="redMain">You must input at least your corporate statistics. To further assist your clients, please
	enter additional locations that you maintain OSHA/MSHA logs for that may be needed by your clients.<br/>
	</span>
</s:if>
<s:if test="permissions.contractor">
<span style="font-size: 12px;color:#003768;">Provide the following numbers (excluding subcontractors) using your OSHA/MSHA 300 Forms from the past 3 years:</span>
</s:if>
<s:form action="OshaSave" method="POST" enctype="multipart/form-data">
	<s:hidden name="conID" value="%{conAudit.contractorAccount.id}"></s:hidden>
	<s:hidden name="auditID"></s:hidden>
	<s:hidden name="catDataID"></s:hidden>
	<s:hidden name="oshaID" value="%{id}"></s:hidden>
<table class="osha">
<thead>
<tr>
	<td>
		<s:select name="%{type}" headerValue="-Type-" headerKey="1" list="#{'OSHA':'OSHA','MSHA':'MSHA'}" value="%{type}" cssClass="forms"/>
	    Location - <s:select name="%{location}" list="#{'Corporate':'Corporate','Division':'Division','Region':'Region','Site':'Site'}" value="%{location}" cssClass="forms"/>
	    &nbsp;<s:textfield name="description" cssClass="osha" size="10" />&nbsp;&nbsp;
		<s:submit name="submit" value="Delete" onclick="return confirm('Are you sure you want to delete this location? This action cannot be undone.');"></s:submit>
	</td>
</tr>
<tr>
	<th>Year</th>
	<th class="center">2007</th>
	<th class="center">2006</th>
	<th class="center">2005</th>
</tr>
</thead>
<tbody>
<tr>
	<th class="label">Were you exempt from submitting <s:property value="type"/> Logs? &nbsp;&nbsp;&nbsp;&nbsp;
		<a href="#" onClick="window.open('reasons.html','name','scrollbars=1,resizable=1,width=800,height=600'); return false;">Valid exemptions</a>
	</th>
	<td><s:radio list="#{'Yes':'Yes','No':'No'}" name="year1.na" ></s:radio> </td>
	<td><s:radio list="#{'Yes':'Yes','No':'No'}" value="year2.na" name="exempt2_%{id}" onchange=""></s:radio> </td>
	<td><s:radio list="#{'Yes':'Yes','No':'No'}" value="year3.na" name="exempt3_%{id}" onchange=""></s:radio> </td>
</tr>
<s:if test="!corporate">
<tr>
	<th class="label">Site Description</th>
	<td colspan="3"><s:textfield name="description" cssStyle="width: 100%"></s:textfield></td>
</tr>
</s:if>
<tr>
	<th class="label">Total Man Hours Worked</th>
	<td><s:textfield name="year1.manHours" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year2.manHours" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year3.manHours" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Number of Fatalities</th>
	<td><s:textfield name="year1.fatalities" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year2.fatalities" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year3.fatalities" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Number of Lost Workday Cases - Has lost days AND is <s:property value="descriptionOsMs"/></th>
	<td><s:textfield name="year1.lostWorkCases" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year2.lostWorkCases" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year3.lostWorkCases" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Number of Lost Workdays - All lost workdays (regardless of restricted days) AND is <s:property value="descriptionOsMs"/></th>
	<td><s:textfield name="year1.lostWorkDays" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year2.lostWorkDays" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year3.lostWorkDays" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Injury & Illnesses Medical Cases - No lost OR restricted days AND is <s:property value="descriptionOsMs"/> (non-fatal)</th>
	<td><s:textfield name="year1.injuryIllnessCases" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year2.injuryIllnessCases" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year3.injuryIllnessCases" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Restricted Cases - Has restricted days AND no lost days AND is <s:property value="descriptionOsMs"/></th>
	<td><s:textfield name="year1.restrictedWorkCases" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year2.restrictedWorkCases" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year3.restrictedWorkCases" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Total <s:property value="descriptionOsMs"/> Injuries and Illnesses</th>
	<td><s:textfield name="year1.recordableTotal" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year2.recordableTotal" cssClass="osha"></s:textfield></td>
	<td><s:textfield name="year3.recordableTotal" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Upload 2007 <s:property value="type"/> Log File(.pdf, .doc, .txt, .xls or .jpg)</th>
	<td colspan="3"><s:file name="uploadFile1" size="10"></s:file></td>
</tr>
<tr>
	<th class="label">Upload 2006 <s:property value="type"/> Log File(.pdf, .doc, .txt, .xls or .jpg)</th>
	<td colspan="3"><s:file name="uploadFile2" size="10"></s:file></td>
</tr>
<tr>
	<th class="label">Upload 2005 <s:property value="type"/> Log File(.pdf, .doc, .txt, .xls or .jpg)</th>
	<td colspan="3"><s:file name="uploadFile3" size="10"></s:file></td>
</tr>
</tbody>
<tfoot>
<tr>
	<th colspan="4"><s:submit name="submit" value="Save This Location" cssStyle="padding: 6px;"></s:submit></th>
</tr>
</tfoot>
</table>
</s:form>
<s:if test="permissions.contractor">
	<table>
		<tr>
			<td></td>
			<td class="redMain">Please upload scanned .pdf <s:property value="type"/> Log Files. If you are unable to do so, you may mail or fax them to us and we can scan them for you.
			</td>
		</tr>
		<tr>
			<td class="redMain">Notes: </td>
			<td><span style="font-size: 11px;color:#003768;">(1)   Data should be for the entire company. Facilities may request additional regional statistics later.</span></td>
		</tr>
		<tr>
			<td></td>
			<td><span style="font-size: 11px;color:#003768;">(2)   If your company is not required to maintain <s:property value="type"/> 300 forms, please provide information from your
			Worker's Compensation insurance carrier itemizing all claims for the last three years.</span> 
			</td>
		</tr>
	</table>
</s:if>