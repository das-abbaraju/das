<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="permissions.hasGroup(981)">
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
                        Account Manage/Sales Representative
                    </th>
					<th>
                        Points For Activation
                    </th>
					<th>
                        Revenue Split
                    </th>
				</tr>
			</thead>
            
			<s:iterator value="invoiceCommissions">
				<tr>
					<td>
                        <s:property value="user.name" />
                    </td>
					<td class="number">
                        <s:property value="points" />
                    </td>
					<td class="number">
                        <s:property value="revenuePercent" />
                    </td>
				</tr>
			</s:iterator>
		</table>
	</s:else>
</s:if>