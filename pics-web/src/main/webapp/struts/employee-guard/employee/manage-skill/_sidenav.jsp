<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<nav id="side-navigation" class="navbar" role="navigation" data-spy="affix">
    <ul class="nav" role="menu">
        <li class="active">
            <a href="#">All</a>
        </li>
        <li class="nav-divider"></li>
        <s:iterator var="company_skill_info" value="companySkillInfoList">
            <li>
                <a href="#">${company_skill_info.accountModel.name}</a>
            </li>
        </s:iterator>
    </ul>
</nav>
<%--
<nav id="side-navigation" class="navbar" role="navigation" data-spy="affix">
    <ul class="nav" role="menu">
        <li class="active">
            <a href="#">All</a>
        </li>
        <li class="nav-divider"></li>
        <li>
            <a href="#dynamicreporting">BASF Houston Texas</a>
            <ul class="nav" role="menu">
                <li><a href="#dynamicreporting">Dynamic Reporting</a></li>
                <li><a href="#ninjadojo">Ninja Dojo</a></li>
            </ul>
        </li>
        <li>
            <a href="#pics">PICS</a>
        </li>
    </ul>
</nav> --%>