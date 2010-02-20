<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Operator Flag Matrix</title>
<s:include value="../jquery.jsp"/>
<style>
.Red {
	color: #CC0000;
}
.Amber {
	color: #FFCC33;
}
.Green {
	color: #339900;
}
</style>
</head>
<body>

<s:include value="reportHeader.jsp"/>


<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<s:iterator value="flagCriteria">
				<th title="<s:property value="description"/>"><s:property value="label"/></th>
			</s:iterator>
		</tr>
	</thead>
	<s:iterator value="contractorCriteria" id="conmap">
	<tr>
		<td>
			<s:property value="#conmap.key.name"/>
		</td>
		<s:iterator value="flagCriteria" id="criteria">
			<td>
				<s:iterator value="#conmap.value.get(#criteria)">
					<span class="<s:property value="value.flag"/>"><s:property value="key.answer"/></span>
				</s:iterator>
			</td>
		</s:iterator>
	</tr>
	</s:iterator>
</table>

</body>
</html>