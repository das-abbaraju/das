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
			<tr<s:if test="canEditFlags()"> id="<s:property value="id" />" class="clickable"</s:if>>
				<td><s:property value="criteria.category" /></td>
				<td<s:if test="canEditFlags()"> onmouseover="$(this).find('.hover').show();" onmouseout="$(this).find('.hover').hide();"</s:if>>
					<s:if test="canEditFlags()">
						<a href="#" onclick="editCriteria(this.parentNode); return false;" class="hover"
							style="display:none; float:right;">[edit]</a>
						<span class="hide">
							<a href="#" onclick="submitHurdle(this.parentNode.parentNode); return false;" class="picsbutton">Save</a>
							<s:select list="getAddableFlags(0)" name="newFlag" value="flag"></s:select> flag if
						</span>
						<s:property value="criteria.descriptionBeforeHurdle" />
						<s:if test="criteria.dataType != 'boolean'">
							<span class="hurdle"><b><s:property value="criteriaValue()" /></b></span>
							<input type="text" value="<s:property value="criteriaValue()" />" name="newHurdle" size="5"
								class="hide" onkeyup="wait(this.parentNode.parentNode.id, this.value, 500);" />
						</s:if>
						<s:property value="criteria.descriptionAfterHurdle" />
					</s:if>
					<s:else>
						<s:property value="replaceHurdle()" />
					</s:else>
				</td>
				<td class="center"><span class="hideOld"><s:property value="flag.smallIcon" escape="false" /></span></td>
				<s:if test="canEditFlags()">
					<td class="center">
						<a href="#" onclick="getImpact(<s:property value="id" />); return false;" title="Click percentage to see impact."
							class="hideOld">
							<s:property value="getPercentAffected(id)" />%</a>
						<span class="newImpact"></span>
					</td>
					<td class="center">
						<a href="#" class="remove" onclick="checkSubmit(<s:property value="id" />); return false;"></a>
					</td>
				</s:if>
			</tr>
		</s:iterator>
	</tbody>
</table>