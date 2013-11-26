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
                <ul class="nav pull-right">
                    <li>
                        <s:url action="ContractorView" var="contractor_view_link">
                            <s:param name="id">
                                ${contractor.id}
                            </s:param>
                        </s:url>
                        <a href="${contractor_view_link}">
                            <strong>
                                ${contractor.name}
                            </strong>
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    </div>
</div>