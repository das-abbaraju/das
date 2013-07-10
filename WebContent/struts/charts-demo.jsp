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
                
                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Pie
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/single-series.json" data-chart-type="Pie" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Flags Pie
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/single-series.json" data-chart-type="Pie" data-style-type="Flags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Bar Single Row
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row.json" data-chart-type="Bar" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Bar Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-multi-row.json" data-chart-type="Bar" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Flags Bar Single Row
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row.json" data-chart-type="Bar" data-style-type="Flags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Flags Bar Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-multi-row.json" data-chart-type="Bar" data-style-type="Flags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            StackedFlags Bar Single Row
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row.json" data-chart-type="Bar" data-style-type="StackedFlags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            StackedFlags Bar Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-multi-row.json" data-chart-type="Bar" data-style-type="StackedFlags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Column Single Row
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row-column.json" data-chart-type="Column" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Column Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-multi-row-column.json" data-chart-type="Column" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Flags Column Single Row
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row-column.json" data-chart-type="Column" data-style-type="Flags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Flags Column Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-multi-row-column.json" data-chart-type="Column" data-style-type="Flags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            StackedFlags Column Single Row
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-single-row-column.json" data-chart-type="Column" data-style-type="StackedFlags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            StackedFlags Column Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/multi-series-multi-row-column.json" data-chart-type="Column" data-style-type="StackedFlags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Line Single Series Single Row
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/single-series-multi-row-line.json" data-chart-type="Line" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Line Multi-Series Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/flag-history.json" data-chart-type="Line" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Flags Line Multi-Series Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/flag-history.json" data-chart-type="Line" data-style-type="Flags">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Area Single Series Single Row
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/single-series-multi-row-line.json" data-chart-type="Area" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Basic Area Multi-Series Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/flag-history.json" data-chart-type="Area" data-style-type="Basic">
                        </div>
                    </div>
                </div>

                <div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
                    <div class="panel" id="panel<s:property value="widgetID"/>">
                        <div class="panel_header">
                            Flags Area Multi-Series Multiple Rows
                        </div>
                        <div id="panel<s:property value="widgetID"/>_content" class="panel_content"
                             data-widget-type="GoogleChart" data-url="js/operator/flag-history.json" data-chart-type="Area" data-style-type="Flags">
                        </div>
                    </div>
                </div>
        <br clear="all" />
    </div>
</body>