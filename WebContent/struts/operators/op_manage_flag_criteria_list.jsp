<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
	<thead>
		<tr>
			<th>Category</th>
			<th>Description</th>
			<th>Flag</th>
			<s:if test="canEditFlags()">
				<th title="Click percentage to see impact."><nobr>% Affected</nobr></th>
				<th>Remove</th>
			</s:if>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="criteriaList">
		<tr id="<s:property value="id" />" class="clickable">
			<td><s:property value="criteria.category" /></td>
			<td onmouseover="$(this).find('.hover').show();" onmouseout="$(this).find('.hover').hide();">
				<a href="#" onclick="editCriteria(this.parentNode); return false;" class="hover"
					style="display:none; float:right;">[edit]</a>
				<a href="#" onclick="submitHurdle(this.parentNode); return false;" class="picsbutton hide">Save</a>
				<s:select list="getAddableFlags(0)" name="newFlag" value="flag" cssClass="hide"></s:select>
				<span class="hide"> flag if </span>
				<s:property value="criteria.descriptionBeforeHurdle" />
				<s:if test="criteria.dataType != 'boolean'">
					<span class="hurdle"><b><s:property value="criteriaValue()" /></b></span>
				</s:if>
				<s:property value="criteria.descriptionAfterHurdle" />
			</td>
			<td class="center"><span class="empty"><s:property value="flag.smallIcon" escape="false" /></span></td>
			<s:if test="canEditFlags()">
				<td class="center">
					<a href="#" onclick="getImpact(<s:property value="id" />); return false;" title="Click percentage to see impact."
						class="empty">
						<s:property value="getPercentAffected(id)" />%</a>
				</td>
				<td class="center">
					<a href="#" class="remove" onclick="checkSubmit(<s:property value="id" />); return false;"></a>
				</td>
			</s:if>
		</tr>
	</s:iterator>
	</tbody>
</table>