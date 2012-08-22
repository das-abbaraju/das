<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">PICS Style Guide</s:param>
</s:include>

<s:include value="/struts/style_guide/_menu.jsp" />

<%-- URL --%>
<s:url action="PicsStyleGuide" method="forms" var="pics_style_guide_forms" />

<div class="row-fluid">
    <div class="span6">
        <s:form cssClass="well form-horizontal" action="%{#pics_style_guide_forms}" name="YOUR_FORM_NAME" id="YOUR_FORM_ID">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="field_1">Label 1</label>
                    <div class="controls">
                        <input type="text" name="field_1" id="field_1">
                    </div>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary" name="save">Save</button>
                    <button type="submit" class="btn" name="save_add">Save and Add</button>
                    <a href="${pics_style_guide_forms}" class="btn">Back to List</a>
                </div>
            </fieldset>
        </s:form>
    </div>
    <div class="span6">
    
<pre class="prettyprint linenums">
&lt;%-- URL --%&gt;
&lt;s:url action="YOUR_ACTION" var="YOUR_ACTION_VARIABLE_NAME" /&gt;
&lt;s:url action="YOUR_ACTION" method="YOUR_METHOD" var="YOUR_ACTION_METHOD_VARIABLE_NAME" /&gt;

&lt;s:form cssClass="well form-horizontal" action="%{#YOUR_ACTION_METHOD_VARIABLE_NAME}" name="YOUR_FORM_NAME" id="YOUR_FORM_ID"&gt;
    &lt;fieldset&gt;
        &lt;div class="control-group"&gt;
            &lt;label class="control-label" for="field_1"&gt;Label 1&lt;/label&gt;
            &lt;div class="controls"&gt;
                &lt;input type="text" name="field_1" id="field_1"&gt;
            &lt;/div&gt;
        &lt;/div&gt;
        &lt;div class="form-actions"&gt;
            &lt;button type="submit" class="btn btn-primary" name="save"&gt;Save&lt;/button&gt;
            &lt;button type="submit" class="btn" name="save_add"&gt;Save and Add&lt;/button&gt;
            &lt;a href="\${YOUR_ACTION_VARIABLE_NAME}" class="btn"&gt;Back to List&lt;/a&gt;
        &lt;/div&gt;
    &lt;/fieldset&gt;
&lt;/s:form&gt;
</pre>

    </div>
</div>