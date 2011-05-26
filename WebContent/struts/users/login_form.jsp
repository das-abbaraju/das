<%@ taglib prefix="s" uri="/struts-tags"%>

<s:include value="../actionMessages.jsp"></s:include>

<div class="loginForm">
	<s:form id="login">
		<fieldset class="form">
		<h2 class="formLegend">Login to PICS Organizer</h2>
		<ol>
			<li>
				<label>Username:</label>
				<s:textfield id="username" name="username" cssClass="login" tabindex="1"/>
			</li>
			<li>
				<label>Password:</label> <s:password name="password" cssClass="login" tabindex="2"/>
			</li>
		</ol>
		</fieldset>
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" name="button" value="Login" tabindex="3"/>
		</fieldset>
	</s:form>

	<div class="info other">
		<p>Forget your login information? <a href="AccountRecovery.action">Click here to recover it</a></p>
		<p>Are you a contractor? <a href="ContractorRegistration.action">Click to Register your company</a></p>
	</div>
</div>