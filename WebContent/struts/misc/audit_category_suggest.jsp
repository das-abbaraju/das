<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="results">
<s:property value="auditType.name"/> &gt;&gt; <s:property value="category"/>|<s:property value="id"/>
</s:iterator>
