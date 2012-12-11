<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="routing" class="guide">
    <div class="page-header">
        <h1>Routing</h1>
    </div>
    
    <p>
        It is important to note that incoming requests can be manipulated at different levels, from <code>DNS</code> to <code>.htaccess</code>. Struts 2 has a few different configurations on <code>Routing</code>, be sure to investigate them on your own. This guide will explain how a response is returned when a user makes a request to the application. This is a basic guide and is not meant to be comprehensive. 
    </p>
    
    <p>
        <strong>Below is an example of someone attempting to access "https://www.picsorganizer.com/Login.action"</strong>
    </p>
    
    <div class="example">
    
        <p>
            <strong>Step 1: Match and direct an incoming request.</strong>
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
</pre>

        <p>
            <strong>Step 2: Build a response, including headers and content.</strong>
        </p>

<pre class="prettyprint linenums lang-sh">
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
</pre>

        <p>
            <strong>Step 3: Decorate response appropriately.</strong>
        </p>

<pre class="prettyprint linenums lang-sh">
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
</pre>

        <p>
            <strong>Step 4: Enjoy a cup of tea along with your response.</strong>
        </p>
    
    </div>
</section>