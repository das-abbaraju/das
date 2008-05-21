<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<body>

<label>
You have <s:property value="openTasks.size"/> 
outstanding account task<s:if test="openTasks.size != 1">s</s:if>
</label>

<ol>
<s:iterator value="openTasks">
	<li><s:property escape="false"/></li>
</s:iterator>
</ol>

</body>
</html>
