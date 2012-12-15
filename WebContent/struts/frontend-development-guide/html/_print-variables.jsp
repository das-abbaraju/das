<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="print_variables" class="guide">
    <div class="page-header">
        <h1>Printing variables in HTML</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        To dynamic display content onto a page is commonplace. However, it is important to know how that data is being generated. Malformed or unvalidated data can cause much more harm than good. The most common example of unvalidated data is script injection. This security vulnerability is important to be cognizant of. Consequently, it is a good idea to be/get familiar with the <code>JSP 2.0 Expression Language</code>.
    </p>
    
    <div class="example">
    
        <p>
            <strong>Accessing a name variable on the <code>ValueStack</code></strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;!-- JSP 2.0 EL (Expression Language) --&gt;
\${name}

&lt;!-- Struts 2 --&gt;
&lt;s:property value="name" /&gt;
</pre>

        <p>
            <strong>Accessing a name variable on an object on the <code>ValueStack</code></strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;!-- JSP 2.0 EL (Expression Language) --&gt;
\${user.name}

&lt;!-- Struts 2 --&gt;
&lt;s:property value="user.name" /&gt;
</pre>

        <p>
            <strong>Accessing a string literal</strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;!-- JSP 2.0 EL (Expression Language) --&gt;
\${'Did you say just "Douglas"?'}

&lt;!-- Struts 2 --&gt;
&lt;s:property value="%{'Did you just say \"Douglas\"?'}" /&gt;
</pre>

        <p>
            <strong>Accessing a translation</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- JSP 2.0 EL (Expression Language) --&gt;
&lt;s:text name="{{YOUR_TRANSLATION_KEY}}" var="translation" /&gt;
\${translation}

&lt;!-- Struts 2 --&gt;
&lt;s:text name="{{YOUR_TRANSLATION_KEY}}" /&gt;
</pre>

        <p>
            <strong>Accessing a variable created by the <code>&lt;s:set var="name"&gt;Douglas&lt;/s:set&gt;</code></strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- JSP 2.0 EL (Expression Language) --&gt;
\${name}

&lt;!-- Struts 2 --&gt;
&lt;s:property value="#name" /&gt;
</pre>

        <p>
            <strong>Accessing a variable you do NOT want to escape</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- JSP 2.0 EL (Expression Language) --&gt;
\${name}

&lt;!-- Struts 2 --&gt;
&lt;s:property value="name" escape="false" /&gt;
</pre>

        <p>
            <strong>Accessing a variable you want to escape</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- JSP 2.0 EL (Expression Language) --&gt;
\${fn:escapeXml(???)}

&lt;!-- Struts 2 --&gt;
&lt;s:property value="name" /&gt;
&lt;s:property value="name" escape="true" /&gt;
</pre>

    DOES SPROPERTY SEARCH ENTIRE VALUE STACK AND EXPRESSION LANGUAGE DOES NOT?<br />
    
    STEXT AND GETTEXT DIFFERENCE?<br />
        STEXT - ACTIONCONTEXTLOCALE (LOCALE IN SESSION? MAYBE)<br />
        GETTEXT - CAN SPECIFY LOCALE SPROPERTY VALUE="GETTEXT" KEY, LOCALE<br />

    WHAT IS %{

    </div>
</section>