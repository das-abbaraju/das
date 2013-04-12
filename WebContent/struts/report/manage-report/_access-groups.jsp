<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="group_access">
    <h1>Groups with access</h1>
    
    <ul class="group-list unstyled">
        <s:iterator value="#groups" var="group">
            <s:set var="access_type" value="%{#group.editable ? 'edit' : 'view' }" />
        
            <li class="group ${access_type} clearfix">
                <s:include value="/struts/report/manage-report/_access-group-access-options.jsp" />
                
                <div class="summary">
                    <span class="name">${group.accountName}</span>
                    <p class="description">${group.location}</p>
                </div>
            </li>
        </s:iterator>
    </ul>
</section>