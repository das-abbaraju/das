<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- <link rel="stylesheet" type="text/css" href="css/pics.css"> -->
<link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme-menu.css">

<script type="text/javascript" src="js/pics/extjs/bootstrap.js"></script>

<nav id="site_navigation"></nav>

<ul>
    <s:iterator value="insuranceLimits" var="limit">
    <li>
        Operator: ${limit.operatorAccount.name}
        Insurance Criteria: ${limit.flagCriteria.label}
        Calculated Limit: ${limit.insuranceLimit}
    </li>
    </s:iterator>
</ul>