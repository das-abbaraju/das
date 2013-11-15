<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:iterator value="#menu_items" var="menu_item" status="rowstatus">
    <s:if test="#menu_item.name == 'separator'">
        <li class="divider"></li>
    </s:if>
    <s:elseif test="#menu_item.url == null && #menu_item.cssClass == 'label'">
        <li class="mm-label"><span>${menu_item.name}</span></li>
    </s:elseif>
    <s:else>
        <s:set var="a_target" value="%{#menu_item.hasTarget() ? #menu_item.target : ''}" />

        <%-- chat settings --%>
        <s:if test="#menu_item.htmlId == 'live_chat'">
            <s:set var="chat_onclick">onclick="if(navigator.userAgent.toLowerCase().indexOf('opera') != -1 &amp;&amp; window.event.preventDefault) window.event.preventDefault();this.newWindow = window.open('${menu_item.url}&amp;url='+escape(document.location.href)+'&amp;referrer='+escape(document.referrer), 'webim', 'toolbar=0,scrollbars=0,location=0,status=1,menubar=0,width=640,height=480,resizable=1');this.newWindow.focus();this.newWindow.opener=window;return false;"</s:set>
        </s:if>
        <s:else>
            <s:set var="chat_onclick" value="%{''}" />
        </s:else>

        <%-- user menu settings --%>
        <s:set var="is_user_menu" value="#menu_item.level == 1 && #menu_item.htmlId == 'user_menu'" />
        <s:set var="switch_to_class" value="%{#is_user_menu && permissions.switchedToUserName != null ? 'switch-to' : ''}" />

        <li class="${switch_to_class}">
            <span>
                <a id="${menu_item.htmlId}" href="${menu_item.url}" target="${a_target}" ${chat_onclick}>
                    <s:if test="is_user_menu">
                        <s:if test="permissions.switchedToUserName != null">
                            <div class="account">
                                <span>Switched to<br /></span>${menu_item.name}
                            </div>
                            <i class="icon-cog icon-large"></i>
                        </s:if>
                        <s:else>
                            ${menu_item.name}
                            <i class="icon-cog icon-large"></i>
                        </s:else>
                    </s:if>
                    <s:else>
                        ${menu_item.name}
                    </s:else>
                </a>
            </span>
            <s:if test="#menu_item.hasChildren()">
                <ul>
                    <s:set var="menu_items" value="#menu_item.children" />
                    <s:include value="/struts/layout/menu/_mobile-menu-item.jsp" />
                </ul>
            </s:if>
        </li>
    </s:else>
</s:iterator>