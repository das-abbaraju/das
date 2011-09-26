<#--
/*
 * $Id: optgroup.ftl 817282 2009-09-21 15:07:03Z wesw $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<#if parameters.optGroupInternalListUiBeanList??>
<#assign optGroupInternalListUiBeans=parameters.optGroupInternalListUiBeanList />
<#list optGroupInternalListUiBeans as optGroupInternalListUiBean>
<optgroup 
	<#if optGroupInternalListUiBean.parameters.label??>
	label="${optGroupInternalListUiBean.parameters.label}"
	</#if>
	<#if optGroupInternalListUiBean.parameters.disabled?default(false)>
	disabled="disabled"
	</#if>
<#include "/${parameters.templateDir}/simple/dynamic-attributes.ftl" />
>

<#list optGroupInternalListUiBean.parameters.list as optGroupBean>
<#assign trash=stack.push(optGroupBean) />
	<#assign tmpKey=stack.findValue(optGroupInternalListUiBean.parameters.listKey) />
	<#assign tmpValue=stack.findValue(optGroupInternalListUiBean.parameters.listValue) />
	<#assign tmpKeyStr = tmpKey.toString() />
	<option value="${tmpKeyStr?html}"
	<#if tag.contains(parameters.nameValue, tmpKeyStr) == true>
	selected="selected"
	</#if>
	>${tmpValue?html}
	</option>
<#assign trash=stack.pop() />
</#list>
</optgroup>
</#list>
</#if>