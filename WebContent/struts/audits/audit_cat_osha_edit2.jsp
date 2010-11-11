<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set name="osha" value="[0]"></s:set>

<s:form action="OshaSave" method="POST" enctype="multipart/form-data" id="osSave">
	<s:hidden name="auditID"/>
	<s:hidden name="catDataID" value="%{categoryData.id}"/>
	<s:hidden name="categoryID" value="%{#category.id}"/>
	<s:hidden name="id"/>
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
<s:if test="!corporate">
<tr>
	<th class="label">Site Description</th>
	<td><s:textfield name="osha.description" value="%{description}" cssStyle="width: 95%" value="%{description}" maxlength="250"></s:textfield></td>
	<td>&nbsp;</td>
</tr>
</s:if>
<tr>
	<th class="label">
	<s:if test="manHours > 0">	
		<s:property value="getText('totalHoursWorked')"/>
	</s:if>
	<s:else>
		<span style="color: #272;font-weight: bold;"><s:property value="getText('totalHoursWorked')"/></span>
	</s:else>
	</th>
	<td>
		<s:textfield name='osha.manHours' key="{manHours}" value="%{getText('format.plain',{manHours})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label"><s:property value="getText('fatalities')"/></th>
	<td>
		<s:textfield name='osha.fatalities' key="{fatalities}" value="%{getText('format.plain',{fatalities})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label"><s:property value="getText('lostWorkDayCases.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.lostWorkCases' key="{lostWorkCases}" value="%{getText('format.plain',{lostWorkCases})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label"><s:property value="getText('lostWorkDays.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.lostWorkDays' key="{lostWorkDays}" value="%{getText('format.plain',{lostWorkDays})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label"><s:property value="getText('restrictedCases.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.restrictedWorkCases' key="{restrictedWorkCases}" value="%{getText('format.plain',{restrictedWorkCases})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<s:if test="#category.id in { 151, 158 }">
<tr>
	<th class="label"><s:property value="getText('modifiedWorkDay.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.modifiedWorkDay' key="{modifiedWorkDay}" value="%{getText('format.plain',{modifiedWorkDay})}" cssClass="osha"></s:textfield>
	</td>
</tr>
</s:if>
<tr>
	<th class="label"><s:property value="getText('injuryAndIllness.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.injuryIllnessCases' key="{injuryIllnessCases}" value="%{getText('format.plain',{injuryIllnessCases})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<s:if test="#category.id == 158">
<tr>
	<th class="label"><s:property value="getText('firstAidInjuries.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.firstAidInjuries' key="{firstAidInjuries}" value="%{getText('format.plain',{firstAidInjuries})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label"><s:property value="getText('vehicleIncidents.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.vehicleIncidents' key="{vehicleIncidents}" value="%{getText('format.plain',{vehicleIncidents})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label"><s:property value="getText('totalkmDriven.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.totalkmDriven' key="{totalkmDriven}" value="%{getText('format.plain',{totalkmDriven})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label"><s:property value="getText('cad7.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.cad7' key="{cad7}" value="%{getText('format.plain',{cad7})}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label"><s:property value="getText('neer.'.concat(type))"/></th>
	<td>
		<s:textfield name='osha.neer' key="{neer}" value="%{getText('format.plain',{neer})}" cssClass="osha"></s:textfield>
	</td>
</tr>
</s:if>
<s:if test="type.toString().equals('OSHA')">	
	<tr>
		<th class="label">
		<s:if test="fileUploaded">
			Upload <s:property value="conAudit.auditFor"/> <s:property value="type"/> Log File(.pdf, .doc, .txt, .xls or .jpg)
		</s:if>
		<s:else>
			<span style="color: #272;font-weight: bold;">Upload <s:property value="conAudit.auditFor"/> <s:property value="type"/> Log File(.pdf, .doc, .txt, .xls or .jpg)</span>
		</s:else></th>
		<td colspan="3">
		<s:if test="fileUploaded">
			<a href="#" onclick="openOsha(<s:property value="id"/>); return false;">View File</a>
		</s:if>
		<s:if test="categoryData.id > 0">
			<s:file name="uploadFile" size="10"></s:file></s:if>
		</td>
	</tr>
</s:if>
</tbody>
<tfoot>
<s:if test="categoryData.id > 0">
	<tr>
		<th colspan="2" style="font-size: 14px;">
			<input class="buttonOsha picsbutton positive" type="button" value="Save This Location" >
			<s:if test="(!corporate || permissions.admin) && categoryData.id > 0">
					<input type="button" class="buttonOsha picsbutton negative" value="Delete""/>
			</s:if>
		</th>
	</tr>
</s:if>
</tfoot>
</table>
</s:form>
<s:if test="permissions.contractor && #category.id in {151, 157}">
	<table>
		<s:if test="#category.id == 151">
		<tr>
			<td></td>
			<td class="redMain">Please upload scanned .pdf <s:property value="type"/> Log Files. If you are unable to do so, you may mail or fax them to us and we can scan them for you.
			</td>
		</tr>
		</s:if>
		<tr>
			<td class="redMain">Notes: </td>
			<td><span style="font-size: 11px;color:#003768;">(1)   Data should be for the entire company. Facilities may request additional regional statistics later.</span></td>
		</tr>
		<s:if test="#category.id == 151">
		<tr>
			<td></td>
			<td><span style="font-size: 11px;color:#003768;">(2)   If your company is not required to maintain <s:property value="type"/> 300 forms, please provide information from your
			Worker's Compensation insurance carrier itemizing all claims for the last three years.</span> 
			</td>
		</tr>
		<tr>
			<td></td>
			<td><span style="font-size: 11px;color:#003768;">(3)   If Injuries are reported you will be required to submit both the 300 and 300a forms as one file.</span> </td>
		</tr>
		</s:if>
	</table>
</s:if>