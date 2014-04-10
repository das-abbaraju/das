<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="menu_size" value="menu.children.size()" />
<s:set var="last_menu_index" value="menu.children.size() - 1" />
<s:set var="isContractor" value="permissions.contractor" />
<s:set var="userMode" value="permissions.currentMode.toString().toLowerCase()" />

<s:url action="Search" var="search_url" />

<div id="primary_navigation" class= "navbar-fixed-top bootstrap3">
    <div id="ie8_primary_navigation_background"></div>
    <div class="container">
        <nav class="navbar navbar-default" role="navigation">
            <div class="navbar-header">
                <s:if test="#userMode == 'employee'">
                    <a class="navbar-brand" href="/employee-guard/employee/dashboard">Dashboard</a>
                </s:if>
                <s:else>
                    <a class="navbar-brand" href="/">Dashboard</a>
                </s:else>
            </div>
            <ul class="nav navbar-nav">
                <s:set var="menu_items" value="menu.children.subList(0, #last_menu_index)" />
                <s:include value="/struts/layout/menu/_menu-item.jsp" />
            </ul>
            <ul id="user_menu" class="nav navbar-nav navbar-right">
                <s:set var="menu_items" value="menu.children.subList(#last_menu_index, #menu_size)" />
                <s:include value="/struts/layout/menu/_menu-item.jsp" />
            </ul>
            <s:if test="!permissions.contractor">
            <form class="search-form navbar-form navbar-right" action="${search_url}" role="search">
                <div class="form-group">
                    <input type="text" class="input-sm typeahead form-control search-box" placeholder="Search">
                    <i class="icon-search icon-large"></i>
                </div>
                <s:set var="has_search_box_class">has-search-box</s:set>
            </form>
            </s:if>
            <s:else>
                <s:set var="has_search_box_class" value="''" />
            </s:else>
            <a class="contact-us-link ${has_search_box_class}" href="${contact_us_url}"><i class="icon-phone icon-large"></i></a>
        </nav>
    </div>
</div>

<s:if test="showContractorSubmenu">
    <s:action name="Menu!contractorSubmenuBootstrap3" executeResult="true"/>
</s:if>
