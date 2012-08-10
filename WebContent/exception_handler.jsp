<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<head>
	<title>
		<s:text name="Exception.PicsError" />
	</title>
	<style type="text/css">
		#response_form
		{
			width: 450px;
		}
		
		#response_form label
		{
			width: 5em;
		}
		
		#response_form input,
		#response_form textarea
		{
			color: #464646;
			font-size: 12px;
			font-weight: bold;
		}
	</style>
</head>

<body>
	<s:if test="hasKey(exceptionTranslationKey + '.title')">
		<h1>
			<s:text name="%{exceptionTranslationKey + '.title'}" />
		</h1>
	</s:if>
	
	<s:if test="hasKey(exceptionTranslationKey + '.information')">
		<s:text name="%{exceptionTranslationKey + '.information'}" />
	</s:if>
	<s:elseif test="!isStringEmpty(exception.message)">
		<s:if test="exception.message.contains('div>')">
			${exception.message}
		</s:if>
		<s:else>
			<div class="error">
				${exception.message}
			</div>
		</s:else>
	</s:elseif>
	<s:else>
		<div class="error">
			<s:text name="Exception.UnexpectedError" />
		</div>
	</s:else>
	
	<s:if test="debugging">
		<p><s:property value="exceptionStack"/></p>
	</s:if>
	<s:else>
		<s:form id="response_form" method="post">
			<s:hidden name="exceptionStack" />
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="Exception.Form.Legend" />
				</h2>
				<div>
					<div style="padding: 2ex;">
						<s:if test="!permissions.loggedIn">
							<label>
								<span>
									<s:text name="User.name" />:
								</span>
							</label>
							<input
								type="text"
								id="user_name"
								size="25" />
							<br />
							<label>
								<span>
									<s:text name="User.email" />:
								</span>
							</label>
							<input
								type="text"
								id="from_address"
								size="25" />
							<br />
						</s:if>
						<label>
							<s:text name="Exception.Optional" />:
						</label>
						<s:text name="Exception.Form.Message" />:<br />
						<label>&nbsp;</label>
						<div>
							<textarea
								id="user_message"
								name="user_message"
								rows="3"
								cols="40"></textarea>
						</div>
					</div>
				</div>
			</fieldset>
			<fieldset class="form submit">
				<input
					class="picsbutton"
					type="button"
					value="&lt;&lt; <s:text name="button.Back" />"
					onclick="history.go(-1)" />
				<s:submit
					method="sendExceptionEmail"
					cssClass="picsbutton"
					value="%{getText('Exception.ReportToPicsEngineers')}" />
			</fieldset>
		</s:form>
	</s:else>
</body>