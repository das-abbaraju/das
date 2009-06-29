<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Email Subscriptions</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

<script type="text/javascript">
function save(id, status) {
	var sub = $('subscription'+id+'_'+status).innerHTML;
	var timeperiod;
	$A($$('.timeperiod'+id+'_'+status)).each(function (e) { if(e.checked) timeperiod = e.value;});
	var pars = "button=Save&id="+id+"&subscription="+sub+"&timePeriod="+timeperiod;
	alert(pars);
	var myAjax = new Ajax.Updater('', 'UserEmailSubscriptionAjax.action', {
		method: 'post', 
		parameters: pars
	});
	
}
</script>

</head>
<body>
<h1>Email Subscriptions</h1>
<s:include value="../users/userHeader.jsp"/>

<table class="report">
	<thead><tr>
		<th>Subscription</th>
		<th>Time Period</th>
		<th></th>
	</tr></thead>
	<tbody>
		<s:iterator value="eList" status="num">
			<tr id="test">
				<td><span id="subscription<s:property value="id"/>_<s:property value="#num.index"/>"><s:property value="subscription.description"/></span></td>
				<td><s:radio cssClass="timeperiod%{id}_%{#num.index}" list="subscriptionTimePeriods" theme="pics" value="%{timePeriod}"/></td>
				<td><div class="buttons">
					<button type="button" onclick="save(<s:property value="id"/>, <s:property value="#num.index"/>)">Save</button>
					</div>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

</body>
</html>
