<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
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
	<td><s:textfield name="osha.description" value="%{description}" cssStyle="width: 95%" maxlength="250"></s:textfield></td>
</tr>
</s:if>
<tr>
	<th class="label">
	<s:if test="manHours > 0">
		<s:text name="totalHoursWorked"/>
	</s:if>
	<s:else>
		<span style="color: #272;font-weight: bold;"><s:text name="AnnualUpdate.totalHoursWorked"/></span>
	</s:else>
	</th>
	<td>
		<s:textfield name="osha.manHours" value="%{getTextParameterized('format.plain',manHours)}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label">
		<s:text name="AnnualUpdate.fatalities"/>
	</th>
	<td>
		<s:textfield name="osha.fatalities" label="%{getTextParameterized('format.plain',fatalities)}" value="%{fatalities}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label">
		<s:text name="%{type}.lostWorkDayCases"/>
	</th>
	<td>
		<s:textfield name="osha.lostWorkCases" value="%{getTextParameterized('format.plain',lostWorkCases)}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label">
		<s:text name="%{type}.lostWorkDays"/>
	</th>
	<td>
		<s:textfield name="osha.lostWorkDays" value="%{getTextParameterized('format.plain',lostWorkDays)}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label">
		<s:text name="%{type}.restrictedCases"/>
	</th>
	<td>
		<s:textfield name='osha.restrictedWorkCases' value="%{getTextParameterized('format.plain',restrictedWorkCases)}" cssClass="osha"></s:textfield>
	</td>
</tr>
<s:if test="#category.id in { 151, 158 }">
<tr>
	<th class="label">
		<s:text name="%{type}.modifiedWorkDay"/>
	</th>
	<td>
		<s:textfield name="osha.modifiedWorkDay" value="%{getTextParameterized('format.plain',modifiedWorkDay)}" cssClass="osha"></s:textfield>
	</td>
</tr>
</s:if>
<tr>
	<th class="label">
		<s:text name="%{type}.injuryAndIllness"/>
	</th>
	<td>
		<s:textfield name="osha.injuryIllnessCases" value="%{getTextParameterized('format.plain',injuryIllnessCases)}" cssClass="osha"></s:textfield>
	</td>
</tr>
<s:if test="#category.id == 158">
<tr>
	<th class="label">
		<s:text name="%{type}.firstAidInjuries"/>
	</th>
	<td>
		<s:textfield name="osha.firstAidInjuries" value="%{getTextParameterized('format.plain',firstAidInjuries)}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label">
		<s:text name="%{type}.vehicleIncidents"/>
	</th>
	<td>
		<s:textfield name="osha.vehicleIncidents" value="%{getTextParameterized('format.plain',vehicleIncidents)}" cssClass="osha"></s:textfield>
	</td>
</tr>
<tr>
	<th class="label">
		<s:text name="%{type}.totalkmDriven"/>
	</th>
	<td>
		<s:textfield name="osha.totalkmDriven" value="%{getTextParameterized('format.plain',totalkmDriven)}" cssClass="osha"></s:textfield>
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
		<td colspan="2">
		<s:if test="fileUploaded">
			<a href="DownloadOsha.action?id=<s:property value="id"/>" target="_BLANK">View File</a>
		</s:if>
		<s:if test="categoryData.id > 0">
			<s:file name="uploadFile" size="10"/></s:if>
		</td>
	</tr>
</s:if>
</tbody>
<tfoot>
<s:if test="categoryData.id > 0">
	<tr>
		<th colspan="2" style="font-size: 14px;">
			<input class="buttonOsha picsbutton positive" type="submit" name="button" value="Save This Location" >
			<s:if test="(!corporate || permissions.admin) && categoryData.id > 0">
					<input name="button" type="submit" onclick="return confirm('Are you sure you want to delete this location? This action cannot be undone.')" class="buttonOsha picsbutton negative" value="Delete"/>
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