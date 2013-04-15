<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="user_access">
    <h1>People with access</h1>
    
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
            
            <li class="user ${access_type} clearfix" data-user-id="${person.id}">
                <s:if test="#person.owner">
                    <div class="is-owner pull-right">
                        <i class="icon-key"></i> Owner
                    </div>
                </s:if>
                
                <s:include value="/struts/report/manage-report/_access-user-access-options.jsp" />
                
                <div class="summary">
                    <span class="name">${person.userName}</span>
                    <p class="description">${person.location}</p>
                </div>
            </li>
        </s:iterator>
    </ul>
</section>