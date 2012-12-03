<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="html" class="guide">
    <div class="page-header">
        <h1>HTML</h1>
    </div>
    
    <p>
        HTML is XML. Organization and maintenance of HTML is just as important as any other code.  HTML is the language we use to tell the computer what type of content we are trying to convey and we do so by writing semantic markup. Semantic markup allows us to describe our content, to the computer, through a list of comprehensive tags.
    </p>
    
    <p>
        Writing clean and elegant HTML is not as easy as it seems, but is not an impossible task either. The goal is to approach HTML in two easy steps:
    </p>
    
    <ol>
        <li>Use semantic tags to describe the content you want to display.</li>
        <li>Modify or wrap the content with additional containers to be appropriate for the design.</li>
    </ol>
</section>

<section id="xml" class="guide">
    <div class="page-header">
        <h1>No XML in HTML</h1>
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

<section id="text" class="guide">
    <div class="page-header">
        <h1>Displaying text</h1>
    </div>
</section>

<section id="partials" class="guide">
    <div class="page-header">
        <h1>Partials</h1>
    </div>
    
    <p>
        Partial Templates are and should be used to define the page's sub-content. Any information that will make up a portion of a View Template, used for ajax or is a shared among multiple View Templates should be a partial.
    </p>
    
    <div class="example">
    
<pre class="prettyprint linenums">
&lt;!-- Defined in layout.jsp, struts action tag to inject the result of an action into a template --&gt;
&lt;s:action name="Menu!menu" executeResult="true" /&gt;

&lt;!-- the result returns a menu partial that is not to be called directly --&gt;
&lt;result&gt;/struts/layout/menu/_menu.jsp&lt;/result&gt;

&lt;!-- the menu partial iterates over items and passes its children to create drop down menus --&gt;
&lt;s:set var="menu_items" value="menu.children.subList(0, #last_menu_index)" /&gt;
&lt;s:include value="/struts/layout/menu/_menu-item.jsp" /&gt;
</pre>
    
    </div>
    
    <div class="example">

<pre class="prettyprint linenums">
&lt;!-- Insert a commonly used partial that is not to be called directly, prevents duplicate code --&gt;
&lt;s:include value="/struts/layout/_page-header.jsp"&gt;
    &lt;s:param name="title"&gt;Ma cherie amour, distant as the Milky Way&lt;/s:param&gt;
    &lt;s:param name="subtitle"&gt;Ma cherie amour, pretty little one that I adore&lt;/s:param&gt;
&lt;/s:include&gt;
</pre>
    
    </div>
</section>

<section id="iteration" class="guide">
    <div class="page-header">
        <h1>Iteration</h1>
    </div>
    
    <p>
        Iterating over lists and maps are commonplace. Ensure you do not confuse scope by <em>SPECIFICALLY</em> specifying a new context. The new context would be accessed directly so that there is no confusion to which variable / object you are referencing. You can do this by declaring the <code>var</code> keyword in an <code>&lt;s:iterator&gt;</code> tag.
    </p>
    
    <div class="example">
    
<pre class="prettyprint linenums">
&lt;ul id="\${list_id}" class="\${list_class} unstyled"&gt;
    &lt;s:iterator value="#reports" var="user_report" status="rowstatus"&gt;
        &lt;s:set name="report" value="#user_report.report" /&gt;
        &lt;s:set name="report_id" value="#report.id" /&gt;

        &lt;%-- Url --%&gt;
        &lt;s:url action="ManageReports" method="unfavorite" var="report_favorite_url"&gt;
            &lt;s:param name="reportId"&gt;\${report_id}&lt;/s:param&gt;
        &lt;/s:url&gt;

        &lt;s:url action="Report" var="report_url"&gt;
            &lt;s:param name="report"&gt;\${report_id}&lt;/s:param&gt;
        &lt;/s:url&gt;

        &lt;%-- Icon --%&gt;
        &lt;s:set name="is_favorite_class" value="''" /&gt;

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

<section id="logic" class="guide">
    <div class="page-header">
        <h1>Logic</h1>
    </div>
</section>