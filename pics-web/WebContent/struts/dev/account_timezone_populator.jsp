<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
    <head>
        <title>Account And User Timezone Populator</title>
        <script type="text/javascript">
            $(document).ready(checkProgress);

            function checkProgress() {
                $.getJSON('AccountTimezonePopulator!progressOnConversion.action', function(json) {
                    $('#progress').html('Info: ' + json.info + '<br/>Completed ' + json.accountsConverted + ' out of ' + json.totalAccountsWillRun + ' of ' + json.totalAccounts + ' total accounts with null timezones<br/>');
                });
                setTimeout(checkProgress, 10000);
            }
        </script>
    </head>
    <body>
    	<s:include value="../actionMessages.jsp"></s:include>
    	
        <s:form id="runConversionForm">
            <p>How many contractors should we run in a batch: <s:textfield name="totalAccountsWillRun" /></p>
            <p>Run timezone conversion: <s:submit value="Start Conversion" method="startConversion" /></p>
        </s:form>
        <div id="progress"/>
    </body>
</html>