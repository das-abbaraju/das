<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            
            <div class="nav-collapse collapse">
                <a class="brand" href="/"><img src="/v7/img/logo.svg" /></a>
                
                <ul class="nav">
                    <s:set var="menu_items" value="menu.children.subList(0, 6)" />
                    <s:include value="/struts/layout/menu/_menu-item.jsp" />
                </ul>
                
                <ul class="nav pull-right">
                    <s:set var="menu_items" value="menu.children.subList(6, 7)" />
                    <s:include value="/struts/layout/menu/_menu-item.jsp" />
                </ul>
                
                <form class="navbar-search pull-right">
                    <i class="icon-search icon-large"></i>
                    <input type="text" class="search-query span2" placeholder="Search" />
                </form>
            </div>
        </nav>
    </div>
</div>