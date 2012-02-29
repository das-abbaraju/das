<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="country.canada">
	PICS <br>
	100, 111 - 5 Avenue SW <br> 
	Suite # 124 <br>
	Calgary, AB T2P 3Y6 
</s:if>
<s:elseif test="country.uk">
	PICS Ltd<br/>
	314 Midsummer Court<br/>
	Milton Keynes<br/>
	United Kingdom<br/>
	MK9 2RG
</s:elseif>
<s:else>
	PICS <br>
	P.O. Box 51387 <br>
	Irvine, CA 92619-1387
</s:else>