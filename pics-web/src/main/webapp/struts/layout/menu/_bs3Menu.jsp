<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="menu_size" value="menu.children.size()" />
<s:set var="last_menu_index" value="menu.children.size() - 1" />

<s:url action="Search" var="search_url" />

<div id="primary_navigation" class= "bootstrap-menu navbar-fixed-top">
  <div class="container">
    <nav class="navbar navbar-default" role="navigation">
      <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
          <span class="sr-only">Toggle navigation</span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="#"></a>
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
              <input type="text" class="typeahead form-control search-box" placeholder="Search">
              <i class="icon-search icon-large"></i>
            </div>
        </form>
      </s:if>
    </nav>
  </div>
</div>