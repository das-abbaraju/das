<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
	<thead>
		<tr>
			<th>Category</th>
			<th>Description</th>
			<th>Flag</th>
			<th title="Click percentage to see impact."><nobr>% Affected</nobr></th>
			<th>Remove</th>
		</tr>
	</thead>
	
	<s:iterator value="criteriaList">
		<pics:permission perm="AuditVerification">
		<tr id="<s:property value="id" />">
			<td><s:property value="criteria.category" /></td>
			<td>
				<s:if test="criteria.allowCustomValue">
					<s:property value="criteria.descriptionBeforeHurdle" />
					<s:if test="criteria.dataType == 'boolean'">
						<s:select list="#{'true':'True','false':'False'}" value="criteriaValue()" name="newHurdle"
							onchange="submitHurdle(this);"></s:select>
					</s:if>
					<s:elseif test="criteria.dataType == 'number'">
						<input type="text" value="<s:property value="criteriaValue()" />" size="5" name="newHurdle"
							onchange="submitHurdle(this);" />
					</s:elseif>
					<s:elseif test="criteria.dataType == 'date'">
						<input type="text" class="datepicker" value="<s:property value="criteriaValue()" />" name="newHurdle"
							size="10" onchange="submitHurdle(this);" />
					</s:elseif>
					<s:else>
						<input type="text" value="<s:property value="criteriaValue()" />" size="20" name="newHurdle"
							onchange="submitHurdle(this);" />
					</s:else>
					<s:property value="criteria.descriptionAfterHurdle"/>
				</s:if>
				<s:else>
					<s:property value="criteria.description" />
				</s:else>
			</td>
			<td class="center"><s:property value="flag.smallIcon" escape="false" /></td>
			<td class="center">
				<a href="#" onclick="getImpact(<s:property value="id" />); return false;" title="Click percentage to see impact.">
					<s:property value="getPercentAffected(id)" />%</a>
			</td>
			<td class="center">
				<a href="#" class="remove" onclick="checkSubmit(<s:property value="id" />); return false;"></a>
			</td>
		</tr>
		</pics:permission>
	</s:iterator>
</table>