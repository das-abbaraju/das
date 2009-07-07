<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Edit Profile</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

<script type="text/javascript">
	function checkUsername(username) {
		$('username_status').innerHTML = '<img src="images/ajax_process.gif" width="16" height="16" /> checking availability of username...';
		pars = 'userID=<s:property value="u.id"/>&username=' + username;
		var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {
			method :'get',
			parameters :pars
		});
	}

	function save(id, status) {
		var pars = $('eu'+id+'_'+status).serialize();

		var divName = 'td'+id+'_'+status;
		startThinking({'div':divName, 'message':'Saving email/Subscription data'});

		var myAjax = new Ajax.Updater(divName, 'UserEmailSubscriptionSaveAjax.action', {
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) {
			new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
	}
</script>

</head>
<body>
<h1>Edit Profile</h1>

<s:include value="userHeader.jsp" />

<table style="width: 100%">
	<tr>
		<td><s:form cssStyle="width: 500px">
			<s:hidden name="u.id" />
			<fieldset class="form"><legend><span>Profile</span></legend>
			<ol>
				<li><label>Assigned to account:</label> <s:property value="u.account.name" /></li>
				<li><label for="u.name">Display name:</label> <s:textfield name="u.name" /></li>
				<li><label for="u.email">Email address:</label> <s:textfield name="u.email" size="30" /></li>
				<li><label for="u.phone">Phone:</label> <s:textfield name="u.phone" size="20" /></li>
				<li><label for="u.fax">Fax:</label> <s:textfield name="u.fax" size="20" /></li>
				<li><label>Created:</label> <s:date name="u.creationDate" /></li>
				<li><label>Last login:</label> <s:date name="u.lastLogin" /></li>
			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Username &amp; Password</span></legend>
			<ol>
				<li><label for="u.username">Username:</label> <s:textfield name="u.username"
					onchange="checkUsername(this.value);" />
				<div id="username_status">&nbsp;</div>
				</li>
				<li><label for="password1">Password:</label> <s:password name="password1" value="" /></li>
				<li><label for="password2">Confirm Password:</label> <s:password name="password2" value="" /></li>
			</ol>
			</fieldset>
			<fieldset class="form submit">
			<div>
			<button id="saveButton" class="picsbutton positive" value="Save Profile" name="button" type="submit">Save Profile</button>
			</div>
			</fieldset>
		</s:form></td>
		<td style="width: 20px;">&nbsp;</td>
		<td style="vertical-align:top;">
		<s:if test="switchTos.size > 0">
			<img src="images/beta.jpg" width="98" height="100" style="float: right;"
				title="This is a new feature. Please send us your feedback or suggestions." />
			<h3>Switch Accounts</h3>
			<table class="report">
				<thead>
					<tr>
						<td>Account</td>
						<td>User</td>
					</tr>
				</thead>
				<tbody>
					<tr><td>
						<a href="Login.action?button=switch&switchToUser=<s:property value="u.id" />"><s:property
						value="u.account.name" /></a>
					</td>
					<td>Primary</td>
					</tr>
					<s:iterator value="switchTos">
					<tr>
						<td><a href="Login.action?button=switch&switchToUser=<s:property value="switchTo.id" />"><s:property
						value="switchTo.account.name" /></a></td>
						<td><s:property value="switchTo.name" /></td>
					</tr>
				</s:iterator>
				</tbody>
			</table>
		</s:if>
	</td>
	</tr>
</table>

<div class="clear"></div>

<table class="report">
	<thead><tr>
		<th>Subscription</th>
		<th>Time Period</th>
		<th></th>
	</tr></thead>
	<tbody>
		<s:iterator value="eList" status="num">
			<tr>
				<td><s:property value="subscription.description"/></td>
				<td id="td<s:property value="id" />_<s:property value="#num.index" />">
					<s:include value="../mail/user_email_subscription.jsp"></s:include>
				</td>
				<td>
				<div class="buttons">
					<button type="button" onclick="save('<s:property value="id"/>', '<s:property value="#num.index" />')">Save</button>
					</div>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

</body>
</html>
