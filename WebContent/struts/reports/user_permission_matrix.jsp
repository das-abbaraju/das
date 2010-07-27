<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
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
$(function(){
	var sortList = $('#list').sortable({
		update: function() {
			$('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="subCategory.id"></s:property>&type=AuditSubCategory', 
				sortList.sortable('serialize'), 
				function() {sortList.effect('highlight', {color: '#FFFF11'}, 1000);}
			);
		}
	});
});

var data = <s:property value="data" escape="false"/>;
var initCountries = <s:property value="initialCountries" escape="false"/>;
var acfb;
$(function(){
	function acfbuild(cls,url){
		var ix = $("input"+cls);
		ix.addClass('acfb-input').wrap('<ul class="'+cls.replace(/\./,'')+' acfb-holder"></ul>');
		
		return $("ul"+cls).autoCompletefb({
				urlLookup:url,
				delimeter: '|',
				acOptions: {
					matchContains: true,
					formatItem: function(row,index,count){
						return row.name + ' (' + row.id + ')';
					},
					formatResult: function(row,index,count){
						return row.id;
					}
				}
			});
	}
	acfb = acfbuild('.countries', data);
	acfb.init(initCountries);

	$('form#save').submit(function() {
			$(this).find('[name=countries]').val(acfb.getData());
		}
	);
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

</style>
</head>
<body>

<h1>User Permissions Matrix</h1>
<div id="manage_controls">
<div id="search">
<div class="clear"></div>
<s:form id="form1" method="get" cssStyle="width: 800px;">
	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{subCategory.category.id}" />
	<s:hidden name="subCategory.category.id" />
		<fieldset class="form">
		<div class="filterOption">
			<h4>Search by user/group:</h4>
				<s:hidden name="countries" value="%{subCategory.countries}"/>
				<s:textfield size="50" cssClass="countries"/>
			</div>
			<div class="filterOption">
			<h4>Search by permission:</h4>
				<s:hidden name="countries" value="%{subCategory.countries}"/>
				<s:textfield size="50" cssClass="countries"/>
				</div>
			<div class="search-btn">
			<button class="picsbutton positive" type="submit" name="button" value="Search">Search</button>
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
</ul></div>
<div class="clear"></div>
	</div>
<div style="height:15px;"></div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">User/Group</td>
		<s:iterator value="perms">
			<td><s:property value="description" /></td>
		</s:iterator>
	</tr>
	</thead>
	<s:iterator value="users" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="UsersManage.action?accountId=<s:property value="accountID"/>&user.id=<s:property value="id"/>">
					<s:property value="name" /></a>
			</td>
			<s:iterator value="perms">
				<td>
				<s:iterator value="permissions">
					<s:if test="[1].equals(opPerm)">
						<s:if test="viewFlag==true">V</s:if>
						<s:if test="editFlag==true">E</s:if>
						<s:if test="deleteFlag==true">D</s:if>
						<s:if test="grantFlag==true">G</s:if>
					</s:if>
				</s:iterator>
				</td>
			</s:iterator>
		</tr>
	</s:iterator>
</table>

</body>
</html>
