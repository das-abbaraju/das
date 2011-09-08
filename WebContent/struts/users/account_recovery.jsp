<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:text name="AccountRecovery.title" /></title>
		
		<meta name="accountrecovery" content="Account_Recovery">
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/account_recovery.css?v=<s:property value="version"/>" />
		
		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript" src="js/account_recovery.js"></script>
	</head>
	<body>
		<%-- <s:if test="debugging"><!-- We can remove this once we've release i18n --> --%>
		<ul class="locales">
			<li>
				<a href="?request_locale=en">English</a>
			</li>
			<li>
				<a href="?request_locale=fr">Français</a>
			</li>
			<li>
				<a href="?request_locale=es">Español</a>
			</li>
		</ul> 
		<%-- </s:if> --%>
		
		<h1><s:text name="AccountRecovery.title" /></h1>
		
		<s:form id="accountRecovery">
			<div style="margin-bottom: 10px;">
				<a href="Login.action">&lt;&lt; <s:text name="AccountRecovery.ReturnToLogin" /></a>
			</div>
			
			<div style="width: 500px;">
				<fieldset class="form">
					<h2 class="formLegend"><s:text name="AccountRecovery.title" /></h2>
					
					<ol style="margin-top:7px;">
						<li class="showUser">
							<div>
								<label><s:text name="global.Username" />:</label>
								<s:textfield id="usernameBox" name="username" cssClass="login" />
							</div>
							
							<div class="fieldhelp">
								<h3><s:text name="global.Username" /></h3>
								<p>
									<s:text name="AccountRecovery.Username.fieldhelp" />
								</p>
							</div>
						</li>
						<li class="showEmail">
							<div>
								<label><s:text name="global.Email" />:</label>
								<s:textfield id="emailBox" name="email" cssClass="login" size="28"/>
							</div>
							<div class="fieldhelp">
								<h3><s:text name="global.Email" /></h3>
								<p>
									<s:text name="AccountRecovery.Email.fieldhelp" />
								</p>
							</div>
						</li>
						<li>
							<a class="showUser showPointer"><s:text name="AccountRecovery.ForgotName" /></a>
							<a class="showEmail showPointer"><s:text name="AccountRecovery.ForgotPassword" /></a>
						</li>
						<li>
							<label><s:text name="AccountRecovery.title.Verification" />:</label>
							<s:property value="recaptcha.recaptchaHtml" escape="false"/>
							
							<div class="fieldhelp">
								<h3><s:text name="AccountRecovery.Verification" /></h3>
								<p><s:text name="AccountRecovery.Verification.fieldhelp" /></p>
							</div>
						</li>
					</ol>
				</fieldset>
				
				<fieldset class="form submit">
					<s:submit method="findName" value="%{getText('AccountRecovery.button.FindName')}" cssClass="picsbutton positive showEmail"></s:submit>
					<s:submit method="resetPassword" value="%{getText('AccountRecovery.button.ResetPassword')}" cssClass="picsbutton positive showUser"></s:submit>
				</fieldset>
			</div>
		</s:form>
		
		<s:include value="../actionMessages.jsp"></s:include>
	</body>
</html>