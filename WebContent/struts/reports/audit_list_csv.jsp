<%@ taglib prefix="s" uri="/struts-tags"%><%@ taglib prefix="pics" uri="pics-taglib"%>"Contractor ID","Contractor Name","Audit ID","Audit Type",Status,Created,Completed,Scheduled,Closed,Expires,Complete,Verified,Risk,"Contact Name",Email,Phone,<s:if test="permissions.operator"><s:if test="data.get(0).get('classType') == 'Policy'">"Approval Status","Notes"</s:if></s:if>
<s:iterator value="data"><s:property 
value="get('id')" />,"<s:property 
value="get('name')" escape="false" />","<s:property 
value="get('auditID')" escape="false" />","<s:property 
value="get('auditName')" escape="false" />","<s:property 
value="get('auditStatus')" escape="false" />","<s:date 
name="get('createdDate')" format="M/d/yy" />","<s:date 
name="get('completedDate')" format="M/d/yy" />","<s:date 
name="get('scheduledDate')" format="M/d/yy" />","<s:date 
name="get('closedDate')" format="M/d/yy" />","<s:date 
name="get('expiresDate')" format="M/d/yy" />","<s:property 
value="get('percentComplete')" escape="false" />%","<s:property 
value="get('percentVerified')" escape="false" />%","<s:property 
value="get('riskLevel')" escape="false" />","<s:property 
value="get('contact')" escape="false" />","<s:property 
value="get('email')" escape="false" />","<s:property 
value="get('phone')" escape="false" />",<s:if test="get('classType') == 'Policy' && permissions.operator">"<s:property 
value="get('CaoStatus')" escape="false" />","<s:property 
value="get('notes')" escape="false" />"</s:if>
</s:iterator>