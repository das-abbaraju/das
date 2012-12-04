<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="links" class="guide">
    <div class="page-header">
        <h1>Links</h1>
    </div>
    
    <p>
        It is important to utilize the routing capabilities to generate appropriate URLs. The power of generating URLs comes from a single point of entry for URL management. By allowing the routing system to generate and control our URLs it will be a rare case in which a URL would need to be hard coded. Links can then be changed in a centralized location that is not in the templates. This way the routing file can determine how links should be formatted and displayed.
    </p>
    
    <p>
        If the homepage is <code>https://www.picsorganizer.com/</code> today, but tomorrow it is <code>https://www.picsorganizer.com/dashboard/</code>, the only thing that would have to be changed is the Routing file and all of the links would update appropriately.
    </p>
    
    <div class="example">
    
        <p>
            <strong>Generating a link to a Action</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;s:url action="Home" var="dashboard_url" /&gt;
&lt;a href="\${dashboard_url}"&gt;Go to my Dashboard&lt;/a&gt;
</pre>

        <p>
            <strong>Generating a link to a Action/Method</strong>
        </p>

<pre class="prettyprint linenums lang-sh">
&lt;s:url action="Login" method="logout" var="logout_url" /&gt;
&lt;a href="\${logout_url}"&gt;Logout&lt;/a&gt;
</pre>

        <p>
            <strong>Generating a link to a Action with parameters</strong>
        </p>

<pre class="prettyprint linenums lang-sh">
&lt;s:url action="Report" var="report_url"&gt;
    &lt;s:param name="id"&gt;\${report.id}&lt;/s:param&gt;
&lt;/s:url&gt;

&lt;form action="\${report_url}" method="post"&gt;&lt;/form&gt;
</pre>

        <p>
            <strong>Generating a link to an external source</strong>
        </p>

<pre class="prettyprint linenums lang-sh">
&lt;a href="//www.twitter.com/chemoish"&gt;@chemoish&lt;/a&gt;
</pre>
    
    </div>
</section>