<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="page_layout_containers" class="guide">
    <div class="page-header">
        <h1>Containers</h1>
    </div>
    
    <p>
        Containers are a place for you to semantically structure your code consistently and elegantly. <strong>This example is not a substitute for understanding grid systems or scaffolding. It is only meant to be a preview to it.</strong>
    </p>
    
    <div class="alert alert-info">
        Some content
    </div>
    
    <div class="row">
        <div class="span3">
            <div class="alert alert-success">
                Tooty fruity
            </div>
        </div>
        <div class="span6">
            <div class="alert alert-error">
                Some more content
            </div>
        </div>
    </div>
    
<pre class="prettyprint linenums">
&lt;div class="alert alert-info"&gt;
    Some content
&lt;/div&gt;

&lt;div class="row"&gt;
    &lt;div class="span3"&gt;
        &lt;div class="alert alert-success"&gt;
            Tooty fruity
        &lt;/div&gt;
    &lt;/div&gt;
    &lt;div class="span6"&gt;
        &lt;div class="alert alert-error"&gt;
            Some more content
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
</pre>

    <strong>Not overly used <code>&lt;div.row&gt;</code> elements are not required. A <code>&lt;div.row&gt;</code> is only required when creating nested columns.</strong>
</section>