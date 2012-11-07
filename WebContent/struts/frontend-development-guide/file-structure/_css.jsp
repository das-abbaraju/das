<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="file_structure_css" class="guide">
    <div class="page-header">
        <h1>CSS</h1>
    </div>
    
    <p>
        CSS is used to style the markup or HTML that is generated by View Templates and Partial Templates.  <strong>It is important to note that we do not write CSS in our application, instead we write SASS (Using Compass).</strong>
    </p>
    
    <div class="example">
<pre class="prettyprint linenums lang-sh">
# font-awesome and textual based icons
/css/font

# files that are created by 3rd party companies, such as twitter bootstrap and font awesome.
/css/vendor/*

# a compiled version of the applications custom styles
/css/pics.css

# a compile version of the applications styles
/css/style.css
</pre>
    </div>
</section>