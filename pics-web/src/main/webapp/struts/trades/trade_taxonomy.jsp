<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<head>
	<title>
		<s:text name="TradeTaxonomy.title"/>
	</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/css/reports.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="/css/trades.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="/css/rules.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="/js/jquery/blockui/blockui.css?v=${version}" />
	
	<s:include value="../jquery.jsp"/>
	
	<script type="text/javascript" src="/js/jquery/jsTree/jquery.jstree.js?v=${version}"></script>
	<script type="text/javascript" src="/js/jquery/blockui/jquery.blockui.js?v=${version}"></script>
	<pics:permission perm="ManageTrades" type="Edit">
		<script type="text/javascript" src="/js/trade_taxonomy_admin.js?v=${version}"></script>
	</pics:permission>
	<script type="text/javascript" src="/js/trade_taxonomy_common.js?v=${version}"></script>
	<script type="text/javascript">
		var ajaxUrl = 'TradeTaxonomy!tradeAjax.action?trade=';
	</script>
</head>
<body>
	<h1><s:text name="TradeTaxonomy.title"/></h1>
	
	<s:include value="../actionMessages.jsp"></s:include>
	
	<pics:permission perm="ManageTrades" type="Edit">
		<div class="clearfix">
			<a href="#" class="add trade"><s:text name="TradeTaxonomy.AddTopLevelTrade"/></a>
		</div>
	</pics:permission>
	
	<s:include value="trade_search.jsp"/>
	
	<div id="trade-view">
		<div class="info">
			<s:text name="TradeTaxonomy.SearchOrBrowse"/>
		</div>
	</div>
</body>