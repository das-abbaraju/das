<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<label>Dependent Status</label>
<s:select list="dependentAuditStatus" name="rule.dependentAuditStatus" id="dAuditSelect" headerKey="" headerValue="Any" />