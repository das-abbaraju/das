<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<pics:permission perm="Translator">
	<s:if test="i18nUsedKeys.size() > 0">
		<a class="preview" href="ManageTranslations.action?button=search<s:iterator value="i18nUsedKeys">&key=<s:property/></s:iterator>">Translate</a>
	</s:if>
</pics:permission>