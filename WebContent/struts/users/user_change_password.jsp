<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
	<s:include value="../actionMessages.jsp" />
	
	<s:form cssClass="form">
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
				<s:if test="permissions.admin && user.id != u.id" >
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
					<s:password name="password1" label="global.Password" theme="form" />
				</li>
				<li>
					<s:password name="password2" label="ProfileEdit.ConfirmPassword" theme="form" />
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<s:submit value="%{getText('button.Save')}" cssClass="picsbutton positive" method="changePassword" />		
	
			<s:if test="source=='manage' && user.id != u.id">
				<s:if test="hasProfileEdit">
					<s:submit value="%{getText('UsersManage.SendResetPasswordEmail')}" cssClass="btn" method="emailPassword" />
				</s:if>			
			</s:if>
		</fieldset>
	</s:form>
</div>
