<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="navigation" class="guide">
    <div class="page-header">
        <h1>Primary Navigation</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        The navigation component is bootstrap built and responsive.
    </p>
    
    <div class="example">
    
        <p>
            <strong>How the menu is included:</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- /WebContent/decorators/layout.jsp --&gt;
&lt;header&gt;
    &lt;s:action name="Menu!menu" executeResult="true" /&gt;
&lt;/header&gt;
</pre>

        <p>
            <strong>How the menu is generated:</strong>
        </p>

<pre class="prettyprint linenums lang-sh">
# primary navigation element
/struts/layout/menu/_menu.jsp

# drop down &mdash; menus &amp; sub-menus
/struts/layout/menu/_menu-item.jsp
</pre>

        <p>
            <strong>How the menu becomes interactive:</strong>
        </p>

<pre class="prettyprint linenums lang-sh">
/js/pics/layout/menu/menu.js
</pre>

        <p>
            <strong>How the menu gets painted:</strong>
        </p>
        
<pre class="prettyprint linenums lang-sh">
/sass/pics/layout/_menu.scss
</pre>
    </div>
    
</section>