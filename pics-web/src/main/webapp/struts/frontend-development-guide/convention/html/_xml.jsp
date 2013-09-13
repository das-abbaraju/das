<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="xml" class="guide">
    <div class="page-header">
        <h1>Struts 2.x</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <p>
        HTML is hard enough to read. Don't make it any harder than it already is!
    </p>
    
    <div class="example">
    
        <p>
            <strong>Bad Example:</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- 300 characters too long --&gt;
&lt;a href="http://www.mapquest.com/maps/map.adp?country=&lt;s:property value="contractor.country.isoCode" /&gt;&city=&lt;s:property value="contractor.city" /&gt;&state=&lt;s:property value="contractor.state" /&gt;&address=&lt;s:property value="contractor.address" /&gt;&zip=&lt;s:property value="contractor.zip" /&gt;&zoom=5" target="_blank"&gt;&lt;s:text name="ContractorView.ShowMap" /&gt;&lt;/a&gt;
</pre>

        <p>
            <strong>Good example:</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- Clear, Maintainable, Elegant --&gt;
&lt;s:url value="http://www.mapquest.com/maps/map.adp" var="mapquest_url"&gt;
    &lt;s:param name="country"&gt;\${contractor.country.isoCode}&lt;/s:param&gt;
    &lt;s:param name="city"&gt;\${contractor.city}&lt;/s:param&gt;
    &lt;s:param name="state"&gt;\${contractor.state}&lt;/s:param&gt;
    &lt;s:param name="address"&gt;\${contractor.address}&lt;/s:param&gt;
    &lt;s:param name="zip"&gt;\${contractor.zip}&lt;/s:param&gt;
    &lt;s:param name="zoom"&gt;5&lt;/s:param&gt;
&lt;/s:url&gt;

&lt;a href="\${mapquest_url}" target="_blank"&gt;\${ContractorView.ShowMap}&lt;/a&gt;
</pre>

    </div>
    
    <div class="example">
    
        <p>
            <strong>Bad Example:</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- No --&gt;
&lt;a href="OperatorConfiguration.action?id=&lt;s:property value="get('opID')"/&gt;" class="account&lt;s:property value="get('operatorStatus')"/&gt;"&gt;&lt;s:property value="get('operator')"/&gt;&lt;/a&gt;
</pre>

        <p>
            <strong>Good example:</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- Clear, Maintainable, Elegant --&gt;
&lt;s:url action="OperatorConfiguration" var="operator_url"&gt;
    &lt;s:param name="id" value="get('opID')" /&gt;
&lt;/s:url&gt;

&lt;s:if test="get('operatorStatus')"&gt;
    &lt;s:set var="account_class"&gt;account-status&lt;/s:set&gt;
&lt;/s:if&gt;
&lt;s:else&gt;
    &lt;s:set var="account_class" value="''" /&gt;
&lt;/s:else&gt;

&lt;a href="\${operator_url}" class="\${account_class}"&gt;&lt;s:property value="get('operator')"/&gt;&lt;/a&gt;
</pre>

    </div>
    
    <div class="example">
    
        <p>
            <strong>Bad Example:</strong>
        </p>
        
<pre class="prettyprint linenums">
&lt;!-- I am just going to walk away from you --&gt;
&lt;s:if test="filter.showType"&gt;
    &lt;div class="filterOption"&gt;&lt;a href="#"
        onclick="toggleBox('form1_type'); return false;"&gt;&lt;s:text name="global.Type" /&gt;&lt;/a&gt; = &lt;span
        id="form1_type_query"&gt;&lt;s:text name="JS.Filters.status.All" /&gt;&lt;/span&gt;&lt;br /&gt;&lt;span id="form1_type_select"
        style="display: none" class="clearLink"&gt;&lt;s:select
        list="filter.typeList" multiple="true" cssClass="forms"
        name="filter.type" id="form1_type" /&gt;&lt;br /&gt;
    &lt;a class="clearLink" href="#"
        onclick="clearSelected('form1_type'); return false;"&gt;&lt;s:text name="Filters.status.Clear" /&gt;&lt;/a&gt; &lt;/span&gt;&lt;/div&gt;
&lt;/s:if&gt;
</pre>

    </div>
</section>