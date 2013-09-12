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
    
    <form action="/" method="post" id="contractor_form" name="contractor_form" class="form-horizontal" autocomplete="off" data-init="validate" data-messages='{"required":"En Blanco"}'>
        <div class="control-group">
            <label class="control-label" for="contractor_id">Contractor Id</label>
            <div class="controls">
                <input type="text" placeholder="12345" id="contractor_id" name="contractor_id" data-required data-integerpositive />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="contractor_email">Email</label>
            <div class="controls">
                <input type="text" placeholder="john.smith@example.com" id="contractor_email" name="contractor_email" data-required data-email />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="contractor_currency">Currency</label>
            <div class="controls">
                <div class="input-prepend">
                    <span class="add-on">$, etc.</span>
                    <input type="text" placeholder="1,000 or 1.000" id="contractor_currency" name="contractor_currency" data-currency />
                </div>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="contractor_date">Date</label>
            <div class="controls">
                <input type="text" placeholder="YYYY-MM-DD" id="contractor_date" name="contractor_date" data-date />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="contractor_username">Username</label>
            <div class="controls">
                <input type="text" placeholder="Username" id="contractor_username" name="contractor_username" data-required data-minlength="5" data-maxlength="100" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="contractor_password">Password</label>
            <div class="controls">
                <input type="password" placeholder="Password" id="contractor_password" name="contractor_password" data-required />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="contractor_retype_password">Match Password</label>
            <div class="controls">
                <input type="password" placeholder="Password" id="contractor_retype_password" name="contractor_retype_password" data-match="#contractor_password" />
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