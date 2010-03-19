<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="auditData">
	{"dateVerified" : "<s:date name="auditData.dateVerified" format="MM/dd/yyyy" />", 
	"who" : "<s:property value="auditData.auditor.name"/>", 
	"percentVerified" : <s:property value="auditData.audit.percentVerified"/>}
</s:if>
<s:elseif test="osha">
	{"dateVerified" : "<s:date name="osha.verifiedDate" format="MM/dd/yyyy" />", 
	"who" : "<s:property value="osha.auditor.name"/>", 
	"percentVerified" : <s:property value="osha.conAudit.percentVerified"/>}
</s:elseif>