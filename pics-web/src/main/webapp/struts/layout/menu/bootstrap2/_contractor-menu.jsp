<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="secondary_navigation" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <div class="nav-collapse collapse primary-navigation-items">
                <ul class="nav pull-left">
                    <s:set var="menu_items" value="getContractorMenu(contractor).children" />
                    <s:include value="/struts/layout/menu/bootstrap2/_contractor-menu-item.jsp" />
                </ul>
            </div>
        </nav>
    </div>
</div>