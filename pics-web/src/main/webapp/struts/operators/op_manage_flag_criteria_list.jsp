<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:include value="../actionMessages.jsp" />

<s:if test="criteriaList.size() > 0">
	<table class="report">
		<thead>
			<tr>
				<s:if test="!insurance">
					<th>
						<s:text name="ManageFlagCriteriaOperator.header.Category" />
					</th>
				</s:if>

				<th>
					<s:text name="global.Description" />
				</th>
				<th>
					<s:text name="ManageFlagCriteriaOperator.header.Tag" />
				</th>
				<th>
					<s:text name="global.Flag" />
				</th>

				<s:if test="canEdit">
					<th>
						<s:text name="ManageFlagCriteriaOperator.header.Updated" />
					</th>
					<th>
						<s:text name="button.View" />
					</th>
					<th>
						<s:text name="button.Edit" />
					</th>
					<th>
						<s:text name="button.Remove" />
					</th>
				</s:if>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="criteriaList">
				<tr id="<s:property value="id" />" <s:if test="needsRecalc || (operator.id != account.id)">class="recalc"</s:if>>
					<s:if test="!insurance">
						<td>
							<s:property value="criteria.category" />
						</td>
					</s:if>

					<td>
						<s:if test="canEdit">
							<span class="editable">
								<input type="button" data-id="<s:property value="id" />" class="picsbutton editHurdle" value="<s:text name="button.Save" />" />
								<s:select list="#{'Red':getTextNullSafe('FlagColor.Red'),'Amber':getTextNullSafe('FlagColor.Amber')}" name="newFlag" value="flag" />
								<s:text name="ManageFlagCriteriaOperator.text.FlagOn" />
							</span>

							<s:property value="criteria.descriptionBeforeHurdle" />

							<s:if test="criteria.dataType != 'boolean' && criteria.allowCustomValue">
								<s:if test="criteria.question.questionType == 'AMBest'">
									<s:if test="criteria.label.contains('Class')">
										<span class="hurdle">
											<b><s:property value="getAmBestClass(criteriaValue())" /></b>
										</span>
										<s:select name="newHurdle" list="@com.picsauditing.jpa.entities.AmBest@financialMap" value="criteriaValue()" cssClass="editable"></s:select>
									</s:if>

									<s:if test="criteria.label.contains('Rating')">
										<span class="hurdle">
											<b><s:property value="getAmBestRating(criteriaValue())" /></b>
										</span>
										<s:select name="newHurdle" list="@com.picsauditing.jpa.entities.AmBest@ratingMap" value="criteriaValue()" cssClass="editable"></s:select>
									</s:if>
								</s:if>
								<s:else>
									<span class="hurdle">
										<b><s:property value="getFormatted(criteriaValue())" /></b>
									</span>

									<s:if test="criteria.dataType == 'number' || criteria.dataType == 'date'">
										<input type="text" value="<s:property value="getFormatted(criteriaValue())" />" name="newHurdle" size="10" class="editable" onkeyup="wait(<s:property value="id" />, <s:property value="operator.id" />, this.value, 500);" <s:if test="criteria.dataType == 'date'">class="datepicker"</s:if> />
									</s:if>
									<s:elseif test="criteria.dataType == 'string'">
										<span class="editable">
											<s:radio
												list="#{'Yes':getTextNullSafe('YesNo.Yes'),'No':getTextNullSafe('YesNo.No')}"
												value="criteriaValue()"
												onkeyup="wait(this.parentNode.parentNode.id, this.value, 500);"
												theme="pics"
												cssClass="inline"
											/>
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
							<div class="alert">
								<s:text name="ManageFlagCriteria.alert.GreenFlagCriteria" />
							</div>
						</s:if>
					</td>
					<td class="center">
						<span class="viewable"><s:property value="tag.tag" /></span>
						<span class="editable">
							<s:select list="tags" name="operatorTag" headerKey="0" headerValue="- %{getText('OperatorTag')} -" listKey="id" listValue="tag" value="tag.id" />
						</span>
					</td>
					<td class="center">
						<span class="viewable"><s:text name="%{flag.getI18nKey('smallIcon')}" /></span>
					</td>

					<s:if test="canEdit">
						<td class="nobr">
							<s:text name="ManageFlagCriteriaOperator.text.UpdatedByOn">
								<s:param value="%{updatedBy.name}" />
								<s:param value="%{updateDate}" />
							</s:text>
						</td>
						<td class="center">
							<a href="ManageFlagCriteria!edit.action?criteria=<s:property value="criteria.id"/>" class="preview"></a>
						</td>
						<td class="center">
							<a href="#" data-id="<s:property value="id" />" class="edit"></a>
						</td>
						<td class="center">
							<a href="#" data-id="<s:property value="id" />" class="remove"></a>
						</td>
					</s:if>
				</tr>
			</s:iterator>
		</tbody>
	</table>

    <a href="Report.action?report=2487"><s:text name="OperatorFlagMatrix.title" /></a> &nbsp;|&nbsp;

	<s:if test="canEdit">
		<pics:permission perm="ManageAudits">
			<a href="ManageFlagCriteria.action"><s:text name="ManageFlagCriteriaOperator.link.ManageFlagCriteria" /></a> &nbsp;|&nbsp;
		</pics:permission>

		<a href="#" class="add newCriteria"><s:text name="ManageFlagCriteriaOperator.link.AddNewCriteria" /></a>

		<div id="addCriteria"></div>
	</s:if>
</s:if>
<s:else>
	<div class="alert">
		<s:if test="insurance">
			<s:text name="ManageFlagCriteriaOperator.alert.OperatorHasNoInsuranceCriteria" />
		</s:if>
		<s:else>
			<s:text name="ManageFlagCriteriaOperator.alert.OperatorHasNoFlagCriteria" />
		</s:else>
	</div>

	<s:if test="canEdit">
		<pics:permission perm="ManageAudits">
			<a href="ManageFlagCriteria.action"><s:text name="ManageFlagCriteriaOperator.link.ManageFlagCriteria" /></a> &nbsp;|&nbsp;
		</pics:permission>

		<a href="#" class="add newCriteria"><s:text name="ManageFlagCriteriaOperator.link.AddNewCriteria" /></a>

		<div id="addCriteria"></div>
	</s:if>
</s:else>