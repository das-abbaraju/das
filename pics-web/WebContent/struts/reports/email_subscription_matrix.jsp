<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ReportEmailSubscriptionMatrix.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/jquery/autocompletefb/jquery.autocompletefb.js?v=${version}"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocompletefb/jquery.autocompletefb.css?v=${version}" />
<script type="text/javascript">
var ac_users = <s:property value="usersJSON" escape="false" />;
var ac_subscriptions = <s:property value="subsJSON" escape="false" />;
var users_acfb, subscriptions_acfb;
$(function(){
	function acfbuild(cls,url,type){
		var ix = $("input"+cls);
		ix.addClass('acfb-input').wrap('<ul class="'+cls.replace(/\./,'')+' acfb-holder"></ul>');
		var s = $('#matrix');
		return $("ul"+cls).autoCompletefb({
				urlLookup:url,
				acOptions: {
					matchContains: true,
					formatItem: function(row,index,count) {
						return row.name;
					},
					formatResult: function(row,index,count) {
						return row.id;
					}
				},
				onfind: function(d, count) {
					startThinking({message:'Searching...'});
					if (count > 0)
						$('.'+type,s).not('.selected_'+type).hide();
					$('.'+d.id,s).show().addClass('selected_'+type);
					stopThinking();
				},
				onremove: function(d, count) {
					startThinking({message:'Removing...'});
					var f = $('.'+d.id,s);
					f.not('selected_'+type	).removeClass('selected_'+type).hide();
					if (count == 0)
						$('.'+type,s).show();
					stopThinking();
				}
			});
	}
	users_acfb = acfbuild('.users', ac_users, 'userdata');
	users_subscriptions_acfbacfb = acfbuild('.subscriptions', ac_subscriptions, 'subdata');

	$('#form1').submit(function(e){e.preventDefault()});
});
</script>
<style type="text/css">
.table-key {
	float: left;
	border: 2px solid #4686bf;
	margin: 10px 0;
	padding: 0;
}

.table-key h4 {
	display: block;
	background-color: #eeeeee;
	position: relative;
	top: -10px;
	left: 10px;
	width: 80px;
	padding: 0 0 0 4px;
}

.table-key ul {
	list-style: none;
	margin-top: -10px;
	width: 240px;
}

.table-key ul li {
	list-style: none;
	display: block;
	float: left;
	width: 120px;
	text-align: left;
}

#form1 {
	clear: both;
}

.search-btn {
	margin-top: 26px;
}

fieldset.form {
	border: none;
	background-color: transparent;
	clear: both;
}

fieldset.form div.filterOption {
	width: 350px;
	padding-bottom: 10px;
}

div.filterOption input {
	float: left;
	clear: both;
}
</style>
</head>
<body>

<h1><s:text name="ReportEmailSubscriptionMatrix.title" /></h1>

<div id="search">
	<div class="clear"></div>
	<s:form id="form1" method="get" cssStyle="width: 800px;">
		<fieldset class="form">
			<div class="filterOption">
				<h4><s:text name="ReportEmailSubscriptionMatrix.SearchByUser" />:</h4>
				<s:hidden name="users" value=""/>
				<s:textfield size="50" cssClass="users"/>
			</div>
		<div class="filterOption">
			<h4><s:text name="ReportEmailSubscriptionMatrix.SearchBySubscription" />:</h4>
				<s:hidden name="perms" value=""/>
				<s:textfield size="50" cssClass="subscriptions"/>
				</div>
		</fieldset>
	</s:form>
	<div class="table-key">
		<h4><s:text name="global.Legend" /></h4>
		<ul>
			<li><s:text name="SubscriptionTimePeriod.Daily.short" /> - <s:text name="SubscriptionTimePeriod.Daily" /></li>
			<li><s:text name="SubscriptionTimePeriod.Weekly.short" /> - <s:text name="SubscriptionTimePeriod.Weekly" /></li>
			<li><s:text name="SubscriptionTimePeriod.Monthly.short" /> - <s:text name="SubscriptionTimePeriod.Monthly" /></li>
			<li><s:text name="SubscriptionTimePeriod.Event.short" /> - <s:text name="SubscriptionTimePeriod.Event" /></li>
		</ul>
	</div>
	<div class="clear"></div>
</div>
<div class="right">
	<a class="excel" href="ReportEmailSubscriptionMatrix!download.action?account=<s:property value="account.id" />">
		<s:text name="global.Download" />
	</a>
</div>
<div style="height:22px;">
	<div class="right" id="mainThinkingDiv"></div>
</div>
<table class="report" id="matrix">
	<thead>
	<tr>
		<th><s:text name="User" /></th>
		<s:iterator value="@com.picsauditing.mail.Subscription@values()">
			<th class="<s:property /> subdata"><s:text name="%{getI18nKey('description')}" /></th>
		</s:iterator>
	</tr>
	</thead>
	<tbody>
		<s:iterator value="users" id="user">
			<tr class="<s:property value="#user.id"/> userdata">
				<td>
					<a href="UsersManage.action?user=<s:property value="#user.id"/>"><s:property value="#user.name" /></a>
				</td>
				<s:iterator value="@com.picsauditing.mail.Subscription@values()" id="sub">
					<td class="<s:property value="#sub"/> subdata">
						<s:if test="table.get(#user, #sub).timePeriod != null && table.get(#user, #sub).timePeriod.name() != 'None'">
							<s:text name="%{table.get(#user, #sub).timePeriod.getI18nKey('short')}" />
						</s:if>
					</td>
				</s:iterator>
			</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>
