<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="results">
<s:property value="auditType.auditName"/> &gt;&gt; <s:property value="category"/>|<s:property value="id"/>
</s:iterator>
