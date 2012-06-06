<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
	<s:include value="../actionMessages.jsp" />
	
	<s:form cssClass="form" id="userSavePassword">
		<s:hidden name="url" />
		<s:hidden name="source" value="%{source}" />
		<fieldset>
			<h2 class="formLegend">
				<s:text name="ProfileEdit.Password.heading" />
			</h2>
	
			<ol>
				<li>
					<div id="username_status"/>
				</li>

				<s:if test="user.id != u.id || permissions.forcePasswordReset" >
					<s:hidden id="u" name="u" value="%{u.id}" />
					<s:hidden name="user" value="%{user.id}" />
				</s:if>
				<s:else>
					<s:hidden id="u" name="u" value="%{u.id}" />
					<s:hidden name="user" value="%{user.id}" />
					<li>
						<s:password name="passwordc" label="global.CurrentPassword" theme="form" />
					</li>
					
				</s:else>
				<li>
					<s:password name="password1" label="global.Password.new" theme="form" />
					<pics:fieldhelp title="Password Requirement">
						<p>
							<s:text name="global.Password.fieldhelp" />
						</p>  
					</pics:fieldhelp>
				</li>
				<li>
					<s:password name="password2" label="ProfileEdit.ConfirmPassword" theme="form" />
					<pics:fieldhelp title="Password Requirement">
						<p>
							<s:text name="global.ConfirmPassword.fieldhelp" />
						</p>  
					</pics:fieldhelp>
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<s:submit value="%{getText('button.Save')}" cssClass="picsbutton positive" method="changePassword" />		
			<s:if test="source=='manage'">
				<a class="cancel-password" href="UsersManage.action?account=<s:property value="account.id"/>&user=<s:property value="user.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>">
					<s:text name="JS.button.Cancel" />
				</a>
			</s:if>
			<s:else>
				<a class="cancel-password" href="ProfileEdit.action"/>
					<s:text name="JS.button.Cancel" />
				</a>
			</s:else>
		</fieldset>
	</s:form>
</div>
