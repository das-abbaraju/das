<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<dl class="row">
    <dt class="col-md-2">Default</dt>
    <dd class="col-md-9">Available actions that don't necessarily need to clicked to continue on with a workflow (e.g. edit)</dd>
    <dt class="col-md-2">Primary</dt>
    <dd class="col-md-9">Suggested action to continue on with a workflow (e.g. next). There may only be one primary button on a page or active section.</dd>
    <dt class="col-md-2">Info</dt>
    <dd class="col-md-9">Convention of usage is to be determined.</dd>
    <dt class="col-md-2">Success</dt>
    <dd class="col-md-9">Action that causes a "big" change (e.g. save, add). There may only be one Success button on a page or active section.</dd>
    <dt class="col-md-2">Warning</dt>
    <dd class="col-md-9">Convention of usage is to be determined.</dd>
    <dt class="col-md-2">Danger</dt>
    <dd class="col-md-9">Actions that will have consequences; usually in a negative or damaging way (e.g. delete). These actions may be followed by a confirmation modal (link to modal) if the action will have a deep or lasting effect.</dd>
</dl>