<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set name="osha" value="[0]"></s:set>

<s:form action="OshaSave" method="POST" enctype="multipart/form-data">
	<s:hidden name="auditID"></s:hidden>
	<s:hidden name="catDataID"></s:hidden>
	<s:hidden name="id"></s:hidden>
<table class="osha">
<thead>
<tr class="location">
	<th>
	    <label>Location:</label>
	    <s:if test="location == 'Corporate'">
	    	<s:property value="location"/>
	    </s:if>
	    <s:else>
	    	<s:select name="location" list="#{'Division':'Division','Region':'Region','Site':'Site'}" value="%{location}" cssClass="forms"/>
		</s:else>
	</th>
	<td class="location" style="text-align: center;"><s:property value="conAudit.auditFor"/></td>
</tr>
</thead>
<tbody>
<s:if test="category.id in { 151, 157 }">
	<tr>
		<th class="label">Were you exempt from submitting <s:property value="type"/> Logs? &nbsp;&nbsp;&nbsp;&nbsp;
			<a href="#" onClick="window.open('reasons.html','name','scrollbars=1,resizable=1,width=800,height=600'); return false;">Valid exemptions</a>
		</th>
		<td><nobr><s:radio list="#{false:'Yes',true:'No'}" name="osha.applicable" value="%{applicable}"></s:radio></nobr></td>
	</tr>
</s:if>
<s:if test="!corporate">
<tr>
	<th class="label">Site Description</th>
	<td><s:textfield name="osha.description" value="%{description}" cssStyle="width: 95%" value="%{description}" maxlength="250"></s:textfield></td>
	<td>&nbsp;</td>
</tr>
</s:if>
<tr>
	<th class="label"><s:property value="getText('totalHoursWorked')"/></th>
	<td><s:textfield name="osha.manHours" value="%{manHours}" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label"><s:property value="getText('fatalities')"/></th>
	<td><s:textfield name="osha.fatalities" value="%{fatalities}" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label"><s:property value="getText('lostWorkDayCases.'.concat(type))"/></th>
	<td><s:textfield name="osha.lostWorkCases" value="%{lostWorkCases}" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label"><s:property value="getText('lostWorkDays.'.concat(type))"/></th>
	<td><s:textfield name="osha.lostWorkDays" value="%{lostWorkDays}" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label"><s:property value="getText('injuryAndIllness.'.concat(type))"/></th>
	<td><s:textfield name="osha.injuryIllnessCases" value="%{injuryIllnessCases}" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label"><s:property value="getText('restrictedCases.'.concat(type))"/></th>
	<td><s:textfield name="osha.restrictedWorkCases" value="%{restrictedWorkCases}" cssClass="osha"></s:textfield></td>
</tr>
<tr>
	<th class="label"><s:property value="getText('totalInjuriesAndIllnesses.'.concat(type))"/></th>
	<td><s:textfield name="osha.recordableTotal" value="%{recordableTotal}" cssClass="osha"></s:textfield></td>
</tr>
<s:if test="category.id == 158">
<tr>
	<th class="label"><s:property value="getText('cad7.'.concat(type))"/></th>
	<td><s:textfield name="osha.cad7" value="%{cad7}" cssClass="osha"/></td>
	<td>&nbsp;</td>
	<s:if test="corporate"><td>&nbsp;</td></s:if>
</tr>
<tr>
	<th class="label"><s:property value="getText('neer.'.concat(type))"/></th>
	<td><s:textfield name="osha.neer" value="%{neer}" cssClass="osha"/></td>
	<td>&nbsp;</td>
	<s:if test="corporate"><td>&nbsp;</td></s:if>
</tr>
</s:if>
<tr>
	<th class="label">Upload <s:property value="conAudit.auditFor"/> <s:property value="type"/> Log File(.pdf, .doc, .txt, .xls or .jpg)</th>
	<td colspan="3">
	<s:if test="fileUploaded">
		<a href="#" onclick="openOsha(<s:property value="id"/>, 1); return false;">View File</a>
	</s:if>
	<s:if test="catDataID > 0">
		<s:file name="uploadFile" size="10"></s:file></s:if></td>
</tr>
</tbody>
<tfoot>
<s:if test="catDataID > 0">
	<tr>
		<th colspan="2">
			<s:submit name="button" value="Save This Location" cssStyle="padding: 5px;"></s:submit>
			<s:if test="(!corporate || permissions.admin) && catDataID > 0">
					<s:submit name="button" value="Delete" onclick="return confirm('Are you sure you want to delete this location? This action cannot be undone.');" cssStyle="padding: 5px;"></s:submit>
			</s:if>
		</th>
	</tr>
</s:if>
</tfoot>
</table>
</s:form>
<s:if test="permissions.contractor && category.id in {151,157}">
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