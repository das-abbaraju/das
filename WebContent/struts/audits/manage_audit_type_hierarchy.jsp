<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Audit Types</title>
<link rel="stylesheet" type="text/css" media="screen"href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/jquery/jquery.cookie.js"></script>
<script type="text/javascript" src="js/jquery/jsTree.v.1.0rc2/jquery.jstree.js"></script>
<script type="text/javascript">
$(function() {
	$('#audit-list').change(function() {
		$('#audit-form').submit();
	});
	
	$('#audit-tree').jstree({
		"core": { "animation": 200 },
		"themes": {
			theme: "classic"	
		},
		"json_data": {
			"ajax": {
				"url": 'ManageAuditTypeHierarchyAjax.action',
				"dataType": "json",
				"success": function(data) {
					return data.children;
				},
				"data": function(node) {
					if (node.attr) {
						var n = node.attr('id').split('_');
						return {
							"button": "json",
							"type": n[0],
							"nodeID": n[1]
						};
					}

					return { button: 'json', id: '<s:property value="id"/>'}
				}
			}
		},
		"types": {
			"max_depth": -2,
			"max_children": -2,
			"valid_children": ["audit"],
			"types": {
				"question": {
					"valid_children": "none",
					"icon": {
						"image": "js/jquery/jsTree.v.1.0rc2/themes/classic/file.png"
					}
				},
				"category": {
					"valid_children": ["question", "category"]
				},
				"audit": {
					"valid_children": ["category"]
				}
			}
		},
		plugins: ["themes", "json_data", "ui", "dnd", "types"]
	})
	.bind("remove.jstree", function (e, data) {
		console.log(data);
	})
	.bind("move_node.jstree", function (e, data) {
		console.log(data);
	});
});
</script>
</head>
<body>

<s:form id="audit-form" method="get">
<s:select id="audit-list" list="auditTypeList" headerKey="" headerValue="- Audit Type -" listKey="id" listValue="auditName" name="id" />
</s:form>

<s:if test="auditType != null">
	<h2><s:property value="auditType.auditName" /></h2>
	<div id="audit-tree">
	</div>
</s:if>

</body>
</html>