<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="page_layout_containers" class="guide">
    <div class="page-header">
        <h1>Containers</h1>
    </div>
    
    <p>
        Containers are a place for you to semantically structure your code consistently and elegantly. <strong>This example is not a substitute for understanding grid systems or scaffolding. It is only meant to be a preview to it.</strong>
    </p>
    
    <div class="row show-grid">
        <div class="span1">1</div>
        <div class="span1">1</div>
        <div class="span1">1</div>
        <div class="span1">1</div>
        <div class="span1">1</div>
        <div class="span1">1</div>
        <div class="span1">1</div>
        <div class="span1">1</div>
        <div class="span1">1</div>
    </div>
    
    <div class="row show-grid">
        <div class="span2">2</div>
        <div class="span3">3</div>
        <div class="span4">4</div>
    </div>
    
    <div class="row show-grid">
        <div class="span4">4</div>
        <div class="span5">5</div>
    </div>
    
    <div class="row show-grid">
        <div class="span9">9</div>
    </div>

    <div class="example">
    
        <p>
            <strong>This example was taken from <code>/struts/frontend-development-guide/style-guide/style-guide.jsp</code>:</strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;s:include value="/struts/layout/_page-header.jsp"&gt;
    &lt;s:param name="title"&gt;PICS Style Guide&lt;/s:param&gt;
&lt;/s:include&gt;

&lt;s:include value="/struts/frontend-development-guide/_menu.jsp" /&gt;

&lt;div class="row"&gt;
    &lt;div class="span3"&gt;
        &lt;s:include value="_menu.jsp" /&gt;
    &lt;/div&gt;
    &lt;div class="span9"&gt;
        &lt;s:include value="_overview.jsp" /&gt;
        
        &lt;s:include value="_typography.jsp" /&gt;
        
        &lt;s:include value="_colors.jsp" /&gt;
    &lt;/div&gt;
&lt;/div&gt;
</pre>

    </div>    

    <strong>Not overly used <code>&lt;div.row&gt;</code> elements are not required. A <code>&lt;div.row&gt;</code> is only required when creating nested columns.</strong>
</section>