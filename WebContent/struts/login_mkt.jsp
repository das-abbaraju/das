<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="permissions.loggedIn">
	<s:url action="Home" id="home"></s:url>
	<a href="<s:property value="#home"/>">Return to PICS Online</a>
</s:if>
<s:else>
<table border="0" cellspacing="0" cellpadding="1">
	<tr>
		<td height="5"></td>
		<td></td>
	</tr>
	<tr>
		<td align="right" valign="middle">
		<p><img src="images/login_user.gif" alt="User Name" width="50" height="9">&nbsp;</p>
		</td>
		<td valign="middle"><input name="username" type="text" class="loginForms" size="9"></td>
	</tr>
	<tr>
		<td align="right" valign="middle"><img src="images/login_pass.gif" alt="Password" width="50" height="9">&nbsp;</td>
		<td valign="middle"><input name="password" type="password" class="loginForms" size="9"></td>
	</tr>
	<tr>
		<td class="forgotpassword" valign="middle">
			<s:url id="forgot" value="app/forgot_password.jsp"/>
			<a href="<s:property value="#forgot"/>">
				Forgot<br>
				Password</a>
		</td>
		<td><input name="Submit" type="image" src="images/button_login.jpg" width="65" height="28" border="0">
		</td>

	</tr>
	<tr>
		<td colspan="2" class="blueMain"></td>
	</tr>
</table>
</s:else>