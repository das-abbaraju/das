<#if parameters.label??>
	<label><@s.text name="${parameters.label}"/>:</label>
<#else>
	<label><@s.text name="${scope}.${parameters.name}"/></label>
</#if>