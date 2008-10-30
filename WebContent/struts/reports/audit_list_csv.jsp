<%@ taglib prefix="s" uri="/struts-tags"%>Contractor ID,Name,AuditName,CreatedDate,CompletedDate,ClosedDate,ExpiresDate,AuditStatus,PercentComplete
<s:iterator value="data"><s:property 
value="get('id')" />,"<s:property 
value="get('name')" escape="false" />","<s:property 
value="get('auditName')" escape="false" />","<s:property 
value="get('createdDate')" escape="false" />","<s:property 
value="get('completedDate')" escape="false" />","<s:property 
value="get('closedDate')" escape="false" />","<s:property 
value="get('expiresDate')" escape="false" />","<s:property 
value="get('auditStatus')" escape="false" />","<s:property 
value="get('percentComplete')" escape="false" />%"
</s:iterator>
