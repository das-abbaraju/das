<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />

<div id="thinking_sites" class="right"></div>

<table class="report"">
	<thead>
		<tr>
			<th>Assigned Site</th>
			<th>Project</th>
			<th>Since</th>
			<th>Orientation</th>
			<th>Edit</th>
		</tr>
	</thead>
	<s:iterator value="employee.employeeSites" id="site" status="stat">
		<s:if test="#site.current">
			<tr>
				<td title=""><s:property value="operator.name" /></td>
				<td>
					<s:if test="jobSite.id > 0"><s:property value="#site.jobSite.label" /></s:if>
					<s:else>Unspecified</s:else> 					
				</td>
				<td><s:property value="effectiveDate"/></td>
				<td>
					<s:property value="orientationDate"/>
				</td>
				<td><a href="#"
					onclick="$('#'+<s:property value="%{#stat.count}" />+'_under').toggle(); return false;"
					class="edit"></a>
				</td>
			</tr>
			<tr id="<s:property value="#stat.count" />_under" style="display: none;">
				<td colspan="5">
				<div id="siteEditBox">
					<form id="siteForm_<s:property value="#site.id" />" >
						<input type="hidden" value="<s:property value="#site.id" />" name="childID" />
						<div style="float:left; width: 50%;">
							Start Date: <s:textfield id="sDate_%{#site.id}" name="effectiveDate" value="%{maskDateFormat(effectiveDate)}" size="10" /><br />
							End Date: <s:textfield id="eDate_%{#site.id}" name="expirationDate" value="%{maskDateFormat(expirationDate)}" size="10" /><br />
							<input type="submit" value="Save" onclick="editAssignedSites(<s:property value="#site.id" />); return false;" class="picsbutton positive"/>
						</div>
						<div style="float:right; width: 50%;">
							Site Orientation: <s:textfield id="oDate_%{#site.id}" name="orientationDate" value="%{maskDateFormat(orientationDate)}" size="10" /><br />
							Expires in: <s:select label="Expires" id="expires_%{#site.id}"
											list="#{1:36, 2:24, 3:12, 4:6}"
											value="monthsToExp" /> months<br />
							<input type="submit" value="Remove" onclick="removeJobSite(<s:property value="#site.id" />); return false;" class="picsbutton negative" />
						</div>
					</form>
				</div>
				</td>
			</tr>
		</s:if>
	</s:iterator>
	<tr>
		<td colspan="5"><s:if test="operators.size > 0">
			<s:select
				onchange="addJobSite(this.value);"
				list="operators" name="operator.id" listKey="id" listValue="name"
				headerKey="" headerValue=" - Add Job Site - " id="operator" />
		</s:if><s:else>
			<h5>This employee has been assigned to all available sites.</h5>
		</s:else></td>
	</tr>
</table>
<s:if test="employee.prevAssigned">
	<h3>Previously Assigned Sites</h3>
	<table class="report"">
		<thead>
			<tr>
				<th>Assigned Site</th>
				<th>Project</th>
				<th>Ended On</th>
			</tr>
		</thead>
		<s:iterator value="employee.employeeSites" id="site" status="stat">
			<s:if test="!#site.current"> 
				<tr>
					<td title=""><s:property value="operator.name" /></td>
					<td>
						<s:if test="jobSite.id > 0"><s:property value="#site.jobSite.label" /></s:if>
						<s:else>Unspecified</s:else> 					
					</td>
					<td><s:property value="expirationDate"/></td>
				</tr>
			</s:if>
		</s:iterator>
	</table>
</s:if>

