<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<td>&nbsp;</td>
		<th>CronJobs</th>
		<th>avgCronTime</th>
		<th>Contractors</th>
		<th>min</th>
		<th>max</th>
		<th>avg</th>
		</tr>
	</thead>
	<s:iterator value="cronPeriods">
		
		<s:set name="thisPeriod" value="top"/>
		<s:set name="theseMetrics" value="metricsAggregator.getMetrics(#attr.thisPeriod)"/>
		<tr>
			<td><s:property value="#attr.thisPeriod.name()"/></td>
			<td><s:property value="#attr.theseMetrics.cronJobs"/></td>
			<td><s:property value="#attr.theseMetrics.averageCronTime"/></td>
			<td><s:property value="#attr.theseMetrics.totalContractors" /></td>
			<td><s:property value="#attr.theseMetrics.minContractorTime"/></td>
			<td><s:property value="#attr.theseMetrics.maxContractorTime" /></td>
			<td><s:property value="#attr.theseMetrics.averageContractorTime"/></td>
		</tr>
	</s:iterator>
</table>
