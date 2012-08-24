<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="co.operatorAccount.id == permissions.accountId">
    <s:if test="permissions.hasPermission('ViewUnApproved') || (permissions.approvesRelationships && permissions.hasPermission('ContractorApproval'))">
        <s:if test="co.workStatusPending">
            <div class="alert">
                <p>
                    <s:property value="contractor.name" /> <s:text name="ContractorView.ContractorDashboard.PendingApproval" /> <s:property value="%{co.operatorAccount.name}" />.
                </p>
                
                <s:if test="permissions.approvesRelationships && permissions.hasPermission('ContractorApproval')">
                    <p>
                        <s:text name="ContractorView.ContractorDashboard.ApproveContractor" />
                    </p>
                    <div class="contractor-status-buttons">
                        <button class="btn danger" data-conid="${contractor.id}" data-constatus="N" data-opid="${opID}"><s:text name="AuditStatus.Incomplete.button" /></button>
                        <button class="btn success" data-conid="${contractor.id}" data-constatus="Y" data-opid="${opID}"><s:text name="AuditStatus.Approved.button" /></button>
                    </div>
                </s:if>
                <s:else>
                    <s:set name="users_with_permissions" value="getUsersWithPermission('ContractorApproval')" />
                    
                    <s:if test="#users_with_permissions.isEmpty()">
                        <p>
                            <s:text name="ContractorView.ContractorDashboard.PICSManagerContact">
                                <s:param>${picsRepresentativeForOperator.name}</s:param>
                            </s:text>
                        </p>
                    </s:if>
                    <s:else>
                        <p>
                            <s:text name="ContractorView.ContractorDashboard.ApprovalContact" />:
                        </p>
                        <ul class="users-with-permissions">
                            <s:iterator value="#users_with_permissions" status="loop_index">
                                <li>
                                    <s:property value="name" />
                                </li>
                            </s:iterator>
                        </ul>
                    </s:else>
                </s:else>
            </div>
        </s:if>
        <s:elseif test="co.workStatusRejected">
            <div class="alert">
                <p>
                    <s:property value="contractor.name" /> <s:text name="ContractorView.ContractorDashboard.NotApproved" />  <s:property value="%{co.operatorAccount.name}" />.
                </p>
            </div>
        </s:elseif>
    </s:if>
</s:if>