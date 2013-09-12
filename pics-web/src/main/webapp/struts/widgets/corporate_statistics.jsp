<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<p><label>Number of Contractors:</label> <s:property value="totalContractorCount" /></p>
<p><label>Number of Active Contractors:</label> <s:property value="activeContractorCount" /></p>
<p><label>Number of Active Operators:</label> <s:property value="operatorCount" /><br />
<p><label>Number of Active Corporate:</label> <s:property value="corporateCount" /><br />
<p><label>Number of Active Users:</label> <s:property value="userCount" /></p>
<s:if test="permissions.hasGroup(981) || permissions.hasGroup(10801)">
	<pics:permission perm="DevelopmentEnvironment" negativeCheck="true">
		<br />
		<p>
			<div id="corpStatAM1">
				Total number of contractors registered for all your accounts
			</div>
			<label>Number of Your Contractors:</label>
			<s:property value="amContractorCount" />
			<a href="#" onclick="return false;" class="help cluetip" rel="#corpStatAM1" title="Number of Your Contractors"></a>
			<script type="text/javascript">
			$('.cluetip').cluetip({
				closeText: "<img src='images/cross.png' width='16' height='16'>",
				arrows: true,
				cluetipClass: 'jtip',
				local: true,
				clickThrough: false
			});
			</script>
		</p>
		<p><label>Number of Your Accounts:</label> <s:property value="amAccountCount" /><br />
		<p><label>Number of Your Corporate Accounts:</label> <s:property value="amCorporateCount" /><br />
		<p><label>Number of Your Users:</label> <s:property value="amUserCount" /></p>
	</pics:permission>
</s:if>