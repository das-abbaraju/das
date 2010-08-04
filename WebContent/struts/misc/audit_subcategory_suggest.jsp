<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="results">
<s:property value="category.auditType.auditName"/> &gt;&gt; <s:property value="category.category"/> &gt;&gt; <s:property value="subCategory"/>|<s:property value="id"/>
</s:iterator>
