<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title><s:text name="ProfileEdit.title"></s:text></title>

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp" />

<script type="text/javascript">
    function save(subscription, id, timeperiod) {
        var data = {
            'eu.id' : id,
            'eu.subscription' : subscription,
            'addSubscription' : $('#add' + subscription).is(':checked'),
            goEmailSub : false
        };

        if (timeperiod != null) {
            data['eu.timePeriod'] = $(timeperiod).val();
        }
        var divName = 'td' + subscription;
        startThinking({
            'div' : divName
        });

        $('#' + divName).load('UserEmailSubscriptionSaveAjax.action', data,
                function() {
                    $(this).effect('highlight', {
                        color : '#FFFF11'
                    }, 1000)
                });

    }
</script>

</head>
<body>
	<h1>
		<s:text name="PasswordEdit.title" />
		:
		<s:if test="user.id > 0">
			<s:property value="user.username" />
		</s:if>
		<s:else>
			<s:property value="u.username" />
		</s:else>
	</h1>
	<s:if test="account.admin">PICS</s:if>

	&gt;


	<s:if test="source=='manage'">
		<a href="UsersManage.action?account=<s:property value="account.id"/>">
			<s:text name="UsersManage.title" />
		</a>
		&gt; <a	href="UsersManage.action?account=<s:property value="user.account"/>&user=<s:property value="user.id"/>&isActive=<s:property value="user.isActive"/>&isGroup=<s:property value="user.isGroup"/>">
				<s:property value="user.name" />
			</a>
		&gt; <a href="ChangePassword.action?source=<s:property value="source" />&user=<s:property value="user.id" />">
				<s:text name="button.password" />
			</a>
	</s:if>
	<s:else>
		<a href="ProfileEdit.action"/>
			<s:text name="ProfileEdit.title" />
		</a>
		&gt; <a href="ChangePassword.action?source=<s:property value="source" />&user=<s:property value="user.id" /> ">
			<s:text name="button.password" />
		</a>
	</s:else>
	<s:include value="../actionMessages.jsp"></s:include>

	<div id="tab_password">
		<s:include value="user_change_password.jsp" />
	</div>




</body>
</html>