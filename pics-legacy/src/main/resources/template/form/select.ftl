<#include "/${parameters.templateDir}/form/translate-label.ftl" />
<#setting number_format="#.#####">
<select<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.get("size")??>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.tabindex??>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.id??>
 id="${parameters.id?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/css.ftl" />
<#if parameters.title??>
 title="${parameters.title?html}"<#rt/>
</#if>
<#if parameters.multiple?default(false)>
 multiple="multiple"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
<#include "/${parameters.templateDir}/simple/dynamic-attributes.ftl" />
>
<option
<#if parameters.emptyOption?default(false)>
	value="">
<#elseif parameters.headerKey?? && parameters.headerValue??>
	<#if tag.contains(parameters.nameValue, parameters.headerKey) == true>
	selected="selected"
	</#if>
	value="${parameters.headerKey?html}"
	>- <@s.text name="%{parameters.headerValue}"/> -
<#else>
	<#if tag.contains(parameters.nameValue, parameters.headerKey) == true>
	selected="selected"
	</#if>
	value="${parameters.headerKey?default("")}"
	>- <@s.text name="${labelKey}"/> -
</#if>
</option>
<@s.iterator value="parameters.list">
		<#if parameters.listKey??>
			<#if stack.findValue(parameters.listKey)??>
			  <#assign itemKey = stack.findValue(parameters.listKey)/>
			  <#assign itemKeyStr = stack.findString(parameters.listKey)/>
			<#else>
			  <#assign itemKey = ''/>
			  <#assign itemKeyStr = ''/>
			</#if>
		<#else>
			<#assign itemKey = stack.findValue('top')/>
			<#assign itemKeyStr = stack.findString('top')>
		</#if>
		<#if parameters.listValue??>
			<#if stack.findString(parameters.listValue)??>
			  <#assign itemValue = stack.findString(parameters.listValue)/>
			<#else>
			  <#assign itemValue = ''/>
			</#if>
		<#else>
			<#assign itemValue = stack.findString('top')/>
		</#if>
	<option value="${itemKeyStr?html}"<#rt/>
		<#if tag.contains(parameters.nameValue, itemKey) == true>
 selected="selected"<#rt/>
		</#if>
	><@s.if test="isTranslatable(itemValue)">
		<@s.text name="%{itemValue.i18nKey}"/>
	</@s.if>
	<@s.elseif test="hasKey('${itemValue}')">
		<@s.text name="${itemValue}"/>
	</@s.elseif>
	<@s.else>
		${itemValue?html}
	</@s.else></option><#lt/>
</@s.iterator>

<#include "/${parameters.templateDir}/simple/optgroup.ftl" />

</select>
<#if parameters.multiple?default(false)>
<input type="hidden" id="__multiselect_${parameters.id?html}" name="__multiselect_${parameters.name?html}" value=""<#rt/>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
 />
</#if>

<#if parameters.required??><span class="redMain">*</span></#if>

<@s.fielderror fieldName="${parameters.name}" />