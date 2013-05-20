<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="secondary_navigation" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <button type="button" data-toggle="collapse" data-target=".secondary-navigation-items">
                <i class="icon-reorder"></i>
            </button>
            
            <ul class="nav pull-right account">
                <li>
                    <span>Ancon Marine</span>
                </li>
            </ul>
            
            <div class="nav-collapse collapse secondary-navigation-items">
                <ul class="nav">
                    <s:set var="menu_items" value="menu.children" />
                    <s:include value="/struts/layout/menu/_contractor-menu-item.jsp" />
                </ul>
            </div>
        </nav>
    </div>
</div>