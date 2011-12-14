<#if parameters.label??>
	<#assign labelKey>${parameters.label}</#assign>
<#else>
	<#assign labelKey><@s.property value="getTranslationName(parameters.name)"/></#assign>
</#if>

<@s.if test="hasKey('${labelKey}') && '${labelKey}' != ''">
	<label for="${parameters.id?html}"><@s.text name="${labelKey}"/></label>
</@s.if>
<@s.elseif test="'${labelKey}' != ''">
	<label for="${parameters.id?html}">${labelKey}</label>
</@s.elseif>