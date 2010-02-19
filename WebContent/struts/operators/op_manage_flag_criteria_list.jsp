<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
	<thead>
		<tr>
			<th>Category</th>
			<th>Description</th>
			<th>Flag</th>
			<th><nobr>% Affected</nobr></th>
			<th>Remove</th>
		</tr>
	</thead>
	
	<s:iterator value="criteriaList">
		<pics:permission perm="AuditVerification">
		<tr>
			<td><s:property value="criteria.category" /></td>
			<td>
				<s:if test="criteria.allowCustomValue">
					<s:property value="criteria.descriptionBeforeHurdle" />
					<s:if test="criteria.dataType == 'boolean'">
						<s:select list="#{'true':'True','false':'False'}" value="criteria.defaultValue"></s:select>
					</s:if>
					<s:elseif test="criteria.dataType == 'number'">
						<input type="text" value="<s:property value="criteria.defaultValue" />" size="5" />
					</s:elseif>
					<s:elseif test="criteria.dataType == 'date'">
						<s:select list="#{'<':'<','>':'>','=':'='}" value="criteria.comparison"></s:select>
						<input type="text" class="datepicker" value="<s:property value="criteria.defaultValue" />" size="10" />
					</s:elseif>
					<s:else>
						<s:select list="#{'=':'=','!=':'!='}" value="criteria.comparison"></s:select>
						<input type="text" value="<s:property value="criteria.defaultValue" />" size="20" />
					</s:else>
					<s:property value="criteria.descriptionAfterHurdle"/>
				</s:if>
				<s:else>
					<s:property value="criteria.description" />
				</s:else>
			</td>
			<td class="center"><s:property value="flag.smallIcon" escape="false" /></td>
			<td class="center">
				<a href="#" onclick="getImpact(<s:property value="id" />); return false;">
					<s:property value="getPercentAffected(id)" />%</a>
			</td>
			<td class="center">
				<a href="#" class="remove" onclick="checkSubmit(<s:property value="id" />);"></a>
			</td>
		</tr>
		</pics:permission>
	</s:iterator>
</table>