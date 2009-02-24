<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th>CronJobs</th>
		<th>avgCronTime</th>
		<th>Contractors</th>
		<th>min</th>
		<th>max</th>
		<th>avg</th>
		</tr>
	</thead>
	<s:iterator value="cronMList">
		<tr>
			<td><s:property value="cronJobs"/></td>
			<td><s:property value="averageCronTime"/></td>
			<td><s:property value="totalContractors" /></td>
			<td><s:property value="minContractorTime"/></td>
			<td><s:property value="maxContractorTime" /></td>
			<td><s:property value="averageContractorTime"/></td>
		</tr>
	</s:iterator>
</table>
