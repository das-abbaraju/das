<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">PICS Style Guide</s:param>
</s:include>

<s:include value="/struts/style-guide/_style-guide-menu.jsp" />

<div class="row">
    <div class="span4">
        <div class="well" style="padding: 8px 0;">
            <ul class="nav nav-list">
                <li>
                    <a href="#">Style Guide <i class="icon-chevron-right"></i></a>
                </li>
                <li class="nav-header">
                    Folder Structure
                </li>
                <li>
                    <a href="#">Alerts <i class="icon-chevron-right"></i></a>
                </li>
                <li>
                    <a href="#">Buttons <i class="icon-chevron-right"></i></a>
                </li>
                <li>
                    <a href="#">Forms <i class="icon-chevron-right"></i></a>
                </li>
                <li>
                    <a href="#">Pills <i class="icon-chevron-right"></i></a>
                </li>
            </ul>
        </div>
    </div>
    <div class="span8">
        <h2>CSS Naming Conventions</h2>
        
        <div class="row">
            <div class="span4">
                <h3>ID Naming Conventions</h3>
                <ul>
                    <li>Lowercase</li>
                    <li>Words are separated by underscores</li>
                    <li>a-z0-9_</li>
                </ul>
            </div>
            <div class="span4">
                <pre class="prettyprint linenums">
                    
                </pre>
            </div>
        </div>
        
        <div class="row">
            <div class="span4">
                <h3>Class Naming Conventions</h3>
                <ul>
                    <li>Lowercase</li>
                    <li>Words are separated by underscores</li>
                    <li>a-z0-9_</li>
                </ul>
            </div>
            <div class="span4">
            </div>
        </div>
    </div>
</div>