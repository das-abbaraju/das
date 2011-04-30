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
<#if parameters.emptyOption?default(false)>
	<option value=""></option>
<#else>
	<@s.if test="hasKey(getTranslationName(parameters.name))">
		<option
		<#if tag.contains(parameters.nameValue, parameters.headerKey) == true>
			selected="selected"
		</#if>
		value="<@s.property value="%{getDefaultValueFromType(parameters.name)}"/>">- <@s.text name="%{getTranslationName(parameters.name)}"/> -
		</option>
	</@s.if>
</#if>
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
	><@s.text name="%{getTranslationName(parameters.name)}.${itemValue}" /></option><#lt/>
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
