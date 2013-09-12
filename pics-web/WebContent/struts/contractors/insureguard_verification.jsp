<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>
			<s:property value="subHeading" />
		</title>
		
		<s:include value="../jquery.jsp"/>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
		<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" /> 
		<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
		
		<style type="text/css">
			small {
				font-size: smaller;
			}
			.buttonRow {
				float: right;
			}
		</style>
	</head>
	<body>
		<s:include value="conHeader.jsp" />
		
		<s:if test="caowList.size > 0">
            <s:form>
                <s:hidden name="contractor" />
				<div id="emailPreview">
                    <s:property value="previewEmail.subject" />
					<br/>
                        <iframe width="80%" height="500px" srcdoc="<s:property value='previewEmail.body' />" ></iframe>
				</div>
				<s:submit value="Send Email" method="sendEmail" cssClass="picsbutton positive" />
			</s:form>
		</s:if>
		<s:else>
			<div class="alert">No rejected insurance policies found.</div>
		</s:else>
	</body>
</html>