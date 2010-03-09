<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Operator Flag/Waiting On Differences</title>
<s:include value="../reports/reportHeader.jsp" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function isOk(conid,opid, flag) {
		$.ajax({
			url: 'ContractorFlagDifference.action',
			data: {button: 'delete', conID: conid, opID: opid, flag: flag},
			success: function() {
				$('#tr'+conid+'_'+opid).fadeOut();
			}
		});
}
</script>
</head>
<body>
<h1>Contractor Operator Flag/Waiting On Differences</h1>

<table class="report">
	<thead>
		<tr>
			<td>Contractor</td>
			<td>Operator</td>
			<td>New Flag Color</td>
			<td>Old Flag Color</td>
			<td>New WaitingOn</td>
			<td>Old WaitingOn</td>
			<td></td>
		</tr>
	</thead>
	<s:iterator value="data">
		<tr id="tr<s:property value="get('conID')"/>_<s:property value="get('opID')" />">
			<td>
				<s:property value="get('ContractorName')" />
			</td>
			<td>
				<s:property value="get('OperatorName')" />
			</td>
			<td <s:if test="get('oldColor') != get('newColor')"> style="background-color: YELLOW" </s:if>>
				<a href="ContractorFlag.action?id=<s:property value="get('conID')" />&opID=<s:property value="get('opID')" />">
					<s:property value="get('newColor')" />			
				</a>
			</td>
			<td <s:if test="get('oldColor') != get('newColor')"> style="background-color: YELLOW" </s:if>>
				<a href="http://www.picsauditing.com/app/ContractorFlag.action?id=<s:property value="get('conID')" />&opID=<s:property value="get('opID')" />">
					<s:property value="get('oldColor')" />			
				</a>
			</td>
			<td <s:if test="get('oldwaitingOn') != get('newwaitingon')"> style="background-color: YELLOW" </s:if>>
				<a href="ContractorFlag.action?id=<s:property value="get('conID')" />&opID=<s:property value="get('opID')" />">
					<s:property value="@com.picsauditing.jpa.entities.WaitingOn@valueOf(get('newwaitingon'))" />			
				</a>
			</td>
			<td <s:if test="get('oldwaitingOn') != get('newwaitingon')"> style="background-color: YELLOW" </s:if>>
				<a href="http://www.picsauditing.com/app/ContractorFlag.action?id=<s:property value="get('conID')" />&opID=<s:property value="get('opID')" />">
					<s:property value="@com.picsauditing.jpa.entities.WaitingOn@valueOf(get('oldwaitingOn'))" />
				</a>
			</td>
			<td><a href="#" onclick="isOk(<s:property value="get('conID')" />,<s:property value="get('opID')" />, '<s:property value="get('newColor')" />'); return false;"><img src="images/cross.png" /></a></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
