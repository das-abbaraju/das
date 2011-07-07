<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<p><label><s:text name="ContractorFacilitiesWidget.FacilityCount"/></label> <s:property value="operators.size" /></p>
<table class="report">
	<thead>
		<tr>
		<th><s:text name="global.Flag"/></th>
		<th><s:text name="global.Facility"/></th>
		<th><s:text name="global.WaitingOn"/></th>
		</tr>
	</thead>
	<s:iterator value="activeOperators">
		<tr>
			<td class="center"><a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="flagColor.smallIcon" escape="false" /></a></td>
			<td><a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="operatorAccount.name" /></a></td>
			<td class="center"><s:text name="%{waitingOn.i18nKey}"/></td>
		</tr>
	</s:iterator>
	<tr><td colspan="3" class="right"><a href="ContractorFacilities.action?id=<s:property value="id" />"><s:text name="ContractorFacilitiesWidget.AddMore"/></a></td>
	</tr>
</table>
