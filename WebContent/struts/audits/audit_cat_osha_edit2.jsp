<%@ taglib prefix="s" uri="/struts-tags"%>
<s:form action="OshaSave" method="POST" enctype="multipart/form-data">
	<s:hidden name="conID" value="%{conAudit.contractorAccount.id}"></s:hidden>
	<s:hidden name="auditID"></s:hidden>
	<s:hidden name="catDataID"></s:hidden>
	<s:hidden name="oshaID" value="%{id}"></s:hidden>
<table class="osha">
<thead>
<tr class="location">
	<th>
		<s:select name="osha.type" list="#{'OSHA':'OSHA','MSHA':'MSHA'}" value="%{type}" cssClass="forms"/>
	    Location - 
	    <s:select name="osha.location" list="#{'Corporate':'Corporate','Division':'Division','Region':'Region','Site':'Site'}" value="%{location}" cssClass="forms"/>
	</th>
	<td class="location" style="text-align: center;"><s:property value="conAudit.auditFor"/></td>
</tr>
</thead>
<tbody>
<tr>
	<th class="label">Were you exempt from submitting <s:property value="type"/> Logs? &nbsp;&nbsp;&nbsp;&nbsp;
		<a href="#" onClick="window.open('reasons.html','name','scrollbars=1,resizable=1,width=800,height=600'); return false;">Valid exemptions</a>
	</th>
	<td><nobr><s:radio list="#{false:'Yes',true:'No'}" name="applicable"></s:radio></nobr></td>
</tr>
<s:if test="!corporate">
<tr>
	<th class="label">Site Description</th>
	<td><s:textfield name="osha.description" cssStyle="width: 100%" value="%{description}"></s:textfield></td>
</tr>
</s:if>
<tr>
	<th class="label">Total Man Hours Worked</th>
	<td><s:textfield name="manHours" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Number of Fatalities</th>
	<td><s:textfield name="fatalities" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Number of Lost Workday Cases - Has lost days AND is <s:property value="descriptionOsMs"/></th>
	<td><s:textfield name="lostWorkCases" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Number of Lost Workdays - All lost workdays (regardless of restricted days) AND is <s:property value="descriptionOsMs"/></th>
	<td><s:textfield name="lostWorkDays" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Injury & Illnesses Medical Cases - No lost OR restricted days AND is <s:property value="descriptionOsMs"/> (non-fatal)</th>
	<td><s:textfield name="injuryIllnessCases" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Restricted Cases - Has restricted days AND no lost days AND is <s:property value="descriptionOsMs"/></th>
	<td><s:textfield name="restrictedWorkCases" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Total <s:property value="descriptionOsMs"/> Injuries and Illnesses</th>
	<td><s:textfield name="recordableTotal" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label">Upload <s:property value="conAudit.auditFor"/> <s:property value="type"/> Log File(.pdf, .doc, .txt, .xls or .jpg)</th>
	<td colspan="3">
		<s:if test="uploaded">
			<a href="#" onclick="openOsha(<s:property value="id"/>, 1); return false;">View File</a>
		</s:if>
		<s:if test="catDataID > 0">
			<s:file name="uploadFile1" size="10"></s:file></s:if></td>
</tr>
</tbody>
<tfoot>
<s:if test="catDataID > 0">
	<tr>
		<th colspan="2">
			<s:submit name="submit" value="Save This Location" cssStyle="padding: 5px;"></s:submit>
			<s:if test="(!corporate || permissions.admin) && catDataID > 0">
					<s:submit name="submit" value="Delete" onclick="return confirm('Are you sure you want to delete this location? This action cannot be undone.');" cssStyle="padding: 5px;"></s:submit>
			</s:if>
		</th>
	</tr>
</s:if>
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