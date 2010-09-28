<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set name="verifiedPercent" value="0" />
<s:if test="auditData">
	{"dateVerified" : "<s:date name="auditData.dateVerified" format="MM/dd/yyyy" />", 
	"who" : "<s:property value="auditData.auditor.name"/>", 
	"percentVerified" :
		<s:iterator value="auditData.audit.operators" id="cao">
			<s:set name="verifiedPercent" value="#cao.percentVerified + #verifiedPercent" />
		</s:iterator>
		<s:set name="verifiedPercent" value="#verifiedPercent / auditData.audit.operators.size" /> 
		<s:property value="#verifiedPercent" />
	}
</s:if>
<s:elseif test="osha">
	{"dateVerified" : "<s:date name="osha.verifiedDate" format="MM/dd/yyyy" />", 
	"who" : "<s:property value="osha.auditor.name"/>", 
	"percentVerified" :
		<s:iterator value="osha.conAudit.operators" id="cao">
			<s:set name="verifiedPercent" value="#cao.percentVerified + #verifiedPercent" />
		</s:iterator>
		<s:set name="verifiedPercent" value="#verifiedPercent / osha.conAudit.operators.size" /> 
		<s:property value="#verifiedPercent" />
	}
</s:elseif>