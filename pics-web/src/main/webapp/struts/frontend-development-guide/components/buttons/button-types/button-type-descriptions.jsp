<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<dl>
    <dt>Default</dt>
    <dd>Available actions that don't necessarily need to clicked to continue on with a workflow (e.g. edit)</dd>
    <dt>Primary</dt>
    <dd>Suggested action to continue on with a workflow (e.g. next). There may only be one primary button on a page or active section.</dd>
    <dt>Info</dt>
    <dd>Convention of usage is to be determined.</dd>
    <dt>Success</dt>
    <dd>Action that causes a "big" change (e.g. save, add). There may only be one Success button on a page or active section.</dd>
    <dt>Warning</dt>
    <dd>Convention of usage is to be determined.</dd>
    <dt>Danger</dt>
    <dd>Actions that will have consequences; usually in a negative or damaging way (e.g. delete). These actions may be followed by a confirmation modal (link to modal) if the action will have a deep or lasting effect.</dd>
</dl>