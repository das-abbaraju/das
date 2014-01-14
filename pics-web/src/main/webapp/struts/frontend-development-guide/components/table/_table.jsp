<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="header_title">Table</s:param>
    <s:param name="section_id">table</s:param>

    <s:param name="description">
Tables are used to display large amounts data that is broken up into columns.
    </s:param>

    <s:param name="example_url">
        table/_table-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">table</s:param>

    <s:param name="html_code">
&lt;table class="table table-striped table-condensed table-hover"&gt;
    &lt;thead&gt;
        &lt;tr&gt;
            &lt;th class="col-md-5"&gt;Some Header&lt;/th&gt;
            &lt;th class="col-md-7"&gt;Some Header&lt;/th&gt;
        &lt;/tr&gt;
    &lt;/thead&gt;
    &lt;tbody&gt;
        &lt;tr&gt;
            &lt;td&gt;Some Data&lt;/td&gt;
            &lt;td&gt;Some Data&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;Some Data&lt;/td&gt;
            &lt;td&gt;Some Data&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;Some Data&lt;/td&gt;
            &lt;td&gt;Some Data&lt;/td&gt;
        &lt;/tr&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
    </s:param>

<%--     <s:param name="struts_code">
&lt;table class="table table-striped table-condensed table-hover"&gt;
    &lt;thead&gt;
        &lt;tr&gt;
            &lt;th class="col-md-5"&gt;Some Header&lt;/th&gt;
            &lt;th class="col-md-7"&gt;Some Header&lt;/th&gt;
        &lt;/tr&gt;
    &lt;/thead&gt;

    &lt;tbody&gt;
    &lt;s:iterator value="someCollection" var="someObj"&gt;
        &lt;tr&gt;
            &lt;td&gt;
                $&#123;someObj.someProp&#125;
            &lt;/td&gt;
            &lt;td&gt;
                $&#123;someObj.someProp&#125;
            &lt;/td&gt;
        &lt;/tr&gt;
    &lt;/s:iterator&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
    </s:param> --%>
</s:include>