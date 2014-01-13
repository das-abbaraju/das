<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="menu_size" value="menu.children.size()" />
<s:set var="last_menu_index" value="menu.children.size() - 1" />

<s:url action="Search" var="search_url" />

<div id="primary_navigation" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <button type="button" data-toggle="collapse" data-target=".primary-navigation-items">
                <i class="icon-reorder"></i>
            </button>

            <s:if test="!permissions.contractor">
                <button type="button" data-toggle="collapse" data-target=".primary-navigation-search">
                    <i class="icon-search"></i>
                </button>
            </s:if>

            <%-- FIXME clean up menu links --%>
            <s:if test="permissions.userId > 0">
                <a class="brand" href="/"></a>
            </s:if>
            <s:else>
                <a class="brand" href="/employee-guard/employee/dashboard"></a>
            </s:else>

            <div class="nav-collapse collapse primary-navigation-items">
                <ul class="nav pull-left">
                    <s:set var="menu_items" value="menu.children.subList(0, #last_menu_index)" />
                    <s:include value="/struts/layout/menu/_menu-item.jsp" />
                </ul>
            </div>

            <div class="nav-collapse collapse primary-navigation-items">
                <ul class="nav pull-right">
                    <s:set var="menu_items" value="menu.children.subList(#last_menu_index, #menu_size)" />
                    <s:include value="/struts/layout/menu/_menu-item.jsp" />
                </ul>
            </div>

            <s:if test="!permissions.contractor">
                <div class="nav-collapse collapse primary-navigation-search">
                    <form action="${search_url}" class="navbar-search pull-right">
                        <input type="hidden" name="button" value="search" />
                        <input type="text" name="searchTerm" class="search-query span2" placeholder="Search"
                               autocomplete="off" />
                        <i class="icon-search icon-large"></i>
                    </form>
                </div>
            </s:if>
        </nav>
    </div>
</div>

<%--     <s:if test="showContractorSubMenu"> --%>
<s:include value="/struts/layout/menu/bootstrap2/_contractor-menu.jsp" />
<%--    </s:if> --%>