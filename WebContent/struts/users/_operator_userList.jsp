<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:select list="getUsersList(opID)" listKey="name" listValue="name" id="contractor_numbers_value"
    name="number.value" value="%{newContractor.requestedByUser.id}"
    headerKey="0" headerValue="- %{getText('RequestNewContractor.Other')} -" />
    