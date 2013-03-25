<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section id="overview" class="guide">
    <div class="page-header">
        <h1>Overview</h1>
    </div>
    
    <div class="alert alert-info">
        <strong>BETA</strong> This section is currently under development.
    </div>
    
<pre>
add rule
add rules
</pre>
    
    <form action="" method="post" id="" name="" class="form-horizontal" data-init="validate" data-messages='{"currency":"Please enter the currency without a symbol","date":"Please use a valid YYYY-MM-DD format","email":"Please use a valid email address","integer":"Please enter a whole number","integernegative":"Please enter a negative whole number","integerpositive":"Please enter a positive whole number","minlength":"Minimum length is {0}","maxlength":"Max length is {0}","required":"Cannot be blankio"}'>
        <div class="control-group">
            <label class="control-label">Contractor Id</label>
            <div class="controls">
                <input type="text" placeholder="12345" id="" name="" data-required data-integerpositive />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">Email</label>
            <div class="controls">
                <input type="text" placeholder="john.smith@example.com" id="" name="" data-required data-email />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">Phone</label>
            <div class="controls">
                <input type="text" placeholder="Phone" id="" name="" data-required />
            </div>
        </div>
        <div class="control-group error">
            <label class="control-label">Currency</label>
            <div class="controls">
                <div class="input-prepend input-append">
                    <span class="add-on">$, etc.</span>
                    <input type="text" placeholder="1,000 or 1.000" id="" name="" data-required data-currency />
                    <span class="add-on">.00 or ,00</span>
                </div>
                
                <span class="help-inline validate-icon"><i class="icon-remove-sign icon-large"></i></span>
                <span class="help-block validate-error">Cannot be blankio</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">Date</label>
            <div class="controls">
                <input type="text" placeholder="YYYY-MM-DD" id="" name="" data-required data-date />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">Username</label>
            <div class="controls">
                <input type="text" placeholder="Username" id="" name="" data-required data-min-length="5" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">Password</label>
            <div class="controls">
                <input type="text" placeholder="Password" id="" name="" data-password />
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
                <button type="submit" class="btn">Submit</button>
                <button type="reset" class="btn">Reset</button>
            </div>
        </div>
    </form>
</section>