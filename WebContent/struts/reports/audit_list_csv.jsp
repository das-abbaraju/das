<%@ taglib prefix="s" uri="/struts-tags"%><%@ taglib prefix="pics" uri="pics-taglib"%>"Contractor ID","Contractor Name","Audit ID","Audit Type",Status,Created,Completed,Scheduled,Closed,Expires,Complete,Verified,Risk,"Contact Name",Email,Phone
<s:iterator value="data"><s:property 
value="get('id')" />,"<s:property 
value="get('name')" escape="false" />","<s:property 
value="get('auditID')" escape="false" />","<s:property 
value="get('auditName')" escape="false" />","<s:property 
value="get('auditStatus')" escape="false" />","<s:property 
value="get('createdDate')" escape="false" />","<s:property 
value="get('completedDate')" escape="false" />","<s:property 
value="get('scheduledDate')" escape="false" />","<s:property 
value="get('closedDate')" escape="false" />","<s:property 
value="get('expiresDate')" escape="false" />","<s:property 
value="get('percentComplete')" escape="false" />%","<s:property 
value="get('percentVerified')" escape="false" />%","<s:property 
value="get('riskLevel')" escape="false" />","<s:property 
value="get('contact')" escape="false" />","<s:property 
value="get('email')" escape="false" />","<s:property 
value="get('phone')" escape="false" />"
</s:iterator>