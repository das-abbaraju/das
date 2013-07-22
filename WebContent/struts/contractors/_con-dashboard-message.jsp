<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="result" value="dashboardMessageResult" />
<s:set name="users_with_permissions" value="getOperatorUsersWithPermission('ContractorApproval')"/>
<s:set name="corporate_users_with_permissions" value="getCorporateUsersWithPermission('ContractorApproval')"/>
<s:set name="unapproved_sites" value="getUnapprovedSites('ContractorApproval')"/>

<s:if test="#result.showButtons">
    <div class="alert">
        <p>
            <s:property value="contractor.name"/> <s:text
                name="ContractorView.ContractorDashboard.PendingApproval"/> <s:property
                value="%{co.operatorAccount.name}"/>.
        </p>
        <div class="contractor-status-buttons">
            <button class="btn danger" data-conid="${contractor.id}" data-constatus="N"
                    data-opid="${opID}"><s:text name="AuditStatus.Incomplete.button"/></button>
            <button class="btn success" data-conid="${contractor.id}" data-constatus="Y"
                    data-opid="${opID}"><s:text name="AuditStatus.Approved.button"/></button>
        </div>
    </div>
</s:if>
<s:elseif test="#result.contractorNotApprovedWithAccountManager">
    <div class="alert">
        <p>
            <s:property value="contractor.name"/> <s:text
                name="ContractorView.ContractorDashboard.NotApproved"/> <s:property
                value="%{co.operatorAccount.name}"/>.
        </p>
    </div>
</s:elseif>

<s:elseif test="#result.contractorNotApproved">
    <div class="alert">
        <p>
            <s:property value="contractor.name"/> <s:text
                name="ContractorView.ContractorDashboard.NotApproved"/> <s:property
                value="%{co.operatorAccount.name}"/>.
        </p>
    </div>
</s:elseif>

<s:elseif test="#result.showListOperator">
    <div class="alert">
        <p>
            <s:text name="ContractorView.ContractorDashboard.ApprovalContact"/>:
        </p>
        <ul class="users-with-permissions">
            <s:iterator value="#users_with_permissions" status="loop_index">
                <li>
                    <s:text name="ContractorView.ContractorDashboard.ApprovalContactSites">
                        <s:param><s:property value="name"/></s:param>
                        <s:param><s:property value="account.name"/></s:param>
                    </s:text>
                </li>
            </s:iterator>
        </ul>
    </div>
</s:elseif>

<s:elseif test="#result.showListCorporate">
    <div class="alert">
        <p>
            <s:text name="ContractorView.ContractorDashboard.ApprovalContact"/>:
        </p>
        <ul class="users-with-permissions">
            <s:iterator value="#corporate_users_with_permissions" status="loop_index">
                <li>
                    <s:text name="ContractorView.ContractorDashboard.ApprovalContactSites">
                        <s:param><s:property value="name"/></s:param>
                        <s:param><s:property value="account.name"/></s:param>
                    </s:text>
                </li>
            </s:iterator>
        </ul>
    </div>
</s:elseif>

<s:elseif test="#result.contractorNotApprovedExpectSomeSites"> <!--Corporate Not Approved.  Child sites: Approved, Not Approved -->

	<!-- Show just one client site's data -->
	<s:if test="#unapproved_sites.size() == 1" >
		<div class="alert">
			<p>
				<s:property value="contractor.name"/>
				<s:text name="ContractorView.ContractorDashboard.AwaitingApprovalSingle"/>
			</p>
		</div>
	</s:if>
	<s:else>
		<div class="alert">
			<p>
				<s:property value="contractor.name"/>
				<s:text name="ContractorView.ContractorDashboard.AwaitingApproval"/>
			</p>
		</div>
		<ul class="users-with-permissions">
			<s:iterator value="#unapproved_sites" var="unapproved_site">
				<li>
					<s:property value="#unapproved_site.name"/>
				</li>
			</s:iterator>
		</ul>
	</s:else>

</s:elseif>

<s:elseif test="#result.showEverySiteExceptApprovedOnes"> <!--Corporate Approved.  Child sites: Approved, Not Approved -->
    <div class="alert">
        <p>
            <s:property value="contractor.name"/> <s:text
                name="ContractorView.ContractorDashboard.AwaitingApproval"/>
        </p>

        <ul class="users-with-permissions">
            <s:iterator value="#users_with_permissions" status="loop_index">
                <li>
                    <s:text name="ContractorView.ContractorDashboard.ApprovalContactSites">
                        <s:param><s:property value="name"/></s:param>
                        <s:param><s:property value="account.name"/></s:param>
                    </s:text>
                </li>
            </s:iterator>
        </ul>
    </div>
</s:elseif>

<s:elseif test="#result.showListAccountManager">
    <div class="alert">
        <p>
            <s:property value="contractor.name"/> <s:text
                name="ContractorView.ContractorDashboard.PendingApproval"/> <s:property
                value="%{co.operatorAccount.name}"/>.
        </p>
    </div>
</s:elseif>