<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="menu_size" value="menu.children.size()"/>
<s:set var="last_menu_index" value="menu.children.size() - 1"/> <%--fixme: don't make another unnecessary call--%>
<s:set var="has_contractor_menu">
    ${has_contractor_menu}
</s:set>

<div id="primary_navigation" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <%-- FIXME clean up menu links --%>
            <s:if test="permissions.userId > 0">
                <a class="brand" href="/"></a>
            </s:if>
            <s:else>
                <a class="brand" href="/employee-guard/employee/dashboard"></a>
            </s:else>

            <div class="primary-navigation-items">
                <ul class="nav pull-left">
                    <s:set var="menu_items" value="menu.children.subList(0, #last_menu_index)"/>
                    <s:include value="/struts/layout/menu/_menu-item.jsp"/>
                </ul>
            </div>

            <div class="primary-navigation-items">
                <ul class="nav pull-right">
                    <s:set var="menu_items" value="menu.children.subList(#last_menu_index, #menu_size)"/>
                    <s:include value="/struts/layout/menu/_menu-item.jsp"/>
                </ul>
            </div>

            <s:if test="!permissions.contractor">
                <div class="primary-navigation-search">
                    <form action="${search_url}" class="navbar-search pull-right">
                        <input type="hidden" name="button" value="search"/>
                        <input type="text" name="searchTerm" class="search-query span2" placeholder="Search"
                               autocomplete="off"/>
                        <i class="icon-search icon-large"></i>
                    </form>
                </div>
                <s:set var="has_search_box_class">has-search-box</s:set>
            </s:if>
            <s:else>
                <s:set var="has_search_box_class" value="''" />
            </s:else>

            <div>
                <a class="contact-us-link ${has_search_box_class}" href="${contact_us_url}"><i class="icon-phone icon-large"></i></a>
            </div>
        </nav>
    </div>
</div>

<s:if test="showContractorSubmenu">
    <s:action name="Menu!contractorSubmenu" executeResult="true"/>
</s:if>