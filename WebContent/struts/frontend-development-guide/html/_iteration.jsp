<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="iteration" class="guide">
    <div class="page-header">
        <h1>Iterating over variables in HTML</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        Iterating over lists and maps are commonplace. Ensure you do not confuse scope by <em>SPECIFICALLY</em> specifying a new context. The new context would be accessed directly so that there is no confusion to which variable / object you are referencing. You can do this by declaring the <code>var</code> keyword in an <code>&lt;s:iterator&gt;</code> tag.
    </p>
    
    <div class="example">
    
        <p>
            <strong>Basic example 1</strong>
        </p>
    
<pre class="prettyprint linenums">
&lt;!-- Using OGNL, hitList will be set to the getHitList() method on the ValueStack --&gt;
&lt;!-- The result of that method will be assigned to the hit variable --&gt;
&lt;s:iterator value="hitList" var="hit"&gt;
    &lt;!-- Using EL Expression Language, access getName() method on the variable #hit --&gt;
    \${hit.name}
&lt;/s:iterator&gt;
</pre>
        <p>
            <strong>Basic example 2</strong>
        </p>

<pre class="prettyprint linenums">
&lt;!-- Using OGNL, hitList will be set to the getHitList() method on the ValueStack --&gt;
&lt;!-- The result of that method will be assigned to the hits variable --&gt;
&lt;!-- In addition, a hitStatus variable is to access IteratorStatus values (index|count|first|even|last|odd) --&gt;
&lt;s:set var="hits" value="hitList" /&gt;

&lt;s:iterator value="#hits" var="hit" status="hitStatus"&gt;
    &lt;!-- Using EL Expression Language, access getIndex() method on the variable #hitStatus --&gt;
    \${hitStatus.index}
&lt;/s:iterator&gt;
</pre>

        <p>
            <strong>Advanced example</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;ul id="\${list_id}" class="\${list_class} unstyled"&gt;
    &lt;s:iterator value="#reports" var="user_report"&gt;
        &lt;s:set name="report" value="#user_report.report" /&gt;
        &lt;s:set name="report_id" value="#report.id" /&gt;

        &lt;!-- Url --&gt;
        &lt;s:url action="ManageReports" method="unfavorite" var="report_favorite_url"&gt;
            &lt;s:param name="reportId"&gt;\${report_id}&lt;/s:param&gt;
        &lt;/s:url&gt;

        &lt;s:url action="Report" var="report_url"&gt;
            &lt;s:param name="report"&gt;\${report_id}&lt;/s:param&gt;
        &lt;/s:url&gt;

        &lt;!-- Icon --&gt;
        &lt;s:set name="is_favorite_class" value="%{''}" /&gt;

        &lt;s:if test="favorite"&gt;
            &lt;s:set name="is_favorite_class"&gt;selected&lt;/s:set&gt;
        &lt;/s:if&gt;

        &lt;li class="report clearfix"&gt;
            &lt;a href="\${report_favorite_url}" class="favorite" data-id="\${report_id}"&gt;
                &lt;i class="icon-star icon-large \${is_favorite_class}"&gt;&lt;/i&gt;
            &lt;/a&gt;
        &lt;/li&gt;
    &lt;/s:iterator&gt;
&lt;/ul&gt;
</pre>


    </div>
</section>