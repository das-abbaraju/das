<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="create_variables" class="guide">
    <div class="page-header">
        <h1>Creating variables in HTML</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        The most common use for needing variables in HTML is to make your life easier. Creating variables in the HTML will allow you to manipulate the values on the <code>ValueStack</code> to assist you on the presentation layer. Maybe you need to create CSS classes or display lists dependent on the parameters passed from the <code>Action Class</code>. However, you will want to understand how <code>OGNL</code> works and how, when creating variables, they will be evaluated.
    </p>
    
    <div class="example">
    
        <p>
            <strong>Create an object or list</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- Using OGNL, the variable will be set to the value User getUser() method on the ValueStack --&gt;
&lt;s:set var="{{YOUR_VARIABLE_NAME}}" value="user" /&gt;
</pre>

        <p>
            <strong>Create an list</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- Using OGNL, the variable will be set to the value List&lt;User&gt; getUserList() method on the ValueStack --&gt;
&lt;s:set var="{{YOUR_VARIABLE_NAME}}" value="userList" /&gt;
</pre>

        <p>
            <strong>Create a string from an object</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- Using OGNL, the variable will be set to the value User getUser().getName() method on the ValueStack --&gt;
&lt;s:set var="{{YOUR_VARIABLE_NAME}}" value="user.name" /&gt;
</pre>

        <p>
            <strong>Create a string literal</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- The variable will be evaluated and set to the String "{{YOUR_STRING}}" --&gt;
&lt;s:set var="{{YOUR_VARIABLE_NAME}}"&gt;{{YOUR_STRING}}&lt;/s:set&gt;
</pre>

        <p>
            <strong>Create an empty string</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- Your variable will be set to an empty string --&gt;
&lt;s:set var="{{YOUR_VARIABLE_NAME}}" value="%{''}" /&gt;
</pre>
    
    PASSING IN OBJECTS INTO PARTIALS<br />
    
    PASSING IN STRINGS INTO PARTIALS<br />
    
    USING SPARAMS FOR SURLS, ETC
    </div>
</section>