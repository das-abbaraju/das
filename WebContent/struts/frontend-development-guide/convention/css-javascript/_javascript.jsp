<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="javascript" class="guide">
    <div class="page-header">
        <h1>Javascript</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
    <h4>Variable Requirements:</h4>
    <ul>
        <li>Lower case</li>
        <li>Words are separated by "_" (underscore)</li>
        <li>Noun</li>
        <li>Begins with a letter</li>
        <li>[a-z][a-z0-9]+</li>
    </ul>
    
    <div class="example">
    
        <p>
            <strong>Note the spacing and indentation requirements.</strong>
        </p>
    
<pre class="prettyprint linenums lang-js">
var my_variable = true,
    that = this,
    undefined = true;
</pre>
    
    </div>
    
    <h4>Function Requirements:</h4>
    <ul>
        <li>Camel Case</li>
        <li>Verb</li>
        <li>Begins with lower case letter</li>
        <li>Letters only</li>
    </ul>
    
    <div class="example">
        
        <p>
            <strong>Note the spacing, brace and indentation requirements.</strong>
        </p>
        
<pre class="prettyprint linenums lang-js">
function getBread() {
    return "loaf";
}

var Person = {
    getName: function () {
        return 'Jeevez';
    }
};

// self executing anonymouse function
(function () {
    alert('hello');
}());
</pre>

    </div>

    <h4>Class Requirements:</h4>
    <ul>
        <li>Camel case</li>
        <li>Noun</li>
        <li>Begins with capital letter</li>
        <li>Letters only</li>
    </ul>
    
    <div class="example">
    
        <p>
            <strong>Note the spacing, brace, and indentation requirements.</strong>
        </p>
    
<pre class="prettyprint linenums lang-js">
var person = Object.create({
    getAge: function () {},
    getFirstName: function () {},
    getFullName: function () {},
    getLastName: function () {}
});

function Car() {
    this.doors = '',
    this.model = '',
    this.year = '';
}

var car = new Car();
</pre>
    
    </div>
</section>