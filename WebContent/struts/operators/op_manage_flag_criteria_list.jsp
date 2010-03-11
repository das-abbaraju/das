<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<script type="text/javascript">
$(document).ready(function() {
	$('.datepicker').datepicker();
});
</script>

<table class="report">
	<thead>
		<tr>
			<s:if test="!insurance">
				<th>Category</th>
			</s:if>
			<th>Description</th>
			<th>Flag</th>
			<s:if test="canEdit">
				<th><nobr># Affected</nobr></th>
				<th>Edit</th>
				<th>Remove</th>
			</s:if>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="criteriaList">
			<tr id="<s:property value="id" />" <s:if test="needsRecalc">class="recalc"</s:if>>
				<s:if test="!insurance">
					<td><s:property value="criteria.category" /></td>
				</s:if>
				<td>
					<s:if test="canEdit">
						<span class="hide">
							<a href="#" onclick="submitHurdle(this.parentNode.parentNode); return false;" class="picsbutton">Save</a>
							<s:select list="getAddableFlags(0)" name="newFlag" value="flag"></s:select> flag if
						</span>
						<s:property value="criteria.descriptionBeforeHurdle" />
						<s:if test="criteria.dataType != 'boolean'">
							<span class="hurdle"><b><s:property value="criteriaValue()" /></b></span>
							<s:if test="criteria.dataType == 'number' || criteria.dataType == 'date'">	
								<input type="text" value="<s:property value="criteriaValue()" />" name="newHurdle" size="10"
									class="hide" onkeyup="wait(this.parentNode.parentNode.id, this.value, 500);"
									<s:if test="criteria.dataType == 'date'">class="datepicker"</s:if> />
							</s:if>
							<s:elseif test="criteria.dataType == 'string'">
								<span class="hide">
									<s:radio list="#{'Yes':'Yes','No':'No'}" value="criteriaValue()" 
										onkeyup="wait(this.parentNode.parentNode.id, this.value, 500);"></s:radio>
								</span>
							</s:elseif>
						</s:if>
						<s:property value="criteria.descriptionAfterHurdle" />
					</s:if>
					<s:else>
						<s:property value="replaceHurdle" />
					</s:else>
				</td>
				<td class="center"><span class="hideOld"><s:property value="flag.smallIcon" escape="false" /></span></td>
				<s:if test="canEdit">
					<td class="center">
						<a href="#" onclick="getImpact(<s:property value="id" />); return false;" title="Click to see a list of contractors impacted."
							class="hideOld oldImpact"><s:property value="affected" /></a>
						<span class="newImpact"></span>
					</td>
					<td class="center">
						<a href="#" class="edit" onclick="editCriteria(<s:property value="id" />); return false;"></a>
					</td>
					<td class="center">
						<a href="#" class="remove" onclick="checkSubmit(<s:property value="id" />); return false;"></a>
					</td>
				</s:if>
			
		</s:iterator>
	</tbody>
</table>
<script type="text/javascript">
	var idArray = new Array();
	$('#criteriaDiv tr.recalc').each(function() {
		idArray.push($(this).attr('id'));
	});

	if (idArray[0])
		updateAffected(idArray, 0);
</script>