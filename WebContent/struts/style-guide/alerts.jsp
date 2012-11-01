<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">PICS Style Guide</s:param>
</s:include>

<s:include value="/struts/style-guide/_style-guide-menu.jsp" />

<div class="row">
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
                        <div class="alert">
                            <button type="button" class="close" data-dismiss="alert">×</button>
                            <strong>Warning!</strong> Best check yo self, you're not looking too good.
                        </div>
                    </td>
                    <td>
                        
                    </td>
                </tr>
                <tr>
                    <td>
                        <div class="alert alert-error">
                            <button type="button" class="close" data-dismiss="alert">×</button>
                            <strong>Oh snap!</strong> Change a few things up and try submitting again.
                        </div>
                    </td>
                    <td>
                        
                    </td>
                </tr>
                <tr>
                    <td>
                        <div class="alert alert-success">
                            <button type="button" class="close" data-dismiss="alert">×</button>
                            <strong>Well done!</strong> You successfully read this important alert message.
                        </div>
                    </td>
                    <td>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div class="alert alert-info">
                            <button type="button" class="close" data-dismiss="alert">×</button>
                            <strong>Heads up!</strong> This alert needs your attention, but it's not super important.
                        </div>
                    </td>
                    <td>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div class="alert alert-block">
                            <button type="button" class="close" data-dismiss="alert">×</button>
                            <h4>Warning!</h4>
                            <p>Best check yo self, you're not looking too good. Nulla vitae elit libero, a pharetra augue. Praesent commodo cursus magna, vel scelerisque nisl consectetur et.</p>
                        </div>
                    </td>
                    <td>
                    
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="span6">
    
<pre class="prettyprint linenums">
&lt;div class="alert"&gt;
    &lt;button type="button" class="close" data-dismiss="alert"&gt;×&lt;/button&gt;
    &lt;strong&gt;Warning!&lt;/strong&gt; Best check yo self, you're not looking too good.
&lt;/div&gt;

&lt;div class="alert alert-error"&gt;
    &lt;button type="button" class="close" data-dismiss="alert"&gt;×&lt;/button&gt;
    &lt;strong&gt;Oh snap!&lt;/strong&gt; Change a few things up and try submitting again.
&lt;/div&gt;
                        
&lt;div class="alert alert-success"&gt;
    &lt;button type="button" class="close" data-dismiss="alert"&gt;×&lt;/button&gt;
    &lt;strong&gt;Well done!&lt;/strong&gt; You successfully read this important alert message.
&lt;/div&gt;
                        
&lt;div class="alert alert-info"&gt;
    &lt;button type="button" class="close" data-dismiss="alert"&gt;×&lt;/button&gt;
    &lt;strong&gt;Heads up!&lt;/strong&gt; This alert needs your attention, but it's not super important.
&lt;/div&gt;
                        
&lt;div class="alert alert-block"&gt;
    &lt;button type="button" class="close" data-dismiss="alert"&gt;×&lt;/button&gt;
    &lt;h4&gt;Warning!&lt;/h4&gt;
    &lt;p&gt;Best check yo self, you're not looking too good. Nulla vitae elit libero, a pharetra augue. Praesent commodo cursus magna, vel scelerisque nisl consectetur et.&lt;/p&gt;
&lt;/div&gt;
</pre>

    </div>
</div>