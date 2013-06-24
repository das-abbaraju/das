<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title><s:text name="%{conAudit.auditType.getI18nKey('name')}" /> for <s:property value="conAudit.contractorAccount.name" /></title>
	 
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css?v=${version}" />
	
	<pics:permission perm="ManageCategoryRules">
		<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
	</pics:permission>
	
	<s:include value="../jquery.jsp"/>
	
	<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js?v=${version}"></script>
	<script type="text/javascript" src="js/con_audit.js?v=<s:property value="version"/>"></script>
	<script type="text/javascript" src="js/audit_data_save.js?v=<s:property value="version"/>"></script>
	<script type="text/javascript">
		var auditID = '<s:property value="conAudit.id"/>';
		var conID = '<s:property value="id"/>';
		var hasPermissionsToSeeAuditLinks = '<s:property value="hasPermissionsToSeeAuditLinks" />';
		var operatorCorporate = '<s:property value="permissions.operatorCorporate" />';
		
		$(function() {
			<s:if test="conAudit.categories.size == 0">
				updateCategoriesNow();
			</s:if>
		});
	</script>
</head>
<body>
    <div id="${actionName}_${methodName}_page" class="${actionName}-page page">
        <s:include value="../audits/audit_catHeader.jsp"/>
        
        <s:if test="policy">
            <a href="PolicyVerification.action?button=getFirst" class="picsbutton">
                <s:text name="Audit.button.FirstPolicy" />
            </a>
            <a href="PolicyVerification.action?button=showNext&auditID=<s:property value="auditID" />" class="picsbutton positive">
                <s:text name="Audit.button.NextPolicy" /> &gt;&gt;
            </a>
            <br clear="all" />
        </s:if>

        <s:if test="problems.size() > 0">
            <div id="auditProblems">
                <s:text name="Audit.ProblemsWithAudit" />
            </div>
            <div id="problems">
                <table class="report" style="margin: 0 auto;">
                    <thead>
                        <tr>
                            <th>
                                <s:text name="global.Operator" />
                            </th>
                            <th>
                                <s:text name="Audit.Problem" />
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <s:iterator value="problems" var="pro">
                            <tr>
                                <td>
                                    <s:property value="getViewableCaops(#pro.key).iterator.next.operator.name"/>
                                </td>
                                <td>
                                    <s:property value="#pro.value"/>
                                </td>
                            </tr>
                        </s:iterator>   
                    </tbody>
                </table>
                
                <div class="bottom"><s:text name="button.Hide" /></div>
            </div>
            <div id="problemsHide"></div>
        </s:if>
        
        <div id="submitRemind"></div>
        
        <div class="right noprint" id="modes">
            <s:if test="canEditAudit">
                <a class="edit modeset" href="#mode=Edit">
                    <s:text name="button.Edit" />
                </a>
            </s:if>
            
            <a class="view modeset" href="#mode=View">
                <s:text name="button.View" />
            </a>
            
            <s:if test="canVerifyAudit">
                <a class="verify modeset" href="#mode=Verify">
                    <s:text name="button.Verify" />
                </a> 
            </s:if>
            
            <span style="display: none;" id="printReqButton">
                <a class="print" href="javascript:window.print();">
                    <s:text name="button.Print" />
                </a>
            </span>
        </div>
        
        <table id="audit-layout">
            <tr>
                <td class="auditHeaderSideNav noprint">
                    <div id="auditHeaderSideNav">
                        <s:include value="con_audit_sidebar.jsp"/>
                    </div>
                </td>
                <td style="width: 100%; height: 100%;">
                    <div id="auditViewArea"></div>
                </td>
            </tr>
        </table>
        
        <s:if test="!@com.picsauditing.util.Strings@isEmpty(auditorNotes)">
            <div class="info">
                <b><s:text name="Audit.message.SafetyProfessionalNotes" />:</b>
                <s:property value="auditorNotes"/>
            </div>
        </s:if>
        
        <br clear="all"/>
    </div>
</body>