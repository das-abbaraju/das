<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="files" class="guide">
    <div class="page-header">
        <h1>Files</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        There are some key files that control the application's configuration. It is crucial that you understand what they are for and how they effect your development.
    </p>
    
    <div class="example">

<pre class="prettyprint linenums lang-sh">
# application descriptor/core
web.xml

# application configurations
struts-default.xml

# plugins
struts-plugins.xml

# application configuration overrides
struts.properties

# decorator intercepter
sitemesh.xml

# database connection
server.xml

# routing file
struts.xml

# decorator file
decorators.xml
</pre>
        
    </div>
</section>