<%@ taglib prefix="s" uri="/struts-tags" %>

TEST
<s:iterator value="contractor.trades" id="conTrade">
<s:property value="conTrade.id" />
<br />
</s:iterator>