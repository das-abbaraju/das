<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="Audit.message.Preview"><s:param><s:property value="conAudit.auditType.name"/></s:param></s:text></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>

<script type="text/javascript">
$(document).ready(function() {
	$('.catlist').live('mouseenter', function(){
		$(this).addClass('hover');
	}).live('mouseleave', function(){
		$(this).removeClass('hover');
	});
});
</script>
</head>
<body>

<h1><s:text name="Audit.message.Preview"><s:param><s:property value="conAudit.auditType.name"/></s:param></s:text></h1>

<table>
	<tr>
		<td>
		<ul id="aCatlist" class="vert-toolbar catUL">
			<li class="head"><s:text name="Audit.header.Categories" /></li>
			<s:iterator value="conAudit.auditType.topCategories">
				<li class="catlist<s:if test="categoryID == id"> current</s:if>">
					<a href="AuditCatPreview.action?categoryID=<s:property value="id" />&button=PreviewCategory"
						title="<s:text name="Audit.PreviewCategory" />"><s:property value="name" /></a>
				</li>
			</s:iterator>
		</ul>
		</td>
		<td style="width: 100%; height: 100%;">
			<s:iterator value="categories">
				<s:set var="category" value="key" scope="action" />
			</s:iterator>
			<div id="auditViewArea"><s:include value="audit_cat_view.jsp" /></div>
		</td>
	</tr>
</table>

</body>
</html>