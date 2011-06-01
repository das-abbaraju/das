<#if parameters.label??>
	<#assign labelKey>${parameters.label}</#assign>
<#else>
	<#assign labelKey><@s.property value="getTranslationName(parameters.name)"/></#assign>
</#if>
<label><@s.text name="${labelKey}"/>:</label>