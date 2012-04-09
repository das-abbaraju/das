<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="configEnvironment || i18nReady">
	<ul class="locales">
		<s:iterator value="@com.picsauditing.actions.TranslationActionSupport@getSupportedLocales()">
			<li>
				<a href="?request_locale=${language}">
					<s:property value="@com.picsauditing.util.Strings@capitalize(getDisplayLanguage(language))" />
				</a>
			</li>
		</s:iterator>
	</ul> 
</s:if>