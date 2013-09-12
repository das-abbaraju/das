<%@ taglib prefix="s" uri="/struts-tags"%>
<html><head><title>file upload example</title></head>
<body>
<s:actionmessage/>
<s:actionerror/>
	
	<s:form action="FileUploadExample" enctype="multipart/form-data" method="POST">
		<s:file name="theFile"/><br/>
		<s:submit value="Do the upload"/>
	</s:form>


</body>
</html>