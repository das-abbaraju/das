<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="user_access">
    <h1><s:text name="ManageReports.access.section.people" /></h1>
    
    <ul class="user-list unstyled">
        <s:iterator value="#persons" var="person">
            <s:if test="#person.owner">
                <s:set var="access_type">owner</s:set>
            </s:if>
            <s:elseif test="#person.editable">
                <s:set var="access_type">edit</s:set>
            </s:elseif>
            <s:else>
                <s:set var="access_type">view</s:set>
            </s:else>
            
            <s:if test="permissions.userId == #person.id">
                <s:set var="current_user">data-current-user="true"</s:set>
            </s:if>
            <s:else>
                <s:set var="current_user" value="%{''}" />
            </s:else>
            
            
            <li class="user ${access_type} clearfix" data-user-id="${person.id}" ${current_user}>
                <s:if test="#person.owner">
                    <div class="is-owner pull-right">
                        <s:if test="report.public">
                            <i class="icon-search"></i>
                        </s:if>
                        <i class="icon-key"></i> <s:text name="ManageReports.access.dropDown.owner" />
                    </div>
                </s:if>
                
                <s:include value="/struts/report/manage-report/_access-user-access-options.jsp" />
                
                <div class="summary">
                    <span class="name">${person.name}</span>
                    <p class="location">${person.location}</p>
                </div>
            </li>
        </s:iterator>
    </ul>
</section>