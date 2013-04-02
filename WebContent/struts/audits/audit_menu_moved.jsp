<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<head>
    <title><s:text name="%{conAudit.auditType.getI18nKey('name')}"/> has moved</title>

    <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>"/>
    <link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>"/>
    <link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
    <link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css?v=${version}"/>

    <pics:permission perm="ManageCategoryRules">
        <link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>"/>
    </pics:permission>

    <s:include value="../jquery.jsp"/>

    <style>
        .centerblock {
            margin-left: auto;
            margin-right: auto;
            width: 90%;
        }

        .whiteblock {
            width: 450px;
            height: 300px;
            background-color: #ffffff;
            border: 1px solid #cccccc;
            padding: 80px 80px;
            margin-left: auto;
            margin-right: auto;
            margin-top: 30px;
            margin-bottom: 30px;
        }

        .grayBackground {
            border: 1px solid #cccccc;
            background-color: #f1f1f2;
            border-right: none;
            border-left: none;
            margin: 30px;
        }

        .itemMovedTitle {
            font-size: 40px;
            color: #808285;
        }

        .itemMovedSubTitle {
            font-size: 20px;
            color: #999999;
        }

        .clientReviews {
            font-size: 24px;
            color: #73BBE8;
        }

        .itemMovedBody {
            font-size: 14px;
            color: #666666;
            line-height: 20px;
        }

        .indentBody {
            margin-left: 30px;
        }

    </style>
</head>
<body>
<s:include value="../audits/audit_catHeader.jsp"/>
<div class="grayBackground">
    <div class="whiteblock">
        <div class="centerblock">
            <div class="itemMovedTitle"><s:text name="AuditMenuItemMoved.title"/></div>
            </br>
            <div class="itemMovedSubTitle"><s:text name="AuditMenuItemMoved.subtitle"/></div>
            </br></br>
            <div class="indentBody">
                <div class="clientReviews"><s:text name="global.ClientReviews"/></div>
                </br>
                <div class="itemMovedBody">
                    <s:text name="AuditMenuItemMoved.body">
                        <s:param><s:text name="%{conAudit.auditType.getI18nKey('name')}"/></s:param>
                    </s:text>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</body>