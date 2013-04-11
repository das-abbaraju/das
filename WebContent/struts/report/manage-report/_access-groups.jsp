<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="group_access">
    <h1>Groups with access</h1>
    
    <ul class="group-list unstyled">
        <s:set var="groups" value="groupAccessList" />
        
        <s:iterator value="#groups" var="group">
            <s:set var="access" value="%{#group.editable ? 'edit' : 'view' }" />
        
            <li class="group ${access} clearfix">
                <s:include value="/struts/report/manage-report/_access-group-access-options.jsp" />
                
                <div class="summary">
                    <a href="#" class="name">${group.accountName}</a>
                    <p class="description">${group.location}</p>
                </div>
            </li>
        </s:iterator>
    </ul>
</section>