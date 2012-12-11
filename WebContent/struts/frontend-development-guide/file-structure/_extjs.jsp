<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="extjs" class="guide">
    <div class="page-header">
        <h1>ExtJS</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        ExtJS4 is a large <code>MV*</code> Javascript framework that is used for Dynamic Reports.  ExtJS4 works very different from the typical web stack.  It is responsible for page layout, interaction and styles, but also handles client side database storage.
    </p>
    
    <div class="example">
    
<pre class="prettyprint linenums lang-sh">
# extjs application for dynamic reports
js/extjs/pics

# extjs source files + custom compliations
js/extjs/pics/extjs

# application files
js/extjs/pics/app

# application runner
app.js

# application instructions
js/extjs/pics/README

# application deployment
app.jsb3
index.html
prod.html

# application resources
js/extjs/pics/resources

</pre>
    
    </div>
</section>