<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="logic" class="guide">
    <div class="page-header">
        <h1>Logic in HTML</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        Adding logic to templates should be limited. It is better if you continue to add the business logic in the backend. When it is neccessary to put logic within the templates, it should <strong>NOT</strong> be mixed with the HTML. <em>Keeping your logic separate from the HTML being rendered helps alleviate the readability issues of XML.</em> Templates become overwhelmingly difficult to maintain if you mix HTML with more XML (Struts tags).
    </p>
    
    <div class="example">
    
        <p>
            <strong>Good example 1</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- Your logic (no html) --&gt;
&lt;s:if test="hit.weapon == 'icepick'"&gt;
    &lt;s:set var="hit_image"&gt;/img/icepick.png&lt;/s:set&gt;
&lt;/s:if&gt;
&lt;s:elseif test="hit.weapon == 'crowbar'"&gt;
    &lt;s:set var="hit_image"&gt;/img/crowbar.png&lt;/s:set&gt;
&lt;s:else&gt;
    &lt;s:set var="hit_image"&gt;/img/banana.png&lt;/s:set&gt;
&lt;/s:else&gt;

&lt;!-- Your content (html) --&gt;
&lt;img src="\${hit_image}" /&gt;
</pre>

        <p>
            <strong>Good example 2</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;ul&gt;
    &lt;s:iterator value="hits" var="hit"&gt;
        &lt;!-- Your logic (no html) --&gt;
        &lt;s:if test="hit.alive == true"&gt;
            &lt;s:set var="hit_image"&gt;/img/alive.png&lt;/s:set&gt;
        &lt;/s:if&gt;
        &lt;s:else&gt;
            &lt;s:set var="hit_image"&gt;/img/dead.png&lt;/s:set&gt;
        &lt;/s:else&gt;
        
        &lt;s:url action="Hit" method="profile" var="hit_url"&gt;
            &lt;s:param name="id"&gt;\${hit.id}&lt;/s:param&gt;
        &lt;/s:url&gt;
        
        &lt;!-- Your content (html) --&gt;
        &lt;a href="\${hit_url}"&gt;\${hit.name} is &lt;img src="\${hit_image}" /&gt;&lt;/a&gt;
    &lt;/s:iterator&gt;
&lt;/ul&gt;
</pre>

        <p>
            <strong>Bad example</strong>
        </p>

<pre class="prettyprint linenums">
&lt;s:url action="Hit" method="profile" var="hit_url"&gt;
    &lt;s:param name="id"&gt;\${hit.id}&lt;/s:param&gt;
&lt;/s:url&gt;

&lt;!-- Your content --&gt;
&lt;a href="\${hit_url}"&gt;
    &lt;!-- Your logic --&gt;
    &lt;s:if test="hit.alive == true"&gt;
        &lt;s:set var="hit_image"&gt;/img/alive.png&lt;/s:set&gt;
    &lt;/s:if&gt;
    &lt;s:else&gt;
        &lt;s:set var="hit_image"&gt;/img/dead.png&lt;/s:set&gt;
    &lt;/s:else&gt;
    
    \${hit.name} is &lt;img src="\${hit_image}" /&gt;
&lt;/a&gt;
</pre>

    </div>
    
    <p>
        <strong><em>The only exception to this rule is when sectioning out alternative views of the same page.</em></strong>
    </p>
    
    <div class="example">
    
<pre class="prettyprint linenums">
&lt;s:if test="hits.length &gt; 0"&gt;
    &lt;ul&gt;
        &lt;s:iterator value="hits" var="hit"&gt;
            &lt;li&gt;\${hit.name}&lt;/li&gt;
        &lt;/s:iterator&gt;
    &lt;/ul&gt;
&lt;/s:if&gt;
&lt;s:else&gt;
    &lt;p&gt;No Hits Found&lt;/p&gt;
&lt;/s:else&gt;
</pre>
    
    </div>
</section>