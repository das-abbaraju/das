<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="permissions.hasPermission(@com.picsauditing.access.OpPerms@SalesCommission)">
	<s:if test="commissionDetails.isEmpty()">
		<p>
            No Commission Details for this invoice.
        </p>
	</s:if>
	<s:else>
		<table class="allborder breakdown-table">
			<caption>Commissions Breakdown for Sales/Account Manager(s)</caption>
            
			<thead>
				<tr>
					<th>
                        Client Site
                    </th>
                    <th>
                        Name
                    </th>
                    <th>
                        Role
                    </th>
                    <th>
                        Ownership
                    </th>
					<th>
                        Points
                    </th>
					<th>
                        Revenue
                    </th>
				</tr>
			</thead>
            
			<s:iterator value="commissionDetails">
				<tr>
					<td>
						<s:url action="FacilitiesEdit" var="operator_url">
							<s:param name="operator" value="clientSiteId" />
						</s:url>
						
                        <a href="${operator_url}">
                        	<s:property value="clientSite" />
                        </a>
                    </td>
					<td>
                        <s:property value="accountRepresentativeName" />
                    </td>
                    <td>
                        <s:property value="role.description" />
                    </td>
                    <td class="number">
                        <s:property value="weight" />%
                    </td>
					<td class="number">
						 <s:number name="points" 
                        		type="number" 
                        		maximumFractionDigits="2" 
                        		minimumFractionDigits="2" 
                        		roundingMode="half-up" />
                    </td>
					<td class="number">
                        <s:number name="revenue" 
                        		type="number" 
                        		maximumFractionDigits="2" 
                        		minimumFractionDigits="2" 
                        		roundingMode="half-up" />&nbsp;<s:property value="invoice.currency"/>
                    </td>
				</tr>
			</s:iterator>
		</table>
		
		<s:url var="download_commission" action="DownloadCommissionAudit">
			<s:param name="invoiceId" value="invoice.id" />
		</s:url>
		
		<div id="download_commission">
			<a href="${download_commission}"><img src="images/icon_link_excel.png" />Download Breakdown Calculation</a>
		</div>
		
	</s:else>
</s:if>