<#include "/${parameters.templateDir}/form/translate-label.ftl" />
<#include "/${parameters.templateDir}/simple/textarea.ftl" />

<#if parameters.required??><span class="redMain">*</span></#if>

<@s.fielderror fieldName="${parameters.name}" />