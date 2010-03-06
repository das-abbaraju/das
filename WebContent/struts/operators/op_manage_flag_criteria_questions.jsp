<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
	<thead>
		<tr>
			<th>Add</th>
			<th>Flag</th>
			<s:if test="!insurance">
				<th>Category</th>
			</s:if>
			<th>Description</th>
		</tr>
	</thead>
	
	<s:iterator value="addableCriterias">
		<tr id="<s:property value="id" />">
			<td class="center">
				<a href="#" class="add" onclick="addCriteria(<s:property value="id" />); return false;"></a>
			</td>
			<td><nobr>
				<s:if test="getAddableFlags(id).size() == 1">
					<span class="flagImage"><s:property value="getAddableFlags(id).get(0).smallIcon" escape="false" /></span>
					<input type="hidden" name="newFlag" value="<s:property value="getAddableFlags(id).get(0)" />" />
				</s:if>
				<s:else>
					<span class="flagImage"><s:property value="getAddableFlags(id).get(0).smallIcon" escape="false" /></span>
					<s:select list="getAddableFlags(id)" name="newFlag" onchange="getFlag(this)" />
				</s:else>
			</nobr></td>
			<s:if test="!insurance">
				<td><s:property value="category" /></td>
			</s:if>
			<td>
				<s:if test="allowCustomValue">
					<s:property value="descriptionBeforeHurdle" />
					<s:if test="dataType == 'boolean'">
						<s:select name="newHurdle" list="#{'true':'True','false':'False'}" value="defaultValue"></s:select>
					</s:if>
					<s:elseif test="dataType == 'number'">
						<input name="newHurdle" type="text" value="<s:property value="defaultValue" />" size="10" />
					</s:elseif>
					<s:elseif test="dataType == 'date'">
						<s:property value="comparison" />
						<input name="newHurdle" type="text" class="datepicker" value="<s:property value="defaultValue" />" size="10" />
					</s:elseif>
					<s:else>
						<s:select list="#{'=':'=','!=':'!='}" value="comparison"></s:select>
						<input name="newHurdle" type="text" value="<s:property value="defaultValue" />" size="20" />
					</s:else>
					<s:property value="descriptionAfterHurdle"/>
				</s:if>
				<s:else>
					<s:property value="description" />
				</s:else>
			</td>
		</tr>
	</s:iterator>
</table>