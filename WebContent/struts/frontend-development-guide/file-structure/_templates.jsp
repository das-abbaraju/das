<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="templates" class="guide">
    <div class="page-header">
        <h1>Templates</h1>
    </div>
    
    <p>
        Template files are the views that are seen on every page.  Each template(s) file makes up the visual markup that you want to display for a given page. Additionally, all Template files should be under the struts directory found under "WebContent".  <strong>Note: each page may be composed of multiple templates.</strong>
    </p>
    
    <p>
        There are two types of Template files:
    </p>
    
    <ul>
        <li>Views</li>
        <li>Partials</li>
    </ul>
    
    <h2>Views</h2>
    
    <p>
        View Templates are and should be used to define the page's content.  It is the View Templates responsibility to organize the layout of the content within itself.
    </p>
    
    <div class="example">
    
<pre class="prettyprint linenums lang-sh">
/struts/contractor/registration/registration.jsp
/struts/contractor/trade/trade-list.jsp
/struts/frontend-development-guide/style-guide/style-guide.jsp
</pre>

    </div>
    
    <hr />
    
    <h2>Partials</h2>
    
    <p>
        Partial Templates are and should be used to define the page's <strong>sub-content</strong>.  Any information that will make up a portion of a View Template, used for ajax or is a shared among multiple View Templates should be a partial. 
    </p>
    
    <div class="example">
    
<pre class="prettyprint linenums lang-sh">
# menu is used on every page (primary navigation)
/struts/layout/_menu.jsp

# page header is used on almost every page to include a consistent title and subtitle
/struts/layout/_page-header.jsp

/struts/frontend-development-guide/style-guide/_colors.jsp
/struts/frontend-development-guide/style-guide/_typography.jsp
</pre>

    </div>
    
</section>