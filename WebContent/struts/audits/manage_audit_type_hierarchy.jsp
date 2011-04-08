<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Audit Types</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/jquery/jquery.cookie.js"></script>
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js"></script>
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
					return data.result;
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
						"image": "images/file.png"
					}
				},
				"category": {
					"valid_children": ["question", "category"],
					"icon": {
						"image": "images/folder.png"
					}
				},
				"audit": {
					"valid_children": ["category"],
					"icon": {
						"image": "images/root.png"
					},
					"start_drag" : false,
					"move_node" : false,
					"delete_node" : false,
					"remove" : false
				}
			}
		},
		"ui" : {
			"selected_parent_close": "deselect",
			"disable_selecting_children": true,
			"select_multiple_modifier": "shift"
		},
		"contextmenu": {
			"items": function(node) {
				var id = node.attr('id').split('_')[1];
				if (this._get_type(node) == "audit")
					return {
						"view": {
							"label": "View",
							"action": function() {
								window.open('ManageAuditType.action?id='+id);
							}
						}
					};
				if (this._get_type(node) == "category")
					return {
						"view": {
							"label": "View",
							"action": function() {
								window.open('ManageCategory.action?id='+id);
							}
						}
					};
				if (this._get_type(node) == "question")
					return {
						"view": {
							"label": "View",
							"action": function() {
								window.open('ManageQuestion.action?id='+id);
							}
						}
					};
			}
		},
		plugins: ["themes", "json_data", "ui", "dnd", "types", "contextmenu", "crrm"]
	})
	.bind("remove.jstree", function (e, data) {
		alert("remove node");
	})
	.bind("move_node.jstree", function (e, data) {
		var np = data.rslt.np.attr('id').split('_');
		var types=[], ids=[];
		data.inst._get_children(data.rslt.np).each(function(i) {
			var n = $(this).attr('id').split('_');
			types.push(n[0]);
			ids.push(n[1]);
		});
		startThinking();
		$.ajax({
			async: false,
			type: 'POST',
			dataType: 'json',
			url: 'ManageAuditTypeHierarchyAjax.action',
			data: {
				button: 'move',
				types: types,
				ids: ids,
				parentType: np[0],
				parentID: np[1]
			},
			success: function(r) {
				if (r.success) {
					data.inst.refresh(data.rslt.np);
					data.inst.refresh(data.rslt.op);
					data.inst.open_node(data.rslt.op);
				} else {
					$.jstree.rollback(data.rlbk);
				}
				stopThinking();
			}
		});
	});
});
</script>
<style>
.required a {
	color: #272 !important;
}
</style>
</head>
<body>
<h2>Manage Hierarchy</h2>

<s:form id="audit-form" method="get">
	<s:select id="audit-list" list="auditTypeList" headerKey=""
		headerValue="- Audit Type -" listKey="id" listValue="name"
		name="id" />
</s:form>

<div id="mainThinkingDiv" class="right"></div>

<s:if test="auditType != null">
	<div id="audit-tree"></div>
</s:if>

</body>
</html>