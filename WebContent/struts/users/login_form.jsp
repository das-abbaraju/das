<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="loginMessages">
<s:include value="../actionMessages.jsp"></s:include>
</div>

<div class="loginForm">
	<s:form id="login">
		<fieldset class="form">
		<h2 class="formLegend"><s:text name="Login.h1" /></h2>
		<ol>
			<li>
				<label><s:text name="User.username" />:</label>
				<s:textfield id="username" name="username" cssClass="login" tabindex="1"/>
			</li>
			<li>
				<label><s:text name="global.Password" />:</label>
				<s:password name="password" cssClass="login" tabindex="2"/>
			</li>
		</ol>
		</fieldset>
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" name="button" value="<s:text name="global.Login" />" tabindex="3"/>
		</fieldset>
	</s:form>

	<div class="info other">
		<p><s:text name="Login.forgot?" /> <a href="AccountRecovery.action">Click here to recover it</a></p>
		<p>Are you a contractor? <a href="ContractorRegistration.action">Click to Register your company</a></p>
	</div>
</div>