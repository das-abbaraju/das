<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="menu_size" value="menu.children.size()" />
<s:set var="last_menu_index" value="menu.children.size() - 1" />

<s:include value="/struts/layout/menu/_mobile-search-result-item.tpl" />

<!-- don't add width, padding, border, or margin to wrapper div -->
<div id="mobile_menu">
    <div id="mobile_menu_navigation" class="loading">
        <ul>
            <s:set var="menu_items" value="menu.children.subList(0, #last_menu_index)" />
            <s:include value="/struts/layout/menu/_mobile-menu-item.jsp" />
        </ul>
    </div>
    <div id="mobile_menu_search" class="loading">
        <ul>
            <li>
            </li>
        </ul>
    </div>
    <div id="page">
        <div class="header">
            <a href="#mobile_menu_navigation"><i class="icon-reorder"></i></a>
            <a href="#mobile_menu_search" class="right"><i class="icon-search"></i></a>
        </div>