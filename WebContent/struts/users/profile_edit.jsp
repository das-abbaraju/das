<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<title><s:text name="ProfileEdit.title"></s:text></title>

<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<script type="text/javascript" src="js/profile_edit.js?v=<s:property value="version"/>"></script>
<script type="text/javascript">
	var accountID = '<s:property value="account.id" />';
	var currentUserID = 0;

	<s:if test="user.id > 0">currentUserID = <s:property value="user.id"/>;</s:if>
</script>

<s:include value="../jquery.jsp" />

<script type="text/javascript">
	function checkUsername(username) {
		startThinking({div: 'username_status', message: translate('JS.ProfileEdit.message.CheckingAvailabilityOfUsername')});
		var data = {userID: <s:property value="u.id"/>, username: username};
		$('#username_status').load('user_ajax.jsp', data);
	}

	function save(subscription, id, timeperiod) {
	    var data = {
            'eu.id': id,
            'eu.subscription': subscription,
            'addSubscription': $('#add'+subscription).is(':checked'),
            goEmailSub: false
        };

		if(timeperiod != null) {
			data['eu.timePeriod']= $(timeperiod).val();
		}

	    var divName = 'td'+subscription;
		startThinking({'div':divName});

		$('#'+divName).load('UserEmailSubscriptionSaveAjax.action',data,function() {
				$(this).effect('highlight', {color: '#FFFF11'}, 1000)
			}
		);
	}

	function showTab(tabName) {
		$('#tab_profile').hide();
		$('#link_tab_profile').removeClass('current');
		$('#tab_permissions').hide();
		$('#link_tab_permissions').removeClass('current');

		<s:if test="eList.size > 0">
			$('#tab_subscriptions').hide();
			$('#link_tab_subscriptions').removeClass('current');
		</s:if>
		<s:if test="switchTos.size > 0">
			$('#tab_switch').hide();
			$('#link_tab_switch').removeClass('current');
		</s:if>

		$('#'+tabName).show();
		$('#link_'+tabName).addClass('current');
	}

	$(function () {
		$('#navlist').delegate('#link_tab_profile', 'click', function(e) {
			e.preventDefault();
			showTab('tab_profile');
		}).delegate('#link_tab_switch', 'click', function(e) {
			e.preventDefault();
			showTab('tab_switch');
		}).delegate('#link_tab_subscriptions', 'click', function(e) {
			e.preventDefault();
			showTab('tab_subscriptions');
		}).delegate('#link_tab_permissions', 'click', function(e) {
			e.preventDefault();
			showTab('tab_permissions');
		});

		$('#departmentSuggest').autocomplete('UserProfileAjax!department.action?user=<s:property value="user.id"/>').result(function(event, data){
			$('#departmentRole').val(data[3])
		});
	});
</script>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
	<h1>
		<s:text name="ProfileEdit.title" />
		:
		<s:property value="u.username" />
	</h1>

	<div id="internalnavcontainer">
		<ul id="navlist">
			<li>
				<a id="link_tab_profile" href="#" class="current">
					<s:text name="button.Edit" />
				</a>
			</li>
			<li>
				<a href="ChangePassword.action?source=profile&user=<s:property value="u.id"/>" id="profile_edit_changePassword"> 
					<s:text	name="button.password" />
				</a>
			</li>
			<s:if test="switchTos.size > 0">
				<li>
					<a id="link_tab_switch" href="#">
						<s:text name="ProfileEdit.label.SwitchAccounts" />
					</a>
				</li>
			</s:if>

			<s:if test="eList.size > 0">
				<li>
					<a id="link_tab_subscriptions" href="#">
						<s:text name="ProfileEdit.label.EmailSubscriptions" />
					</a>
				</li>
			</s:if>

			<s:if test="!permissions.contractor">
				<li><a id="link_tab_permissions" href="#">
						<s:text name="ProfileEdit.label.Permissions" />
					</a>
				</li>
			</s:if>
		</ul>
	</div>

	<s:include value="../actionMessages.jsp"></s:include>

	<s:if test="u.forcePasswordReset">
		<div class="alert">
			<s:text name="ProfileEdit.alert.ChangePasswordRequired" />
		</div>
	</s:if>

	<div id="tab_profile">
		<table style="width: 100%">
			<tr>
                <td style="width: 50%;">
                    <s:include value="_profile-edit-forms.jsp" />
                </td>

				<td style="width: 20px;">&nbsp;</td>

				<td style="vertical-align: top;">
                    <s:include value="_profile-edit-recent-logins.jsp" />
				</td>
			</tr>
		</table>
	</div>
	
	<s:if test="!permissions.contractor">
		<div id="tab_switch" style="display: none;">
			<s:include value="user_switch_accts.jsp" />
		</div>
	</s:if>

	<s:if test="!permissions.contractor">
        <s:include value="_profile-edit-non-contractor.jsp" />
	</s:if>

	<s:if test="goEmailSub">
		<script type="text/javascript">
			$(function() {
				showTab('tab_subscriptions');
			 });
			</script>
	</s:if>

	<div id="tab_subscriptions" style="display: none;">
		<s:iterator value="eList" status="stat">
			<div id="td<s:property value="subscription"/>"
				<s:if test="#stat.even">class="shaded"</s:if>>
				<s:include value="../mail/user_email_subscription.jsp" />
			</div>
		</s:iterator>
	</div>
</div>