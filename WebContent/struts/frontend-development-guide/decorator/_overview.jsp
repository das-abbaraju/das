<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="overview" class="guide">
    <div class="page-header">
        <h1>Overview</h1>
    </div>
    
    <p>
        <code>Decorator templates</code> "decorate" JavaServer Pages or jsp's. <code>Decorators</code> are selected after executing a Controller, but before outputing content to the output buffer. <code>Decorator</code> files include elements that are typically shared between multiple templates.  These shared items include <code>&lt;html&gt;</code>, <code>&lt;body&gt;</code>, <code>&lt;link&gt;</code> and <code>&lt;script&gt;</code> tags. 
    </p>
    
    <p>
        <code>Decorators</code> control the over arching page layout and are typically responsible for including general components such as: header, primary navigation, breadcrumbs, sidebar, footer, etc.  It is also responsible for determining the number of columns in a page (e.g. Is this page a one, two or three column layout).  The control over which <code>decorator file</code> a particular jsp uses is controlled by decorators.xml.
    </p>
    
    <p>
        The two layouts that should be used for all new styles should be:
    </p>
    
    <ul>
        <li>layout.jsp</li>
        <li>simple-layout.jsp</li>
    </ul>
    
    <p>
        <strong>Since these files are used by every template, or jsp, no one is allowed to modify this file without prior authorization.</strong>
    </p>
    
    <div class="example">
<pre class="prettyprint linenums">
&lt;!DOCTYPE html&gt;
&lt;!--[if lt IE 7]&gt;      &lt;html class="no-js lt-ie9 lt-ie8 lt-ie7"&gt; &lt;![endif]--&gt;
&lt;!--[if IE 7]&gt;         &lt;html class="no-js lt-ie9 lt-ie8"&gt; &lt;![endif]--&gt;
&lt;!--[if IE 8]&gt;         &lt;html class="no-js lt-ie9"&gt; &lt;![endif]--&gt;
&lt;!--[if gt IE 8]&gt;&lt;!--&gt; &lt;html class="no-js"&gt; &lt;!--&lt;![endif]--&gt;
    &lt;head&gt;
        &lt;meta charset="utf-8"&gt;
        &lt;meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"&gt;
        &lt;title&gt;PICS - &lt;decorator:title default="PICS" /&gt;&lt;/title&gt;
        ...
    &lt;/head&gt;
    &lt;body&gt;
        &lt;div id="main" role="main" class="container"&gt;
            &lt;decorator:body /&gt;
        &lt;/div&gt;
        ...
    &lt;/body&gt;
    ...
</pre>
    </div>
</section>