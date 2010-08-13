<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Audit Types</title>
<link rel="stylesheet" type="text/css" media="screen"href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
$(function() {
	$('.list').sortable();
	$('.expand').click(function() { $('> .subs,> .questions',$(this).parent()).toggle(); });
	$('.list li').dblclick(function() { $('.expand:first',this).click(); return false});
})
</script>
<style>
.questions, .subs{ display: none; }

.list.even {
	background-color: #dedede;
}
</style>
</head>
<body>

<s:if test="auditType == null">
<table class="report">
<thead>
	<tr><th>Audit Type</th></tr>
</thead>
	<s:iterator value="auditTypeList">
	<tr>
		<td><a href="?id=<s:property value="id"/>"><s:property value="auditName"/></a></td>
	</tr>
	</s:iterator>
</table>
</s:if>

<s:else>
	<h2><s:property value="auditType.auditName" /></h2>
	<ul class="list">
		<s:iterator value="auditType.categories">
			<li><s:include value="manage_audit_type_hierarchy_category.jsp" /></li>
		</s:iterator>
	</ul>
</s:else>

</body>
</html>