<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ContractorTrades.title"></s:text> </title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js?v=<s:property value="version"/>"></script>
<style>
a.trade, a.trade:hover, a.trade:active {
	text-decoration: none;
	white-space: nowrap;
}
#trade-cloud {
	background-color: Snow;
	border: 2px solid #A84D10;
	padding: 1em;
}

#trade-cloud a.trade {
	padding: 3px;
	margin-right: 1em;
	color: white;
	background-color: black;
}

#trade-cloud a.trade1 { font-size: 8px; }
#trade-cloud a.trade2 { font-size: 10px; }
#trade-cloud a.trade3 { font-size: 12px; }
#trade-cloud a.trade4 { font-size: 14px; }
#trade-cloud a.trade5 { font-size: 16px; }
#trade-cloud a.trade6 { font-size: 18px; }
#trade-cloud a.trade7 { font-size: 21px; font-weight: bold; }
#trade-cloud a.trade8 { font-size: 24px; font-weight: bold; }
#trade-cloud a.trade9 { font-size: 28px; font-weight: bold; }
#trade-cloud a.trade10 { font-size: 32px; font-weight: bold; }

#trade-cloud a.trade:hover {
	color: white;
	background-color: black;
}

#trade-nav {
	background-color: #E0EEFF;
	border: 2px solid #4686BF;
	float: left;
	height: 500px;
	overflow: auto;
	padding: 0;
	width: 50%;
}

#trade-view {
	float: right;
	width: 45%
}

#trade-view ul, #trade-view ol {
	list-style: none;
}

#trade-view ol > li {
	padding: 5px;
}

#trade-view-single {
	margin-top: 1em;
}

#trade-view-single .buttons {
	float: right;
}

#trade-view-single .hierarchy {
	border: 2px solid #DDDDDD;
	height: 250px;
	overflow-y: auto;
	overflow-x: hidden;
	padding: 0;
	width: 100%;
}

.fieldoption {
	padding: 10px;
	width: 100%;
}
.search {
	width: 400px;
}
</style>
<script>
var conID = '<s:property value="id"/>';

$(function() {
	$('#trade-nav').tabs();

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
					"success": function(json) {
						if (json.result.length == 0)
							$('#search-tree').msg('alert', 'No trades found');
						else
							$('#search-tree .alert').remove();
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
		<div class="alert"><s:text name="ContractorTrades.RegistrationMessage" /></div>
		<s:form><s:submit action="ContractorTrades!nextStep" cssClass="picsbutton positive" value="%{getText('button.Next')}"></s:submit></s:form>
	</s:else>
</s:if>




<div id="trade-nav">
	<ul>
		<li><a href="#search-tab"><s:text name="%{scope}.header.Search"/></a></li>
		<li><a href="#browse-tab"><s:text name="%{scope}.header.Browse"/></a></li>
	</ul>
	<div id="search-tab">
		<form id="suggest">
			<input type="search" name="q" class="search" />
			<input type="submit" value="Search" class="trade-search" />
		</form>
		<div class="messages"></div>
		<div id="search-tree"></div>
	</div>
	<div id="browse-tab">
		<div id="browse-tree"></div>
	</div>
</div>
<div id="trade-view">
	<s:include value="contractor_trade_cloud.jsp"/>
</div>
</body>
</html>