<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Operator Flag Matrix</title>
<s:include value="reportHeader.jsp"/>
<style>
.Red {
	color: #CC0000 !important;
}
.Amber {
	color: #FFCC33 !important;
}
.Green {
	color: #339900 !important;
}
.flag_override {
	cursor: pointer;
}
</style>
</head>
<body>

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
			<a href="ContractorFlag.action?id=<s:property value="#conmap.key.id"/>&opID=<s:property value="id"/>" class="<s:property value="overall.get(#conmap.key)"/>">
				<s:property value="overall.get(#conmap.key).smallIcon" escape="false"/>
				<s:property value="#conmap.key.name"/>
			</a>
		</td>
		<s:iterator value="flagCriteria" id="criteria">
			<td>
				<s:iterator value="#conmap.value.get(#criteria)">
					<s:if test="#criteria.dataType == 'boolean'">
						<s:property value="value.flag.smallIcon" escape="false"/>
					</s:if>
					<s:elseif test="dataType == 'number'">
						<span class="<s:property value="value.flag"/>"><s:property value="format(key.answer)"/></span>
					</s:elseif>
					<s:else>
						<span class="<s:property value="value.flag"/>"><s:property value="key.answer"/></span>
					</s:else>
				</s:iterator>
			</td>
		</s:iterator>
	</tr>
	</s:iterator>
</table>

</body>
</html>