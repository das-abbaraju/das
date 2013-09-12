<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Cache Statistics - Element List</title>
</head>
<body>

<h1>Cache Statistics - Element List</h1>

<s:if test="elementList.size() == 0">
	<div class="alert">Element List is empty</div>
</s:if>
<s:else>
	<s:div cssClass="alert">
	<h1><s:property value="cacheName" /></h1>
		<s:iterator value="elementList">
			<p><s:property value="toString()" /></p>
		</s:iterator>
		</s:div>
</s:else>

</body>
</html>