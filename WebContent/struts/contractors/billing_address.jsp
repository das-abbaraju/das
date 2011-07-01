<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="country.equals('Canada')">
	PICS <br>
	100, 111 - 5 Avenue SW <br> 
	Suite # 124 <br>
	Calgary, AB T2P 3Y6 

</s:if>
<s:else>
	PICS <br>
	P.O. Box 51387 <br>
	Irvine, CA 92619-1387
</s:else>