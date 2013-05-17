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
            <s:set var="li_dropdown_class" value="''" />
            <s:set var="a_dropdown_class" value="''" />
            <s:set var="a_data_toggle" value="''" />
        </s:else>

		<s:if test="#menu_item.hasTarget()">
			<s:set var="a_target" value="#menu_item.target" />
		</s:if>
		<s:else>
			<s:set var="a_target" value="''" />
		</s:else>

        <li class="${li_dropdown_class}">
            <a id="${menu_item.htmlId}"
                href="${menu_item.url}"
                class="${a_dropdown_class}"
                data-toggle="${a_data_toggle}"
				target="${a_target}"
				<s:if test="#menu_item.htmlId == 'live_chat'">
					onclick="if(navigator.userAgent.toLowerCase().indexOf('opera') != -1 &amp;&amp; window.event.preventDefault) window.event.preventDefault();this.newWindow = window.open('${menu_item.url}&amp;url='+escape(document.location.href)+'&amp;referrer='+escape(document.referrer), 'webim', 'toolbar=0,scrollbars=0,location=0,status=1,menubar=0,width=640,height=480,resizable=1');this.newWindow.focus();this.newWindow.opener=window;return false;"
				</s:if>
            >
                ${menu_item.name}

                <s:if test="#menu_item.level == 1 && #menu_item.htmlId == 'user_menu'">
                    <i class="icon-cog icon-large"></i>
                </s:if>
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