<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set name="verifiedPercent" value="0" />
<s:if test="auditData">
	{"dateVerified" : "<s:date name="auditData.dateVerified" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />", 
	"who" : "<s:property value="auditData.auditor.name"/>", 
	"percentVerified" :
		<s:iterator value="auditData.audit.operators" id="cao">
			<s:set name="verifiedPercent" value="#cao.percentVerified + #verifiedPercent" />
		</s:iterator>
		<s:set name="verifiedPercent" value="#verifiedPercent / auditData.audit.operators.size" /> 
		<s:property value="#verifiedPercent" />
	}
</s:if>
<s:elseif test="oshaAudit">
	{"dateVerified" : "<s:date name="verifiedDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />", 
	"who" : "<s:property value="auditor.name"/>", 
	"percentVerified" :
		<s:iterator value="oshaAudit.caos" id="cao">
			<s:set name="verifiedPercent" value="#cao.percentVerified + #verifiedPercent" />
		</s:iterator>
		<s:set name="verifiedPercent" value="#verifiedPercent / oshaAudit.caos.size" /> 
		<s:property value="#verifiedPercent" />
	}
</s:elseif>