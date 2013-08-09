<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="title"><s:property value="report.name"/></s:set>
<s:set name="description"><s:property value="report.description"/></s:set>

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>PICS - Print Report</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">

        <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

        <link rel="stylesheet" type="text/css" href="v7\css\report-print.css" />
    </head>

    <body id="${actionName}_${methodName}_page" class="${actionName}-page page">
        <div class="wrapper">
            <table>
                <caption>
                    <div class="report-info">
                        <h1>${title}</h1>
                        <h2>${description}</h2>
                    </div>
                    <div class="print-info">
                        <p>picsorganizer.com</p>
                        <p class="print-date"></p>
                    </div>
                </caption>
                <thead>
                    <tr>
                    <s:iterator value="report.columns">
                        <th class="<s:property value="name" />"><s:property value="field.text" /></th>
                    </s:iterator>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="reportResults.rows" var="row">
                        <tr>
                            <s:iterator value="report.columns" var="column">
                                <s:set name="display_type"><s:property value="#column.displayType" /></s:set>

                                <s:if test="#display_type == 'Number'">
                                    <s:set name="class_name">number</s:set>
                                </s:if>
                                <s:elseif test="#display_type == 'String'">
                                    <s:set name="class_name">string</s:set>
                                </s:elseif>
        
                                <td class="${class_name}">
                                    <div class="no-page-break">
                                        <s:property value="#row.getCellByColumn(#column).value" />
                                    </div>
                                </td>
                            </s:iterator>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
        </div>

        <script src="v7/js/script.js?v=${version}"></script>

        <!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
        <script>
            var _gaq=[['_setAccount','UA-2785572-4'],['_trackPageview']];
            (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
            g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js';
            s.parentNode.insertBefore(g,s)}(document,'script'));
        </script>
    </body>
</html>