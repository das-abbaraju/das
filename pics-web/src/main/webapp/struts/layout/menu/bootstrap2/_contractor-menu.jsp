<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="last_menu_index" value="menu.children.size()"/>

<div id="secondary_navigation" class="navbar navbar-fixed-top">
     <div id="ie8_secondary_navigation_background"></div>
     <div class="navbar-inner">
        <nav class="container">
            <div class="primary-navigation-items">
                <ul class="nav pull-left">
                    <s:set var="menu_items" value="menu.children.subList(0, #last_menu_index)"/>
                    <s:include value="/struts/layout/menu/bootstrap2/_contractor-menu-item.jsp"/>
                </ul>
            </div>
            <div class="primary-navigation-items">
                <ul class="nav pull-right">
                    <li>
                        <s:url action="ContractorView" var="contractor_submenu_contractor_view_link">
                            <s:param name="id">${contractor.id}</s:param>
                        </s:url>
                        <a href="${contractor_submenu_contractor_view_link}"><strong>${contractor.name}</strong></a>
                    </li>
                </ul>
            </div>
        </nav>
    </div>
</div>