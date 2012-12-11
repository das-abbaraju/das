<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="permissions.hasPermission(@com.picsauditing.access.OpPerms@SalesCommission)">
	<s:if test="invoiceCommissions.isEmpty()">
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
                        Weight
                    </th>
					<th>
                        Points
                    </th>
					<th>
                        Revenue
                    </th>
				</tr>
			</thead>
            
			<s:iterator value="invoiceCommissions">
				<tr>
					<td>
                        <s:property value="accountUser.account.name" />
                    </td>
					<td>
                        <s:property value="accountUser.user.name" />
                    </td>
                    <td>
                        <s:property value="accountUser.role.description" />
                    </td>
                    <td>
                        <s:property value="accountUser.ownerPercent" />
                    </td>
					<td class="number">
						 <s:number name="points" 
                        		type="number" 
                        		maximumFractionDigits="5" 
                        		minimumFractionDigits="2" 
                        		roundingMode="half-up" />
                    </td>
					<td class="number">
                        <s:set var="invoiceTotal" value="%{invoice.getTotalCommissionEligibleInvoice(false)}" />
                        <s:set var="revenuePercent" value="revenuePercent" />
                        <s:set var="calculatedTotal" value="%{(#invoiceTotal.doubleValue() * revenuePercent)}" />
                        <s:number name="calculatedTotal" 
                        		type="number" 
                        		maximumFractionDigits="2" 
                        		minimumFractionDigits="2" 
                        		roundingMode="half-up" />
                    </td>
				</tr>
			</s:iterator>
		</table>
	</s:else>
</s:if>