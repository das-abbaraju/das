<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="buttons" class="guide">
    <div class="page-header">
        <h1>Buttons</h1>
    </div>

    <p>
        Button styles can be applied to anything with the <code>.btn</code> class applied. However, typically you'll want to apply these to only <code>&lt;a&gt;</code> and <code>&lt;button&gt;</code> elements for the best rendering.
    </p>    
    
    <table class="table table-striped">
        <thead>
            <tr>
                <th>
                    Button
                </th>
                <th>
                    Disabled
                </th>
                <th>
                    class=""
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
                    <a class="btn disabled">Default</a>
                </td>
                <td>
                    <code>btn</code>
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
                    <a class="btn btn-primary disabled">Primary</a>
                </td>
                <td>
                    <code>btn btn-primary</code>
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
                    <a class="btn btn-info disabled">Info</a>
                </td>
                <td>
                    <code>btn btn-info</code>
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
                    <a class="btn btn-success disabled">Success</a>
                </td>
                <td>
                    <code>btn btn-success</code>
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
                    <a class="btn btn-warning disabled">Warning</a>
                </td>
                <td>
                    <code>btn btn-warning</code>
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
                    <a class="btn btn-danger disabled">Danger</a>
                </td>
                <td>
                    <code>btn btn-danger</code>
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
                    <a class="btn btn-inverse disabled">Inverse</a>
                </td>
                <td>
                    <code>btn btn-inverse</code>
                </td>
                <td>
                    Alternate dark gray button, not tied to a semantic action or use
                </td>
            </tr>
            <tr>
                <td>
                    <a class="btn btn-link">Link</a>
                </td>
                <td>
                    
                </td>
                <td>
                    <code>btn btn-link</code>
                </td>
                <td>
                    Alternate dark gray button, not tied to a semantic action or use
                </td>
            </tr>
        </tbody>
    </table>
    
    <div class="example">
        
<pre class="prettyprint linenums">
&lt;!-- URL --&gt;
&lt;s:url action="YOUR_ACTION" var="YOUR_ACTION_VARIABLE_NAME" /&gt;

&lt;a href="&#36;{YOUR_ACTION_VARIABLE_NAME}" class="btn btn-danger"&gt;Click Me&lt;/a&gt;
</pre>
        
    </div>
        
</section>