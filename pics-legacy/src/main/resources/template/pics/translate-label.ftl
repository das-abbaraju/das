<#if parameters.label??>
	<#assign labelKey>${parameters.label}</#assign>
<#else>
	<#assign labelKey><@s.property value="getTranslationName(parameters.name)"/></#assign>
</#if>

<@s.if test="hasKey('${labelKey}') && '${labelKey}' != ''">
    <#assign translation><@s.text name="${labelKey}"/></#assign>
    <label for="${parameters.id?html}">
        <#if translation?has_content>
            ${translation}
        <#else>
            ${labelKey}
        </#if>
    </label>
</@s.if>
<@s.elseif test="'${labelKey}' != ''">
	<label for="${parameters.id?html}">${labelKey}</label>
</@s.elseif>