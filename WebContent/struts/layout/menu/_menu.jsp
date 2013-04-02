<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="menu_size" value="menu.children.size()" />
<s:set var="last_menu_index" value="menu.children.size() - 1" />

<s:url action="Search" var="search_url" />

<div id="primary_navigation" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            
            <div class="nav-collapse collapse">
                <a class="brand" href="/"></a>
                
                <ul class="nav">
                    <s:set var="menu_items" value="menu.children.subList(0, #last_menu_index)" />
                    <s:include value="/struts/layout/menu/_menu-item.jsp" />
                </ul>
                
                <ul class="nav pull-right">
                    <s:set var="menu_items" value="menu.children.subList(#last_menu_index, #menu_size)" />
                    <s:include value="/struts/layout/menu/_menu-item.jsp" />
                </ul>
                
                <form action="${search_url}" class="navbar-search pull-right">
                    <i class="icon-search icon-large"></i>
                    
                    <input type="hidden" name="button" value="search" />
                    <input type="text" name="searchTerm" class="search-query span2" placeholder="Search" />
                </form>
            </div>
        </nav>
    </div>
</div>