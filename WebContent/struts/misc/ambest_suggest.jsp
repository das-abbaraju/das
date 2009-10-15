<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="results">
<s:property value="companyName"/> (<s:property value="naic"/>)|<s:property value="companyName"/>|<s:property value="naic"/>
</s:iterator>
