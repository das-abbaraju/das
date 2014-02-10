<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("delete_url") != null) { %>
<s:set var="delete_url">${param.delete_url}</s:set>
<% } %>

<% if (request.getParameter("modal_title") != null) { %>
<s:set var="modal_title">${param.modal_title}</s:set>
<% } %>

<% if (request.getParameter("modal_message") != null) { %>
<s:set var="modal_message">${param.modal_message}</s:set>
<% } %>


<div class="modal fade unassignModal" tabindex="-1" role="dialog" aria-labelledby="unassignModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">${modal_title}</h4>
      </div>
      <div class="modal-body">
        <p>${modal_message}</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <button class="btn btn-warning unassign">Unassign</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
