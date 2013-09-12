<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:set name="colspan" value="2" />
<s:if test="affected.get(0).flagData.criteria.allowCustomValue">
	<s:set name="colspan" value="#colspan + 1" />
</s:if>
<table class="report" style="margin-left: 20px;">
	<thead>
		<tr>
			<th colspan="<s:property value="#colspan" />"><s:property value="flagCriteriaOperator.replaceHurdle"/></th>
			<s:if test="override">
				<th><s:text name="ManageFlagCriteriaOperator.header.ForcedFlag" /></th>
			</s:if>
		</tr>
	</thead>
	<tbody>
		<s:if test="affected.size() == 0">
			<tr>
				<td colspan="<s:property value="#colspan + (override ? 1 : 0)" />" class="center">
					<s:text name="ManageFlagCriteriaOperator.message.NoContractorsAffected" />
				</td>
			</tr>
		</s:if>
		<s:else>
			<s:iterator value="affected" status="stat" id="fdo">
				<tr>
					<td><s:property value="#stat.count" /></td>
					<td><a href="ContractorView.action?id=<s:property value="flagData.contractor.id" />">
						<s:property value="flagData.contractor.name" /></a></td>
					<s:if test="flagData.criteria.allowCustomValue">
						<s:if test="flagData.criteria.description.contains('fatalities')">
							<td><s:property value="@com.picsauditing.util.Strings@trimTrailingZeros(flagData.criteriaContractor.answer)"/></td>
						</s:if>
						<s:elseif test="flagData.criteria.dataType == 'number'">
							<td class="right"><s:property value="@com.picsauditing.util.Strings@formatDecimalComma(flagData.criteriaContractor.answer)"/></td>
						</s:elseif>
						<s:else>
							<td><s:property value="flagData.criteriaContractor.answer"/></td>
						</s:else>
					</s:if>
					<s:if test="override">
						<td class="center">
							<s:text name="FlagColor.%{#fdo.forcedFlag}.smallIcon" />
						</td>
					</s:if>
				</tr>
			</s:iterator>
			<tr>
				<td colspan="<s:property value="#colspan + (override ? 1 : 0)" />" class="center">
					<a href="#" data-fco="<s:property value="fcoID"/>" data-op="<s:property value="opID" />" class="excel">
						<s:text name="ManageFlagCriteriaOperator.link.DownloadList" />
					</a>
				</td>
			</tr>
		</s:else>
	</tbody>
</table>