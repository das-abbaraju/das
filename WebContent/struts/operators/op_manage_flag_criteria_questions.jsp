<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
	<thead>
		<tr>
			<th><s:text name="button.Add" /></th>
			<th><s:text name="global.Flag" /></th>
			<s:if test="!insurance">
				<th><s:text name="ManageFlagCriteriaOperator.header.Category" /></th>
			</s:if>
			<th><s:text name="global.Type" /></th>
			<th><s:text name="global.Description" /></th>
			<th><s:text name="ManageFlagCriteriaOperator.header.Tag" /></th>
		</tr>
	</thead>
	
	<s:iterator value="addableCriterias">
		<tr id="<s:property value="id" />">
			<td class="center">
				<a href="#" data-id="<s:property value="id" />" class="add"></a>
			</td>
			<td><nobr>
				<span class="flagImage"><s:text name="FlagColor.Red.smallIcon" /></span>
				<s:select list="#{'Red':getTextNullSafe('FlagColor.Red'),'Amber':getTextNullSafe('FlagColor.Amber')}"
					name="newFlag" onchange="getFlag(this)" />
			</nobr></td>
			<s:if test="!insurance">
				<td><s:property value="category" /></td>
			</s:if>
			<td>
				<s:if test="auditType != null">
					<a href="ManageAuditType.action?id=<s:property value="auditType.id" />">
						<s:property value="auditType.name" /></a></s:if>
				<s:elseif test="question != null">
					<a href="ManageAuditType.action?id=<s:property value="question.auditType.id" />">
						<s:property value="question.auditType.name" /></a>
				</s:elseif>
				<s:elseif test="oshaType != null"><s:property value="oshaType.toString()" /></s:elseif>
			</td>
			<td>
				<s:if test="allowCustomValue">
					<s:property value="descriptionBeforeHurdle" />
					<s:if test="dataType == 'boolean'">
						<s:select name="newHurdle" list="#{'true':getTextNullSafe('global.True'),'false':getTextNullSafe('global.False')}" value="defaultValue"></s:select>
					</s:if>
					<s:elseif test="dataType == 'number'">
						<s:if test="question.questionType == 'AMBest'">
							<s:if test="label.contains('Class')">
								<s:select name="newHurdle" list="@com.picsauditing.jpa.entities.AmBest@financialMap" value="defaultValue"></s:select>
							</s:if>
							<s:if test="label.contains('Rating')">
								<s:select name="newHurdle" list="@com.picsauditing.jpa.entities.AmBest@ratingMap" value="defaultValue"></s:select>
							</s:if>
						</s:if>
						<s:else>
							<input name="newHurdle" type="text" value="<s:property value="getFormatted(defaultValue)" />" size="10" />
						</s:else>
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
			<td>
				<s:select list="operator.inheritedTags" name="operatorTag" listKey="id" listValue="tag" headerKey="0"
					headerValue="- %{getText('OperatorTag')} -" />
			</td>
		</tr>
	</s:iterator>
</table>