<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="badge_80">
    <s:include value="/struts/badge/_badge.jsp">
        <s:param name="host">${requestHost}</s:param>
        <s:param name="hash">${hash}</s:param>
        <s:param name="size">80</s:param>
    </s:include>
</s:set>

<s:set var="badge_100">
    <s:include value="/struts/badge/_badge.jsp">
        <s:param name="host">${requestHost}</s:param>
        <s:param name="hash">${hash}</s:param>
        <s:param name="size">100</s:param>
    </s:include>
</s:set>

<s:set var="badge_150">
    <s:include value="/struts/badge/_badge.jsp">
        <s:param name="host">${requestHost}</s:param>
        <s:param name="hash">${hash}</s:param>
        <s:param name="size">150</s:param>
    </s:include>
</s:set>

<head>
	<title>${contractor.name}</title>
    
    <link rel="stylesheet" type="text/css" media="screen" href="css/badge/badge.css?v=${version}" />
    <script type="text/javascript" src="js/badge/badge.js?v=${version}"></script>
    <script type="text/javascript" src="js/zeroclipboard/ZeroClipboard.js?v=${version}"></script>
</head>
<body>
	<s:include value="conHeader.jsp"/>
    
    <div id="${actionName}-page">
        <s:text name="ContractorBadge.Information" />

		<s:if test="!taskCompleted">        
	        <s:url var="con_badge" action="ContractorBadge" method="save">
	        	<s:param name="contractor" value="%{contractor.id}" />
	        </s:url>
	        
	        <a href="${con_badge}">
	        	<s:text name="ContractorBadge.Confirm" />
	        </a>
        </s:if>
        
        <ul class="badges">
            <li>
                <div class="badge">
                    ${badge_80}
                </div>
                
                <div class="code">
                    <textarea>${badge_80}</textarea>
                    
                    <div id="clip_container_80" class="clip_container">
                        <a href="javascript:;" id="clip_button_80">
                            <s:text name="global.CopyToClipboard" />
                        </a>
                    </div>
                </div>
            </li>
            <li>
                <div class="badge">
                    ${badge_100}
                </div>
                
                <div class="code">
                    <textarea>${badge_100}</textarea>
                    
                    <div id="clip_container_100" class="clip_container">
                        <a href="javascript:;" id="clip_button_100">
                            <s:text name="global.CopyToClipboard" />
                        </a>
                    </div>
                </div>
            </li>
            <li>
                <div class="badge">
                    ${badge_150}
                </div>
                
                <div class="code">
                    <textarea>${badge_150}</textarea>
                    
                    <div id="clip_container_150" class="clip_container">
                        <a href="javascript:;" id="clip_button_150">
                            <s:text name="global.CopyToClipboard" />
                        </a>
                    </div>
                </div>
            </li>
        </ul>
        
    </div>
</body>