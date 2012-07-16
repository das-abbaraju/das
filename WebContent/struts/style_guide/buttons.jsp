<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/page_header.jsp">
    <s:param name="title">PICS Style Guide</s:param>
</s:include>

<s:include value="/struts/style_guide/_menu.jsp" />

<div class="row-fluid">
    <div class="span6">
        <table class="table table-bordered table-striped">
            <thead>
                <tr>
                    <th>
                        Tag
                    </th>
                    <th>
                        Description
                    </th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <a class="btn">Default</a>
                    </td>
                    <td>
                        Standard gray button with gradient
                    </td>
                </tr>
                <tr>
                    <td>
                        <a class="btn btn-primary">Primary</a>
                    </td>
                    <td>
                        Provides extra visual weight and identifies the primary action in a set of buttons
                    </td>
                </tr>
                <tr>
                    <td>
                        <a class="btn btn-info">Info</a>
                    </td>
                    <td>
                        Used as an alternative to the default styles
                    </td>
                </tr>
                <tr>
                    <td>
                        <a class="btn btn-success">Success</a>
                    </td>
                    <td>
                        Indicates a successful or positive action
                    </td>
                </tr>
                <tr>
                    <td>
                        <a class="btn btn-warning">Warning</a>
                    </td>
                    <td>
                        Indicates caution should be taken with this action
                    </td>
                </tr>
                <tr>
                    <td>
                        <a class="btn btn-danger">Danger</a>
                    </td>
                    <td>
                        Indicates a dangerous or potentially negative action
                    </td>
                </tr>
                <tr>
                    <td>
                        <a class="btn btn-inverse">Inverse</a>
                    </td>
                    <td>
                        Alternate dark gray button, not tied to a semantic action or use
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="span6">
    
<pre class="prettyprint linenums">
&lt;a class="btn"&gt;Default&lt;/a&gt;

&lt;a class="btn btn-primary"&gt;Primary&lt;/a&gt;

&lt;a class="btn btn-info"&gt;Info&lt;/a&gt;

&lt;a class="btn btn-success"&gt;Success&lt;/a&gt;

&lt;a class="btn btn-warning"&gt;Warning&lt;/a&gt;

&lt;a class="btn btn-danger"&gt;Danger&lt;/a&gt;

&lt;a class="btn btn-inverse"&gt;Inverse&lt;/a&gt;
</pre>

    </div>
</div>