<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=20091231" />
<script type="text/javascript">
function closePage() {
	window.opener.location.reload();
	self.close();
}
</script>
</head>
<body>
<br />
<div id="main">
	<div id="bodyholder">
		<div id="content">
			<h1>Upload Excel File</h1>
			<s:include value="../actionMessages.jsp" />
			<div>
			<s:form enctype="multipart/form-data" method="POST">
				<div style="background-color: #F9F9F9;">
					<div class="question">
						<label>File:</label>
						<s:file name="file" value="%{file}" size="50"></s:file><br /><br />
						<button class="picsbutton" onclick="closePage(); return false;">Close and Return to Page</button>
						<button class="picsbutton positive" name="button" value="Save" type="submit">Upload</button>
					</div>
				</div>
			</s:form>
			</div>
			<br clear="all" />
		</div>
	</div>
</div>
</body>
</html>