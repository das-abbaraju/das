<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
	<thead>
		<tr>
			<th>Category</th>
			<th>Description</th>
			<th>Flag</th>
			<th>Add</th>
		</tr>
	</thead>
	
	<s:iterator value="addableCriterias">
		<tr id="<s:property value="id" />">
			<td><s:property value="category" /></td>
			<td>
				<s:if test="allowCustomValue">
					<s:property value="descriptionBeforeHurdle" />
					<s:if test="dataType == 'boolean'">
						<s:select name="newHurdle" list="#{'true':'True','false':'False'}" value="defaultValue"></s:select>
					</s:if>
					<s:elseif test="dataType == 'number'">
						<input name="newHurdle" type="text" value="<s:property value="defaultValue" />" size="5" />
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
			<td class="center">
				<s:select list="#{'Amber':'Amber','Red':'Red'}" name="newFlag" />
			</td>
			<td class="center">
				<a href="#" class="add" onclick="addCriteria(<s:property value="id" />); return false;"></a>
			</td>
		</tr>
	</s:iterator>
</table>