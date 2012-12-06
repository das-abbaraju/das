<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Frontend Development Guide</s:param>
</s:include>

<s:include value="/struts/frontend-development-guide/_menu.jsp" />

<section id="overview" class="guide">
    <div class="page-header">
        <h1>Overview</h1>
    </div>
    
    <p>
        Creating a template or HTML file for a page is easy. It is important, however, to understand how the page you are create effects the system. When creating a template one should properly evaluate the files content and location. In addition, it is important to understand the difference between a <code>View Template</code> and a <code>Partial Template</code>.
    </p>
    
    <p>
        <strong>When creating a file ask yourself four questions:</strong>
    </p>
    
    <ol>
        <li><a href="#">Is the file I am creating in the correct location?</a></li>
        <li><a href="#">Is the file and or folder name I am creating following convention?</a></li>
        <li>Do I want a <code>View Template</code> or a <code>Partial Template</code>?</li>
        <li>Have I included the appropriate content type / taglibs?</li>
    </ol>
    
    <p>
        <em>If you are unsure of the content type and taglibs to should be included in your HTML file, include the code below:</em>
    </p>
    
    <div class="example">
    
        <p>
            <strong>With comments:</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- Set the content type to UTF-8 to enable international charset --&gt;
&lt;%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %&gt;

&lt;!-- Include struts tags --&gt;
&lt;%@ taglib prefix="s" uri="/struts-tags" %&gt;

&lt;!-- Add appropriate page title --&gt;
&lt;title&gt;{{YOUR_PAGE_TITLE}}&lt;/title&gt;
</pre>

        <p>
            <strong>Without comments:</strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %&gt;
&lt;%@ taglib prefix="s" uri="/struts-tags" %&gt;

&lt;title&gt;{{YOUR_PAGE_TITLE}}&lt;/title&gt;
</pre>

    </div>
</section>

<section id="templates" class="guide">
    <div class="page-header">
        <h1>Creating a Template</h1>
    </div>
    
    <p>
        Template files are the views that are seen on every page. Each template file makes up the visual mark up that you want to display on a given page. The goal for creating maintainable templates is to split up the content being rendered into display logic that is brief, but comprehensive. We can achieve this goal by defining two template types where each template type will allow us to define manageable and reusable, semantic mark up.  
    </p>
    
    <p>
        There are two types of template files:
    </p>
    
    <ul>
        <li>Views</li>
        <li>Partials</li>
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
&lt;!-- Set the content type to UTF-8 to enable international charset --&gt;
&lt;%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %&gt;

&lt;!-- Include struts tags --&gt;
&lt;%@ taglib prefix="s" uri="/struts-tags" %&gt;

&lt;!-- Browser tab will display "PICS - My Example Page" --&gt;
&lt;title&gt;My Hit List&lt;/title&gt;

&lt;!-- Add "My Hit List" page header --&gt;
&lt;s:include value="/struts/layout/_page-header.jsp"&gt;
    &lt;s:param name="title"&gt;My Hit List&lt;/s:param&gt;
&lt;/s:include&gt;

&lt;ul&gt;
    &lt;s:iterator value="hitList" var="hit"&gt;
        &lt;li&gt;
            &lt;s:include value="/struts/hit/_hit.jsp" /&gt;
                &lt;s:param name="honorific"&gt;\${hit.honorific}&lt;/s:param&gt;
                &lt;s:param name="name"&gt;\${hit.name}&lt;/s:param&gt;
                &lt;s:param name="isAlive"&gt;\${hit.isAlive}&lt;/s:param&gt;
            &lt;/s:include&gt;
        &lt;/li&gt;
    &lt;/s:iterator&gt;
&lt;/ul&gt;
</pre>

    </div>
    
    <hr />
    
    <h2>Partials</h2>
    
    <p>
        Partial Templates are and should be used to define the page's <strong>sub-content</strong>.  Any information that will make up a portion of a <code>View Template</code>, used for ajax or is a shared among multiple <code>View Templates</code> should be a partial. 
    </p>
    
    <div class="example">
    
        <p>
            <strong>/WebContent/struts/hit/_hit.jsp</strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;!-- Set the content type to UTF-8 to enable international charset --&gt;
&lt;%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %&gt;

&lt;!-- Include struts tags --&gt;
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

&lt;% if (request.getParameter("isAlive") != null) { %&gt;
    &lt;s:set name="isAlive"&gt;\${param.isAlive}&lt;/s:set&gt;
&lt;% } else { %&gt;
    &lt;s:set name="isAlive"&gt;true&lt;/s:set&gt;
&lt;% } %&gt;

&lt;div class="hit"&gt;
    &lt;span class="honorific"&gt;\${honorific}&lt;/span&gt;
    &lt;span class="name"&gt;\${name}&lt;/span&gt;
    &lt;span class="is-alive"&gt;
        &lt;s:if test="isAlive == 'true'"&gt;
            &lt;i class="icon-ok"&gt;&lt;/i&gt;
        &lt;/s:if&gt;
    &lt;/span&gt;
&lt;/div&gt;
</pre>

    </div>
</section>

<section id="" class="guide">
    <div class="page-header">
        <h1>Creating Variables in HTML</h1>
    </div>
    
    <div class="example">
    
<pre class="prettyprint linenums">

</pre>
    
    </div>
</section>

Creating a JSP

Creating variables in JSP

Logic in JSP

Iterating over objects in JSP