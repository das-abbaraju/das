<#if parameters.label??>
	<#assign labelValue>
		<@s.text name="%{parameters.label}"/>
	</#assign>
<#else>
	<#assign labelValue>
		<@s.text name="%{getTranslationName(parameters.name)}"/>
	</#assign>
</#if>
<label>${labelValue}:</label>