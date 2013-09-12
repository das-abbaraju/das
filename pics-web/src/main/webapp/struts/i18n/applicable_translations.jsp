<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
	<title>Applicable Translations</title>
	<link rel="stylesheet" href="css/forms.css?v=${version}">
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<s:include value="../actionMessages.jsp" />
		<s:form cssClass="form">
			<fieldset class="form">
				<h2>Update Expired Translations</h2>
				<ol>
					<li>
						<label for="types">Available Types</label>
						<s:select
							id="types"
							list="availableTypes"
							name="type" />
					</li>
				</ol>
			</fieldset>
			<fieldset class="submit">
				<ol>
					<li>
						<s:submit
							method="update"
							cssClass="picsbutton positive"
							value="%{getText('button.Save')}" />
					</li>
				</ol>
			</fieldset>
		</s:form>
	</div>
</body>