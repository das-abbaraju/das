<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="competency.category" />: <s:property value="competency.label" /></title>
</head>
<body>
<s:property value="competency.description" escape="false" />
</body>
</html>