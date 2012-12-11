<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="overview" class="guide">
    <div class="page-header">
        <h1>Overview</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        Creating a template or HTML file for a page is easy. It is important, however, to understand how the page you are create effects the system. When creating a template one should properly evaluate the files content and location. In addition, it is important to understand the difference between a <code>View Template</code> and a <code>Partial Template</code>.
    </p>
    
    <p>
        <strong>When creating a file ask yourself four questions:</strong>
    </p>
    
    <ol>
        <li><a href="#">Is the file I am creating in the correct location?</a></li>
        <li><a href="#">Is the file and or folder name, I am creating, following convention?</a></li>
        <li>Do I want a <code>View Template</code> or a <code>Partial Template</code>?</li>
        <li>Have I included the appropriate content type / taglibs?</li>
    </ol>
    
    <p>
        <em>If you are unsure of the content type and taglibs to should be included in your HTML file, include the code below:</em>
    </p>
    
    <div class="example">
    
        <p>
            <strong>With comments</strong>
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
            <strong>Without comments</strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %&gt;
&lt;%@ taglib prefix="s" uri="/struts-tags" %&gt;

&lt;title&gt;{{YOUR_PAGE_TITLE}}&lt;/title&gt;
</pre>

    </div>
</section>