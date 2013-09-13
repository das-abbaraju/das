<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="host">${param.host}</s:set>
<s:set var="hash">${param.hash}</s:set>
<s:set var="size">${param.size}</s:set>

<!-- PICS Membership Tag START -->
<script type="text/javascript" data-size="${size}" src="${host}/badge/badge.js#pb-id=${hash}"></script>
<!-- PICS Membership Tag END -->