<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="environment_toolbar" class="guide">
    <div class="page-header">
        <h1>Environment Toolbar</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        The environment toolbar is docked at the bottom of the screen and is responsive.  It is only available to non-stable environments.  The purpose of it is to indicate context and to provide debug information.
    </p>
    
    <div class="example">
    
        <p>
            <strong>How the environment toolbar is included:</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- /WebContent/decorators/layout.jsp --&gt;
&lt;footer&gt;
    &lt;s:include value="/struts/layout/_environment.jsp" /&gt;
&lt;/footer&gt;
</pre>

        <p>
            <strong>How the menu gets painted:</strong>
        </p>
        
<pre class="prettyprint linenums lang-sh">
/sass/pics/layout/_environment.scss
</pre>
    </div>
    
</section>