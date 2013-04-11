<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<form action="/" method="POST">
    <ul>
        <li>
            <label>Override Email</label>
            <input type="text" name="override_email" />
        </li>
        <li>
            <label>Template ID</label>
            <input type="text" name="template_id" />
        </li>
        <li>
            <label>Invoice ID</label>
            <input type="text" name="invoice_id" />
        </li>
    </ul>
    
    <button type="submit">Submit</button>
</form>