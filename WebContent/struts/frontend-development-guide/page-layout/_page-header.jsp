<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="page_layout_page_header" class="guide">
    <div class="page-header">
        <h1>Page Header</h1>
    </div>
    
    <p>
        This is the standard header that is to be include on every page.  The title is mandatory. The subtitle is optional.
    </p>
    
        <s:include value="/struts/layout/_page-header.jsp">
        <s:param name="title">You are the sunshine of my life</s:param>
        <s:param name="subtitle">You are the apple of my eye</s:param>
    </s:include>
    
<pre class="prettyprint linenums">
&lt;s:include value="/struts/layout/_page-header.jsp"&gt;
    &lt;s:param name="title"&gt;You are the sunshine of my life&lt;/s:param&gt;
    &lt;s:param name="subtitle"&gt;You are the apple of my eye&lt;/s:param&gt;
&lt;/s:include&gt;
</pre>
    
    <s:include value="/struts/layout/_page-header.jsp">
        <s:param name="title">Ma cherie amour, distant as the Milky Way</s:param>
        <s:param name="subtitle">Ma cherie amour, pretty little one that I adore</s:param>
    </s:include>
    
<pre class="prettyprint linenums">
&lt;s:include value="/struts/layout/_page-header.jsp"&gt;
    &lt;s:param name="title"&gt;Ma cherie amour, distant as the Milky Way&lt;/s:param&gt;
    &lt;s:param name="subtitle"&gt;Ma cherie amour, pretty little one that I adore&lt;/s:param&gt;
&lt;/s:include&gt;
</pre>

<s:include value="/struts/layout/_page-header.jsp">
        <s:param name="title">If they say, why, why? Tell 'em that is human nature</s:param>
    </s:include>
    
<pre class="prettyprint linenums">
&lt;s:include value="/struts/layout/_page-header.jsp"&gt;
    &lt;s:param name="title"&gt;If they say, why, why? Tell 'em that is human nature&lt;/s:param&gt;
&lt;/s:include&gt;
</pre>
</section>