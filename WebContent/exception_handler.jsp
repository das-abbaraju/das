<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<jsp:include page="/struts/jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="/css/forms.css" />
<title>PICS Error</title>
</head>

<body>
<div class="error">
	Oops!! An unexpected error just occurred.<br>
</div>

<s:if test="debugging">
	<p><s:property value="exceptionStack"/></p>
</s:if>
<s:else>
	<s:form id="response_form" method="post" style="width:450px;">
		<s:hidden name="exceptionStack" />
		<fieldset class="form" >
			<h2 class="formLegend">Please help us by reporting this error</h2>
			<div>
				<div style="padding:2ex;">
					<s:if test="!permissions.loggedIn">
						<label style="width:5em;"><span>Name:</span></label>
						<input type="text" id="user_name" size="25" style="color:#464646;font-size:12px;font-weight:bold;"/>
						<br/>
						<label style="width:5em;"><span>Email:</span></label>
						<input type="text" id="from_address" size="25" style="color:#464646;font-size:12px;font-weight:bold;"/>
						<br/>
					</s:if>
					<label style="width:5em;">Optional:</label>Please tell us what you were trying to do:<br/>
					<label style="width:5em;">&nbsp;</label>
					<div>
						<textarea id="user_message" name="user_message" rows="3" cols="40" style="color:#464646;font-size:12px;font-weight:bold;"></textarea>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form submit">
			<input class="picsbutton" type="button" value="&lt;&lt; Back" onclick="history.go(-1)" />
			<s:submit method="sendExceptionEmail" cssClass="picsbutton" value="Report to PICS Engineers" />
		</fieldset>
	</s:form>
</s:else>

</body>
</html>
