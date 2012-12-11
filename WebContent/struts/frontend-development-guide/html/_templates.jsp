<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="templates" class="guide">
    <div class="page-header">
        <h1>Creating a Template</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        Template files are the views that are seen on every page. Each template file makes up the visual mark up that you want to display on a given page. The goal for creating maintainable templates is to split up the content being rendered into display logic that is brief, but comprehensive. We can achieve this goal by defining two template types where each template type will allow us to define manageable and reusable, semantic mark up.  
    </p>
    
    <p>
        There are two types of template files:
    </p>
    
    <ul>
        <li><code>View Templates</code></li>
        <li><code>Partial Templates</code></li>
    </ul>
    
    <p>
        <em>It is important to know that a template represents a specific page's content and nothing more. The menu, header, and footer, for example, should <strong>NOT</strong> be included in a template file. Items such as those should be included in a <code>Decorator</code>.</em>
    </p>
    
    <h2>Views</h2>
    
    <p>
        <code>View Templates</code> are and should be used to define the page's content.  It is the <code>View Template's</code> responsibility to organize the layout of the content within itself. Note: each <code>View Template</code> may be composed of multiple <code>Partial Templates</code>.
    </p>
    
    <div class="example">
    
        <p>
            <strong>/WebContent/struts/hit/hit-list.jsp</strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %&gt;
&lt;%@ taglib prefix="s" uri="/struts-tags" %&gt;

&lt;!-- Browser tab will display "PICS - My Example Page" --&gt;
&lt;title&gt;My Hit List&lt;/title&gt;

&lt;!-- Add "My Hit List" page header --&gt;
&lt;s:include value="/struts/layout/_page-header.jsp"&gt;
    &lt;s:param name="title"&gt;My Hit List&lt;/s:param&gt;
&lt;/s:include&gt;

&lt;ul&gt;
    &lt;!-- Display a list of hits --&gt;
    &lt;s:iterator value="hitList" var="hit"&gt;
        &lt;li&gt;
            &lt;s:include value="/struts/hit/_hit.jsp" /&gt;
                &lt;s:param name="honorific"&gt;\${hit.honorific}&lt;/s:param&gt;
                &lt;s:param name="name"&gt;\${hit.name}&lt;/s:param&gt;
                &lt;s:param name="is_alive"&gt;\${hit.alive}&lt;/s:param&gt;
            &lt;/s:include&gt;
        &lt;/li&gt;
    &lt;/s:iterator&gt;
&lt;/ul&gt;
</pre>

    </div>
    
    <hr />
    
    <h2>Partials</h2>
    
    <p>
        <code>Partial Templates</code> are and should be used to define the page's <strong>sub-content</strong>.  Any information that will make up a portion of a <code>View Template</code>, used for ajax or is a shared among multiple <code>View Templates</code> should be a partial. 
    </p>
    
    <div class="example">
    
        <p>
            <strong>/WebContent/struts/hit/_hit.jsp</strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %&gt;
&lt;%@ taglib prefix="s" uri="/struts-tags" %&gt;

&lt;% if (request.getParameter("honorific") != null) { %&gt;
    &lt;s:set name="honorific"&gt;\${param.honorific}&lt;/s:set&gt;
&lt;% } else { %&gt;
    &lt;s:set name="honorific"&gt;Captain&lt;/s:set&gt;
&lt;% } %&gt;

&lt;% if (request.getParameter("name") != null) { %&gt;
    &lt;s:set name="name"&gt;\${param.name}&lt;/s:set&gt;
&lt;% } else { %&gt;
    &lt;s:set name="name"&gt;Douglas&lt;/s:set&gt;
&lt;% } %&gt;

&lt;% if (request.getParameter("is_alive") != null) { %&gt;
    &lt;s:set name="is_alive"&gt;\${param.is_alive}&lt;/s:set&gt;
&lt;% } else { %&gt;
    &lt;s:set name="is_alive"&gt;true&lt;/s:set&gt;
&lt;% } %&gt;

&lt;div class="hit"&gt;
    &lt;span class="honorific"&gt;\${honorific}&lt;/span&gt;
    &lt;span class="name"&gt;\${name}&lt;/span&gt;
    &lt;span class="is-alive"&gt;
        &lt;s:if test="is_alive == 'true'"&gt;
            &lt;i class="icon-ok"&gt;&lt;/i&gt;
        &lt;/s:if&gt;
    &lt;/span&gt;
&lt;/div&gt;
</pre>

    </div>
</section>