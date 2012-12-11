<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="folders" class="guide">
    <div class="page-header">
        <h1>Folders</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        It is very important that a user gains context by folder structure.  Structuring and organizing files is just as important as structuring and organizing code.  Nest folders when appropriate and make sure you are not forking the project internally by creating multiple directories to achieve similar purpose <strong>(e.g. i18n and translation)</strong>.
    </p>
    
    <h4>Requirements:</h4>
    <ul>
        <li>Lower case</li>
        <li>Words separated by "-" (hyphen)</li>
        <li>Singular</li>
        <li>English (removing vagueness)</li>
        <li>English sounding</li>
    </ul>
    
    <div class="example">
        <p>
            <strong>Good Examples:</strong>
        </p>
        
<pre class="prettyprint linenums lang-sh">
/struts/app-translation
/struts/contractor
/struts/contractor/trade
/struts/contractor/registration
/struts/frontend-development-guide
/struts/layout
</pre>

        <p>
            <strong>Bad Examples:</strong>
        </p>
        
<pre class="prettyprint linenums lang-sh">
# not separated by "-"
/struts/app_property

# not lower case, not separated by "-"
/struts/CustomerService

# vague
/struts/gc
/struts/misc

# plural
/struts/reports
</pre>

    </div>
</section>