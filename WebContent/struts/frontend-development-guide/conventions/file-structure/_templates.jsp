<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="templates" class="guide">
    <div class="page-header">
        <h1>Templates</h1>
    </div>
    
    <p>
        These naming conventions should apply to all HTML, CSS and Javascript files.  These files come under the <code>struts</code>, <code>css</code> and <code>js</code> directories respectively.
    </p>
    
    <h4>Requirements:</h4>
    <ul>
        <li>Organized under correct folder</li>
        <li>Lower case</li>
        <li>Words separated by "-" (hyphen)</li>
        <li>English (remove vagueness)</li>
        <li>English sounding</li>
    </ul>
    
    <h2>Views</h2>
    
    <div class="example">

        <p>
            <strong>Good Examples:</strong>
        </p>
        
<pre class="prettyprint linenums lang-sh">
# lower case
/struts/frontend-development-guide/file-structure.jsp

# separated by "-", english sounding
/struts/report/manage-report/favorites-list.jsp
/struts/report/manage-report/my-reports-list.jsp
</pre>

        <p>
            <strong>Bad Examples:</strong>
        </p>
        
<pre class="prettyprint linenums lang-sh">
# not separated by "-"
/struts/confirmaudit.jsp

# not lower case, not separated by "-"
/struts/jQuerySample.jsp

# not properly organized, not separated by "-"
/struts/login_ajax.jsp

# not properly organized, not separated by "-", vague, not english sounding
/struts/misc/ambest_suggest.jsp
/struts/misc/conop_flag_diff.jsp
</pre>

    </div>
    
    <hr />
    
    <h2>Partials</h2>
    
    <h4>Additional Requirements:</h4>
    <ul>
        <li>Prefixed by "_" (underscore)</li>
    </ul>
    
    <div class="example">
    
        <p>
            <strong>Good Examples:</strong>
        </p>

<pre class="prettyprint linenums lang-sh">
# organized, lowercase, separated by "-", prefixed with "_", english sounding
/struts/frontend-development-guide/_menu.jsp
/struts/layout/_environment.jsp
/struts/layout/menu/_menu.jsp
/struts/layout/_page-header.jsp
</pre>

        <p>
            <strong>Bad Examples:</strong>
        </p>
        
<pre class="prettyprint linenums lang-sh">
# not organized, not prefixed with "_", shouldn't even exist &mdash; what is it for?
/struts/jquery.jsp

# not lower case
/struts/contrators/_contractorFlagAllFlags.jsp

# not prefixed with "_"
/struts/layout/chat.jsp

# not prefixed with "_", not separated with "-", bad practice
/struts/layout/include_javascript.jsp
</pre>

    </div>
    
</section>