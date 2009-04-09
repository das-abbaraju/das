<%@ taglib prefix="s" uri="/struts-tags"%><%@ taglib prefix="pics" uri="pics-taglib"%>"Contractor ID","Contractor Name",Year,Rate,<s:if test="permissions.operator">Flag,"Waiting On",<pics:permission perm="ViewUnApproved">"Work Status",</pics:permission></s:if>"Registration Date",Risk,<s:if test="showContact">"Contact Name",Email,Address,City,State,Zip,Phone,Phone2,"Secondary Contact",Phone,Email,Website,</s:if><s:if test="showTrade">Industry,Trade,</s:if>Fax,"Billing Contact",Phone,Email
<s:iterator value="data"><s:property 
value="get('id')" />,"<s:property 
value="get('name')" escape="false" />",<s:property 
value="get('auditFor')" escape="false"/>,<s:property 
value="get('answer')" escape="false"/>,<s:if test="permissions.operator">"<s:property 
value="get('flag')" escape="false" />","<s:property 
value="get('waitingOn')" escape="false" />",<pics:permission perm="ViewUnApproved">"<s:property 
value="get('workStatus')" escape="false" />",</pics:permission></s:if>"<s:date 
name="get('creationDate')" format="M/d/yy" />","<s:property 
value="get('riskLevel')" escape="false" />",<s:if test="showContact">"<s:property 
value="get('contact')" escape="false" />","<s:property 
value="get('email')" escape="false" />","<s:property 
value="get('address')" escape="false" />","<s:property 
value="get('city')" escape="false" />","<s:property 
value="get('state')" escape="false" />","<s:property 
value="get('zip')" escape="false" />","<s:property 
value="get('phone')" escape="false" />","<s:property 
value="get('phone2')" escape="false" />","<s:property 
value="get('secondContact')" escape="false" />","<s:property 
value="get('secondPhone')" escape="false" />","<s:property 
value="get('secondEmail')" escape="false" />","<s:property 
value="get('web_URL')" escape="false" />",</s:if><s:if test="showTrade">"<s:property 
value="get('industry')" escape="false" />","<s:property 
value="get('main_trade')" escape="false" />",</s:if>"<s:property 
value="get('fax')" escape="false" />","<s:property 
value="get('billingContact')" escape="false" />","<s:property 
value="get('billingPhone')" escape="false" />","<s:property 
value="get('billingEmail')" escape="false" />"
</s:iterator>
