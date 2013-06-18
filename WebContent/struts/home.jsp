<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
    <title><s:text name="global.Home" /></title>
    
    <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css?v=${version}" />
    
    <s:include value="jquery.jsp"/>
    
    <script src="js/FusionCharts.js" type="text/javascript"></script>
    
    <script type="text/javascript">
        function hidePanel(panel) {
        	$('#' + panel + "_hide").hide();
        	$('#' + panel + "_show").show();
        	$('#' + panel + "_content").hide();
        }
        
        function showPanel(panel) {
        	$('#' + panel + "_hide").show();
        	$('#' + panel + "_show").hide();
        	$('#' + panel + "_content").show();
        }
    </script>
</head>
<body>
<s:include value="actionMessages.jsp" />
    <div id="${actionName}_${methodName}_page" class="${actionName}-page page">
        <s:if test="permissions.contractor">
        	<s:include value="contractors/conHeader.jsp" />
        </s:if>
        <s:elseif test="!permissions.admin">
        	<h1>
                <s:text name="Home.Welcome" />
                <span class="sub"><s:property value="account.name" /></span>
            </h1>
        </s:elseif>
        
        <s:if test="permissions.admin && permissions.shadowedUserID != permissions.userId">
        	<div class="info">You are viewing <s:property value="permissions.shadowedUserName" />'s Dashboard</div>
        </s:if>
    
        <s:if test="!supportedLanguages.isLanguageVisible(permissions.locale)">
            <div id="beta_translations_alert">
                <p>
                    <s:text name="global.BetaTranslations" />
                </p>
            </div>
        </s:if>
                
        <s:iterator value="columns">
        	<div class="column" id="column<s:property value="key"/>" style="width: <s:property value="columnWidth"/>%">
        		<s:iterator value="value">
        			<s:if test="widgetID !=19 || permissions.approvesRelationships" >
        			<s:if test="widgetID != 11 || !permissions.insuranceOnlyContractorUser">
        				<div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
            				<div class="panel" id="panel<s:property value="widgetID"/>">
                				<div class="panel_header">
                					<s:if test="!synchronous">
                						<a href="#" onclick="<s:property value="reload" escape="false" />; return false;" style="float: right"><img src="images/arrow-360.png" /></a>
                					</s:if>
                                    
                					<s:text name="%{'Widget.' + widgetID + '.caption'}" />
                                    
                					<pics:permission perm="DevelopmentEnvironment">
                						<a href="<s:property value="url"/>" target="_BLANK" class = "debug">URL</a>
                					</pics:permission>
                				</div>
                                <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                                     data-widget-type="${widgetType}" data-url="${url}" data-chart-type="${googleChartType}">
                                   <s:property value="content" escape="false" />
                                </div>
            				</div>
        				</div>
        			</s:if>
        			</s:if>
        		</s:iterator>
        	</div>
        </s:iterator>
        
        <br clear="all" />
    </div>
</body>