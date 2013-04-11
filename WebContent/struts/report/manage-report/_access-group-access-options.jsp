<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="access-options btn-group pull-right">
    <button class="btn dropdown-toggle" data-toggle="dropdown" href="#">
        <s:if test="#group.editable">
            <i class="icon-edit"></i> Can Edit <span class="caret"></span>
        </s:if>
        <s:else>
            <i class="icon-eye-open"></i> Can View <span class="caret"></span>
        </s:else>
    </button>
    
    <ul class="dropdown-menu">
        <li class="edit">
            <a href="#"><i class="icon-edit"></i> Can Edit</a>
        </li>
        <li class="view">
            <a href="#"><i class="icon-eye-open"></i> Can View</a>
        </li>
        <li class="divider"></li>
        <li class="remove">
            <a href="#"><i class="icon-remove"></i> Remove</a>
        </li>
    </ul>
</div>