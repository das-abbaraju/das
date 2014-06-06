<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Namespace CSS style rules by nesting them within a parent page id.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">css-best-practices-2</s:param>

    <s:param name="example_code">
GOOD:
In plain CSS:
    
#myPageId .myChildClass {
    ...
}

#myPageId .myOtherChildClass span {
    ...
}

In SASS (allows nesting):

#myPageId {
    .myChildClass {
        ...
    }

    .myOtherChildClass span {
        ...
    }
}
    </s:param>
</s:include>