<#if parameters.label??>
	<#assign labelKey>${parameters.label}</#assign>
<#else>
	<#assign labelKey><@s.property value="getTranslationName(parameters.name)"/></#assign>
</#if>
<#assign translation><@s.text name="${labelKey}"/></#assign>
<label>
    <#if translation?has_content>
        ${translation}:
    <#else>
        ${labelKey}:
    </#if>
</label>