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
        Routing is an important subject. It determines how incoming requests are handled and processed. The routing process controls whether or not you get the response you want, but also provides a couple of other benefits:
    </p>
    
    <ol>
        <li>
            Single point of entry for URL management — Removing the need for hard coded URLs
        </li>
        <li>
            Abstracts the defined URL from the path on the file system (E.g. /login goes to Login Java Class and not to a folder called "login")
        </li>
        <li>
            Validation for URLs (E.g. /login?hack=true does not match /login and will result in a 404 page)
        </li>
        <li>
            Validation for URL request parameters (E.g. /contractor/ancon-marine must accept a [A-Za-z\-], /contractor/1 will result in a 404 page)
        </li>
    </ol>
    
    <p>
        This guide will explain how a response is returned when a user makes a request to the application. Struts 2 has a few different configurations on Routing, be sure to investigate them on your own. <strong>This is a basic guide and is not meant to be comprehensive.</strong> 
    </p>
    
</section>

<section id="routing" class="guide">
    <div class="page-header">
        <h1>Routing</h1>
    </div>
    
    <div class="example">
    
        <p>
            <strong>Example of an end to end request:</strong>
        </p>
        
<pre class="prettyprint linenums lang-sh">
# User requests a specified URL from his/her browser
https://www.picsorganizer.com/Login.action

# Catches all requests that end with .action and applies the Struts filter
web.xml

# Forwards to URL management
struts.xml

# Regex pattern matches to appropriate action and associates the request to a Java Class
&lt;action name="Login" class="com.picsauditing.access.LoginController"&gt;&lt;/action&gt;

# Default action handler is execute()
public String execute() throws Exception {}

# Alternate example for https://www.picsorganizer.com/Login!logout.action
public String logout() throws Exception {}

# Obtain a return String from the action method to determine the appropriate response and response headers.
# com.opensymphony.xwork2.Action interface contains a list of provided constants
public String execute() throws Exception {
    return SUCCESS;
}

# Alternate example for https://www.picsorganizer.com/Login!logout.action
public String logout() throws Exception {
    return "logout";
}

# Match the constant back to struts.xml to obtain available template (if there is one)
&lt;action name="Login" class="com.picsauditing.access.LoginController"&gt;&lt;/action&gt;
    &lt;result&gt;/struts/users/login.jsp&lt;/result&gt;
    &lt;result name="logout"&gt;/some-other-file.jsp&lt;/result&gt;
&lt;/action&gt;

# Obtain an appropriate layout file
decorators.xml

# Regex match the return to a layout file
&lt;decorator name="simpleLayout" page="simple-layout.jsp"&gt;
    &lt;pattern&gt;/Login.action&lt;/pattern&gt;
&lt;/decorator&gt;

# Apply the content returned to the layout file using SiteMesh
&lt;!DOCTYPE html&gt;
&lt;html&gt;
    &lt;header&gt;
        &lt;!-- TITLE REPLACEMENT --&gt;
        &lt;decorator:title default="PICS" /&gt;
    &lt;/header&gt;
    &lt;body&gt;
        &lt;!-- CONTENT REPLACEMENT --&gt;
        &lt;decorator:body /&gt;
    &lt;/body&gt;
&lt;/html&gt;

# Enjoy a cup of tea along with your response
</pre>
    
    </div>
</section>

<section id="links" class="guide">
    <div class="page-header">
        <h1>Links</h1>
    </div>
    
    <p>
        It is important to utilize the routing capabilities to generate appropriate URLs. The power of generating URLs comes from a single point of entry for URL management. By allowing the routing system to generate and control our URLs it will be a rare case in which a URL would need to be hard coded. Links can then be changed in a centralized location that is not in the templates. 
    </p>
    
    <div class="example">
    
<pre class="prettyprint linenums">
&lt;!-- Generating a link to a Action --&gt;
&lt;s:url action="Home" var="dashboard_url" /&gt;
&lt;a href="\${dashboard_url}" class="dashboard"&gt;Go to my Dashboard&lt;/a&gt;

&lt;!-- Generating a link to a Action/Method --&gt;
&lt;s:url action="Login" method="logout" var="logout_url" /&gt;
&lt;a href="\${logout_url}" class="logout"&gt;Logout&lt;/a&gt;

&lt;!-- Generating a link to a Action with parameters --&gt;
&lt;s:url action="Report" var="report_url"&gt;
    &lt;s:param name="id"&gt;\${report.id}&lt;/s:param&gt;
&lt;/s:url&gt;

&lt;form action="\${report_url}" method="post"&gt;&lt;/form&gt;

&lt;!-- Generating a link to an external source --&gt;
&lt;a href="//www.twitter.com/chemoish"&gt;Twitter&lt;/a&gt;
</pre>
    
    </div>
</section>