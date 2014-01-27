<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="secondary_navigation" class= "bootstrap-menu navbar-fixed-top">
    <div class="container">
        <nav class="navbar navbar-default" role="navigation">
        <ul class="nav navbar-nav">
            <s:set var="menu_items" value="menu.children" />
            <s:include value="/struts/layout/menu/bootstrap3/_contractor-menu-item.jsp" />
        </ul>
    </nav>
    </div>
</div>