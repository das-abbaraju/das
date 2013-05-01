<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="group_access">
    <h1><s:text name="ManageReports.access.section.groups" /></h1>
    
    <ul class="group-list unstyled">
        <s:iterator value="#groups" var="group">
            <s:set var="access_type" value="%{#group.editable ? 'edit' : 'view'}" />
            
            <s:if test="#group.accessType == 'group'">
                <s:set var="group_data">data-group-id="${group.id}"</s:set>
            </s:if>
            <s:else>
                <s:set var="group_data">data-account-id="${group.id}"</s:set>
            </s:else>
        
            <li class="group ${access_type} clearfix" ${group_data}>
                <s:include value="/struts/report/manage-report/_access-group-access-options.jsp" />
                
                <div class="summary">
                    <span class="name">${group.name}</span>
                    <p class="location">${group.location}</p>
                </div>
            </li>
        </s:iterator>
    </ul>
</section>