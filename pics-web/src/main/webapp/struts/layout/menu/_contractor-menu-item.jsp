<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:iterator value="#menu_items" var="menu_item" status="rowstatus">
    <s:if test="#menu_item.name == 'separator'">
        <li class="divider"></li>
    </s:if>
    <s:elseif test="#menu_item.url == null && #menu_item.cssClass == 'label'">
        <li class="nav-header">
            ${menu_item.name}
        </li>
    </s:elseif>
    <s:else>
        <s:if test="#menu_item.hasChildren()">
            <s:if test="#menu_item.level > 1">
                <s:set var="li_dropdown_class">dropdown-submenu</s:set>
            </s:if>
            <s:else>
                <s:set var="li_dropdown_class">dropdown</s:set>
            </s:else>

            <s:set var="a_dropdown_class">dropdown-toggle</s:set>
            <s:set var="a_data_toggle">dropdown</s:set>
        </s:if>
        <s:else>
            <s:set var="li_dropdown_class" value="%{''}" />
            <s:set var="a_dropdown_class" value="%{''}" />
            <s:set var="a_data_toggle" value="%{''}" />
        </s:else>

        <li class="${li_dropdown_class}">
            <a id="${menu_item.htmlId}"
                href="${menu_item.url}"
                class="${a_dropdown_class}"
                data-toggle="${a_data_toggle}"
            >
                ${menu_item.name}
            </a>

            <s:if test="#menu_item.hasChildren()">
                <ul class="dropdown-menu">
                    <s:set var="menu_items" value="#menu_item.children" />
                    <s:include value="/struts/layout/menu/_menu-item.jsp" />
                </ul>
            </s:if>
        </li>
    </s:else>
</s:iterator>