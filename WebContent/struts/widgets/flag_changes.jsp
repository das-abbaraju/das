<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="report">
	<thead>
		<tr>
			<th>
                Old
            </th>
			<th>
                New
            </th>
			<th>
                Contractor
            </th>
			<th>
                Operator
            </th>
			<th>
                Last Calc
            </th>
		</tr>
	</thead>
    
	<tbody>
		<s:iterator value="flagChanges" status="stat">
        
            <%-- contractor --%>
            <s:set var="contractor_id" value="get('id')" />
            <s:set var="contractor_name" value="get('name')" />
            <s:set var="contractor_status" value="get('status')" />
            
            <s:url action="ContractorView" var="contractor_url">
                <s:param name="id">${contractor_id}</s:param>
            </s:url>
            
            <s:set var="contractor_class">account${contractor_status}</s:set>
            
            <s:set var="from_flag" value="get('baselineFlag')" />
            <s:set var="to_flag" value="get('flag')" />
            
            <%-- operator --%>
            <s:set var="operator_id" value="get('opId')" />
            <s:set var="operator_name" value="get('opName')" />
            
            <s:url action="OperatorConfiguration" var="operator_url">
                <s:param name="id">${operator_id}</s:param>
            </s:url>
            
            <%-- sync --%>
            <s:set var="recalculation" value="get('lastRecalculation')" />
            
			<tr>
				<td class="center">
                    <s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('baselineFlag').toString(), 'Old Flag: ')" escape="false"/>
                </td>
				<td class="center">
                    <s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('flag').toString(), 'New Flag: ')" escape="false"/>
                </td>
				<td>
                    <a href="${contractor_url}" class="${contractor_class}" title="${contractor_name}">${contractor_name}</a>
                </td>
				<td>
                    <a href="${operator_url}">${operator_name}</a>
                </td>
				<td>
                    ${recalculation} mins ago
                </td>
			</tr>
		</s:iterator>
        
		<s:if test="flagChanges.size == 0">
			<tr>
				<td colspan="5">No flag changes to report</td>
			</tr>
		</s:if>
        
		<tr>
			<td colspan="5" class="center">
                <s:url action="ReportFlagChanges" var="report_flag_url">
                    <s:param name="filter.conAuditorId">${permissions.userId}</s:param>
                </s:url>
                
                <a href="${report_flag_url}">View Report</a>
            </td>
		</tr>
	</tbody>
</table>