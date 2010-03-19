<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<script type="text/javascript">
$(document).ready(function() {
	$('.datepicker').datepicker();
});
</script>

<s:if test="criteriaList.size() > 0">
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
						<s:if test="criteria.dataType != 'boolean' && criteria.allowCustomValue">
							<s:if test="criteria.question.questionType == 'AMBest'">
								<s:if test="criteria.label.contains('Class')">
									<span class="hurdle"><b><s:property value="getAmBestClass(criteriaValue())" /></b></span>
									<s:select name="newHurdle" list="@com.picsauditing.jpa.entities.AmBest@financialMap"
										value="criteriaValue()" cssClass="hide"></s:select>
								</s:if>
								<s:if test="criteria.label.contains('Rating')">
									<span class="hurdle"><b><s:property value="getAmBestRating(criteriaValue())" /></b></span>
									<s:select name="newHurdle" list="@com.picsauditing.jpa.entities.AmBest@ratingMap"
										value="criteriaValue()" cssClass="hide"></s:select>
								</s:if>
							</s:if>
							<s:else>
								<span class="hurdle"><b><s:property value="getFormatted(criteriaValue())" /></b></span>
								<s:if test="criteria.dataType == 'number' || criteria.dataType == 'date'">	
									<input type="text" value="<s:property value="getFormatted(criteriaValue())" />" name="newHurdle" size="10"
										class="hide" onkeyup="wait(this.parentNode.parentNode.id, this.value, 500);"
										<s:if test="criteria.dataType == 'date'">class="datepicker"</s:if> />
								</s:if>
								<s:elseif test="criteria.dataType == 'string'">
									<span class="hide">
										<s:radio list="#{'Yes':'Yes','No':'No'}" value="criteriaValue()" 
											onkeyup="wait(this.parentNode.parentNode.id, this.value, 500);"></s:radio>
									</span>
								</s:elseif>
							</s:else>
						</s:if>
						<s:property value="criteria.descriptionAfterHurdle" />
					</s:if>
					<s:else>
						<s:property value="replaceHurdle" />
					</s:else>
					<s:if test="flag.toString() == 'Green'">
						<div class="alert">Warning: Green flagged criteria will be ignored in flagging contractors. Please remove this criteria.</div>
					</s:if>
				</td>
				<td class="center"><span class="hideOld"><s:property value="flag.smallIcon" escape="false" /></span></td>
				<s:if test="canEdit">
					<td class="center">
						<a href="#" onclick="getImpact(<s:property value="id" />); return false;" title="Click to see a list of contractors impacted."
							class="hideOld oldImpact"><s:property value="affected" /></a>
						<span class="newImpact"></span>
						<s:if test="needsRecalc">
							<script type="text/javascript">updateAffected('<s:property value="id" />');</script>
						</s:if>
					</td>
					<td class="center">
						<a href="#" onclick="editCriteria(<s:property value="id" />); return false;" class="edit"></a>
					</td>
					<td class="center">
						<a href="#" onclick="checkSubmit(<s:property value="id" />); return false;" class="remove"></a>
					</td>
				</s:if>
			
		</s:iterator>
	</tbody>
</table>
</s:if>
<s:else>
<div class="alert">This operator doesn't have any <s:if test="insurance">insurance</s:if><s:else>flag</s:else> criteria.</div>
</s:else>
<s:if test="canEdit">
	<div style="clear: left; margin: 10px 0px;">
		<a href="#" onclick="getAddQuestions(); return false;" class="add">Add New Criteria</a>
	</div>
	<div id="addCriteria" style="display:none"></div>
</s:if>