<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:property value="contractor.name" /> Risk Levels</title>
		
		<meta name="help" content="User_Manual_for_Contractors">
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		
		<s:include value="../jquery.jsp"/>
	</head>
	<body>
		<s:include value="conHeader.jsp"></s:include>
		
		<a href="ContractorEdit.action?id=<s:property value="contractor.id" />" class="picsbutton">&laquo; Back to Contractor Edit</a>
		
		<s:form id="save" method="POST" enctype="multipart/form-data">
			<s:hidden name="id" />
			
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="ContractorEditRiskLevel.RiskLevels"/></h2>
				
				<ol>
                    <pics:toggle name="<%= FeatureToggle.TOGGLE_SAFETY_SENSITIVE_ENABLED %>">
                        <li>
                            <label>Safety Sensitive:</label>
                            <s:radio
                                    list="#{true:'Yes', false:'No'}"
                                    name="safetySensitive"
                                    value="%{contractor.safetySensitive}"
                                    theme="pics"
                                    cssClass="inline"
                                    />
                        </li>
                    </pics:toggle>
                    <pics:toggleElse>
                        <li>
                            <label>Safety Risk:</label>
                            <s:radio
                                    list="riskLevelList"
                                    name="safetyRisk"
                                    value="%{contractor.safetyRisk}"
                                    theme="pics"
                                    cssClass="inline"
                                    />
                        </li>
                        <li>
                            <label>Product Risk:</label>
                            <s:radio
                                    list="riskLevelList"
                                    name="productRisk"
                                    value="%{contractor.productRisk}"
                                    theme="pics"
                                    cssClass="inline"
                                    />
                        </li>
                        <li>
                            <label>Transportation Risk:</label>
                            <s:radio
                                    list="riskLevelList"
                                    name="transportationRisk"
                                    value="%{contractor.transportationRisk}"
                                    theme="pics"
                                    cssClass="inline"
                                    />
                        </li>
                    </pics:toggleElse>
				</ol>
			</fieldset>
			
			<fieldset class="form submit">
				<s:submit action="ContractorEditRiskLevel!save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
			</fieldset>
		</s:form>
	</body>
</html>