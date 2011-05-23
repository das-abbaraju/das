<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ContractorTrades.title"></s:text> </title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js?v=<s:property value="version"/>"></script>
<script>
var conID = '<s:property value="id"/>';

$(function() {
	$('#trade-nav').tabs();

	$('a.tradeInfo').live('click',function() {
		$('div.trade-section').hide();
		$($(this).attr('href')).toggle();
		});
	
	var treeOptions = {
			"themes": {
				theme: "classic"	
			},
			"json_data": {
				"ajax": {
					"url": 'TradeTaxonomy!json.action',
					"success": function(json) {
						return json.result;
					},
					"data": function(node) {
						result = {};
						if (node.attr) {
							result.trade = node.attr('id');
						}
						return result;
					}
				}
			},
			"plugins": ["themes", "json_data", "search", "sort"]
		}

	var search_tree = $('#search-tree').jstree(
		$.extend(true, {}, treeOptions, {
			"json_data": {
				"ajax": {
					"url": 'TradeTaxonomy!searchJson.action',
					"success": function(json) {
						return json.result;
					},
					"data": function(node) {
						result = $('#suggest').serialize();
						if (node.attr) {
							result += "&trade=" + node.attr('id');
						}
						return result;
					}
				}
			}
		})
	);
	
	var browse_tree = $('#browse-tree').jstree(treeOptions);

	function loadTrades(url, data) {
		$('#trade-view').load(url, data, function() {
			$('#trade-hierarchy').jstree(
				$.extend(true, {}, treeOptions, {
					"json_data": {
						"ajax": {
							"url": function(node) {
								if (node == -1)
									return 'TradeTaxonomy!hierarchyJson.action';
								else
									return 'TradeTaxonomy!json.action';
							},
							"data": function(node) {
								if (node == -1) {
									return {
										trade: $('#trade-form [name=trade.trade]').val()
									};
								} else {
									result = $('#suggest').serialize();
									if (node.attr) {
										result += "&trade=" + node.attr('id');
									}
									return result;
								}
							}
						}
					}
				})
			);

			if ('<s:property value="requiresMaterial" />' || '<s:property value="requiresService" />') {
				$("#addButton").attr("disabled", "disabled");
			}
		});
	}

	$('body').delegate('.jstree a', 'click', function(e) {
		e.preventDefault();
		var data = { contractor: conID, "trade.trade": $(this).parent().attr('id') };
		loadTrades('ContractorTrades!tradeAjax.action', data);
	});
	
	$('#trade-view').delegate('a.trade', 'click', function(e) {
		e.preventDefault();
		loadTrades($(this).attr('href'));
	});
	
	$('#suggest').submit(function(e) {
		e.preventDefault();
		search_tree.jstree('close_all').jstree('refresh');
	});

	$('#trade-view').delegate('#trade-form', 'submit', function(e) {
		e.preventDefault();
	}).delegate('#trade-form .save', 'click', function(e) {
		loadTrades('ContractorTrades!saveTradeAjax.action', $('#trade-form').serializeArray())
	}).delegate('#trade-form .remove', 'click', function(e) {
		if (confirm("Are you sure you want to remove this trade?")) {
			loadTrades('ContractorTrades!removeTradeAjax.action', $('#trade-form').serialize());
		}
	});

	$('input.service').live("change", function() {
		console.log($('input.service:checked').length > 0)
		if ($('input.service:checked').length > 0)
			$("div.buttons .picsbutton").removeAttr("disabled");
		else
			$("div.buttons .picsbutton").attr("disabled", "disabled");
	});
	
	$('input.product').live('change', function() {
		if ($(this).is(':checked'))
			$("#addButton").removeAttr("disabled");
		else
			$("#addButton").attr("disabled", "disabled");
	});
});
</script>
</head>
<body>

<s:include value="../contractors/conRegistrationHeader.jsp"/>

<s:if test="permissions.contractor">
	<s:if test="registrationStep.done">
		<s:if test="contractor.needsTradesUpdated">
			<div class="alert"><s:text name="ContractorTrades.NeedsUpdating" />
				<s:form><s:submit action="ContractorTrades!nextStep" cssClass="picsbutton positive" value="%{getText('button.Confirm')}"></s:submit></s:form>
			</div>
		</s:if>
	</s:if>
	<s:else>
		<s:form><s:submit action="ContractorTrades!nextStep" cssClass="picsbutton positive" value="%{getText('button.Next')}"></s:submit></s:form>
	</s:else>
</s:if>


<h4>What are your business trades?</h4>
<s:include value="trade_search.jsp"/>

<div id="trade-view">
	<s:include value="contractor_trade_cloud.jsp"/>
</div>
</body>
</html>