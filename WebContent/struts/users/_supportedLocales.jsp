<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="configEnvironment || i18nReady">
	<ul class="locales">
		<s:iterator value="supportedLanguages.visibleLanguagesSansDialect" var="stable_language_key_kalue">
			<li>
				<a href="?request_locale=${stable_language_key_kalue.key}">
					${stable_language_key_kalue.value}
				</a>
			</li>
		</s:iterator>
	</ul> 
</s:if>