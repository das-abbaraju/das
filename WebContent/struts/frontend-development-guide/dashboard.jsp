<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">PICS Style Guide</s:param>
</s:include>

<s:include value="/struts/frontend-development-guide/_menu.jsp" />

<div class="row">
    <div class="span4">
        
    </div>
    <div class="span8">
        <s:include value="/struts/frontend-development-guide/style-guide/_typography.jsp" />
        
        <s:include value="/struts/frontend-development-guide/style-guide/_colors.jsp" />
        
        <div class="page-header">
            <h2>CSS Naming Conventions</h2>
        </div>
        
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
id
my_id
your_long_id
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
        
        <div class="page-header">
            <h2>Page Header</h2>
        </div>
        
        <s:include value="/struts/layout/_page-header.jsp">
            <s:param name="title">You are the sunshine of my life</s:param>
            <s:param name="subtitle">You are the apple of my eye</s:param>
        </s:include>
    </div>
</div>