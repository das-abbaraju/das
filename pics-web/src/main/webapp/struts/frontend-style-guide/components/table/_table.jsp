<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
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
                &lt;th&gt;Some Header&lt;/th&gt;
                &lt;th class="text-center"&gt;Some Date&lt;/th&gt;
                &lt;th class="text-right"&gt;Some Number&lt;/th&gt;
                &lt;th class="success text-center"&gt;
                    &lt;i class="icon-ok-sign icon-large" data-toggle="tooltip" data-placement="top"
                    title="" data-original-title="Some Tip" /&gt;&lt;/i&gt;
                &lt;/th&gt;
                &lt;th class="warning text-center"&gt;
                    &lt;i class="icon-warning-sign icon-large" data-toggle="tooltip" data-placement="top"
                    title="" data-original-title="Some Tip" /&gt;&lt;/i&gt;
                &lt;/th&gt;
                &lt;th class="danger text-center"&gt;
                    &lt;i class="icon-minus-sign-alt icon-large" data-toggle="tooltip" data-placement="top"
                    title="" data-original-title="Some Tip" /&gt;&lt;/i&gt;
                &lt;/th&gt;
            &lt;/tr&gt;
        &lt;/thead&gt;
        &lt;tbody&gt;
            &lt;tr&gt;
                &lt;td&gt;Some Data&lt;/td&gt;
                &lt;td class="text-center"&gt;2014-01-01&lt;/td&gt;
                &lt;td class="text-right"&gt;1,000.00&lt;/td&gt;
                &lt;td class="success text-center"&gt;
                    1
                &lt;/td&gt;
                &lt;td class="warning text-center"&gt;
                    1
                &lt;/td&gt;
                &lt;td class="danger text-center"&gt;
                    1
                &lt;/td&gt;
            &lt;/tr&gt;
            &lt;tr&gt;
                &lt;td&gt;Some Data&lt;/td&gt;
                &lt;td class="text-center"&gt;2014-01-02&lt;/td&gt;
                &lt;td class="text-right"&gt;2,000.00&lt;/td&gt;
                &lt;td class="success text-center"&gt;
                    2
                &lt;/td&gt;
                &lt;td class="warning text-center"&gt;
                    2
                &lt;/td&gt;
                &lt;td class="danger text-center"&gt;
                    2
                &lt;/td&gt;
            &lt;/tr&gt;
            &lt;tr&gt;
                &lt;td&gt;Some Data&lt;/td&gt;
                &lt;td class="text-center"&gt;2014-01-03&lt;/td&gt;
                &lt;td class="text-right"&gt;3,000.00&lt;/td&gt;
                &lt;td class="success text-center"&gt;
                    3
                &lt;/td&gt;
                &lt;td class="warning text-center"&gt;
                    3
                &lt;/td&gt;
                &lt;td class="danger text-center"&gt;
                    3
                &lt;/td&gt;
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