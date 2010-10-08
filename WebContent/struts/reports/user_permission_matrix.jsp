<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>User Permissions Matrix</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocompletefb/jquery.autocompletefb.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocompletefb/jquery.autocompletefb.css" />
<script type="text/javascript">
var ac_users = <s:property value="tableDisplay.rowsJSON" escape="false"/>;
var ac_permissions = <s:property value="tableDisplay.colsJSON" escape="false"/>;
var users_acfb, permissions_acfb;
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
	users_permissions_acfbacfb = acfbuild('.permissions', ac_permissions, 'permdata');

	$('#form1').submit(function(e){e.preventDefault()});
});
</script>

<style type="text/css">
.table-key {
	float:left;
	border:2px solid #4686bf;
	margin:10px 0;
	padding:0;
}
.table-key h4 {
display:block;
background-color:#eeeeee;
position:relative;
top:-10px;
left:10px;
width:36px;
padding:0 0 0 4px;
}
.table-key ul {list-style:none;margin-top:-10px;width:160px;}
.table-key ul li {
list-style:none;
display:block;
float:left;
width:80px;
text-align:left;
}
#form1 {clear:both;}

.search-btn {margin-top:26px;}

fieldset.form {border:none;background-color:transparent;clear:both;}

fieldset.form div.filterOption {width: 350px; padding-bottom: 10px;}
div.filterOption input {
	float: left;
	clear: both;
}
</style>
</head>
<body>

<h1>User Permissions Matrix</h1>

<div id="search">
	<div class="clear"></div>
	<s:form id="form1" method="get" cssStyle="width: 800px;">
			<fieldset class="form">
			<div class="filterOption">
				<h4>Search by user/group:</h4>
					<s:hidden name="users" value=""/>
					<s:textfield size="50" cssClass="users"/>
				</div>
			<div class="filterOption">
				<h4>Search by permission:</h4>
					<s:hidden name="perms" value=""/>
					<s:textfield size="50" cssClass="permissions"/>
					</div>
			</fieldset>
	</s:form>
	<div class="table-key">
		<h4>Key</h4>
		<ul>
			<li>V = View</li>
			<li>E = Edit</li>
			<li>D = Delete</li>
			<li>G = Grant</li>
		</ul>
	</div>
	<div class="clear"></div>
</div>
<div class="right">
	<a class="excel" title="Download all Permissions to a CSV file"
		href="?button=download&accountId=<s:property value="accountId"/>">Download</a>
</div>
<div style="height:22px;">
	<div class="right" id="mainThinkingDiv"></div>
</div>

<table class="report" id="matrix">
	<thead>
	<tr>
		<th>User/Group</th>
		<s:iterator value="tableDisplay.cols">
			<th class="<s:property/> permdata"><s:property value="description" /></th>
		</s:iterator>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="tableDisplay.rows" id="user">
		<tr class="<s:property value="#user.id"/> userdata">
			<td>
				<a href="UsersManage.action?accountId=<s:property value="#user.account.id"/>&user.id=<s:property value="#user.id"/>"><s:property value="#user.name" /></a>
			</td>
			<s:iterator value="tableDisplay.cols" id="perm">
				<td class="<s:property value="#perm"/> permdata">
					<s:property value="tableDisplay.get(#user, #perm)"/>
				</td>
			</s:iterator>
		</tr>
	</s:iterator>
	</tbody>
</table>
</body>
</html>
