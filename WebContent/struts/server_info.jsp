<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/reports.css" />

<h2 style="padding-bottom:15px;">Operating System Info</h2>
<table class="report">
<thead>
<tr>
	<td>Name</td>
	<td>Parameter</td>
</tr>
</thead>

<tr>
	<td>
		Name&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td>
		<s:property value="os.name"/>
	</td>
</tr>

<tr>
	<td>
		Architecture&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td>
		<s:property value="os.arch"/>
	</td>
</tr>

<tr>
	<td>
		Version&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td>
		<s:property value="os.version"/>
	</td>
</tr>

<tr>
	<td>
		Available Processors&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td>
		<s:property value="os.availableProcessors"/>
	</td>
</tr>

<tr>
	<td>
		Load Average&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td>
		<s:property value="os.systemLoadAverage"/>
	</td>
</tr>

<tr>
	<td>
		Heap Memory Usage&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td>
		<s:property value="heapMemoryUsage/1000000"/>Mb
	</td>
</tr>

<tr>
	<td>
		Non-Heap Memory Usage&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td>
		<s:property value="nonHeapMemoryUsage/1000000"/>Mb
	</td>
</tr>

<tr>
	<td>
		Total Memory Usage&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td>
		<s:property value="totalMemoryUsage/1000000"/>Mb
	</td>
</tr>

</table>