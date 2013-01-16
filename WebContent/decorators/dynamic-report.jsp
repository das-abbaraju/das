<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="app_dir">v7/js/extjs/pics</s:set>
<s:set var="css_dir">v7/js/extjs/pics/resources/css</s:set>
<s:set var="extjs_dir">v7/js/extjs/pics/extjs</s:set>

<s:set var="is_development_mode" value="true" />

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
	<head>
		<meta charset="utf-8">
		
		<title>PICS - <decorator:title default="PICS" /></title>
		
        <s:if test="#is_development_mode == true">
            <script type="text/javascript" src="${extjs_dir}/ext-debug.js"></script>
            <script type="text/javascript" src="${extjs_dir}/ext-debug-override.js"></script>
            <script type="text/javascript" src="${app_dir}/ext-overrides.js"></script>
            <script type="text/javascript" src="${app_dir}/app.js?v=${version}"></script>
        </s:if>
        <s:else>
            <script type="text/javascript" src="${extjs_dir}/ext.js?v=${version}"></script>
            <script type="text/javascript" src="${extjs_dir}/app-all.js?v=${version}"></script>
        </s:else>
        
        <s:if test="#is_development_mode == true">
            <link rel="stylesheet" type="text/css" href="${css_dir}/my-ext-theme.css?v=${version}" />
        </s:if>
        <s:else>
            <link rel="stylesheet" type="text/css" href="${css_dir}/my-ext-theme.min.css?v=${version}" />
        </s:else>
        
        <link rel="stylesheet" type="text/css" href="${css_dir}/font-awesome.css?v=${version}" />
        <!--[if lt IE 8]><link rel="stylesheet" href="${css_dir}/font-awesome-ie7.css"><![endif]-->
		
		<decorator:head />
	</head>
	<body id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<decorator:body />
	</body>
</html>