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
	<title>${contractor.name} PICS Membership Tag</title>

    <link rel="stylesheet" type="text/css" media="screen" href="css/badge/badge.css?v=${version}" />
    <script type="text/javascript" src="js/badge/badge.js?v=${version}"></script>
    <script type="text/javascript" src="js/zeroclipboard/ZeroClipboard.js?v=${version}"></script>
</head>
<body>
	<s:include value="conHeader.jsp"/>

    <div id="${actionName}-page">
        <s:text name="ContractorBadge.Title" />    
        <div class="badges">
            <select id="badgeSize">
                <option value="small"><s:text name="ContractorBadge.SmallTag" /></option>
                <option value="medium" selected><s:text name="ContractorBadge.MediumTag" /></option>
                <option value="large"><s:text name="ContractorBadge.LargeTag" /></option>
            </select>
            <div id="badge_80" class="badgeIcon">
                ${badge_80}
                <div id="clip_container_80" class="clip_container">
                    <a href="javascript:;" id="clip_button_80">
                        <s:text name="ContractorBadge.CopyCodeToClipboard" />
                    </a>
                    <br>
                    <a class="toggleCode" href="javascript:;">
                        <s:text name="global.ViewCode" />
                    </a>
                </div>
                <textarea>${badge_80}</textarea>                    
            </div>
            <div id="badge_100" class="badgeIcon">
                ${badge_100}
	            <div id="clip_container_100" class="clip_container">
	                <a href="javascript:;" id="clip_button_100">
	                    <s:text name="ContractorBadge.CopyCodeToClipboard" />
	                </a>
	                <br>
	                <a class="toggleCode" href="javascript:;">
	                    <s:text name="global.ViewCode" />
	                </a>
	            </div>
	            <textarea>${badge_100}</textarea>        
            </div>
            <div id="badge_150" class="badgeIcon">
                ${badge_150}
                <div id="clip_container_150" class="clip_container">
                    <a href="javascript:;" id="clip_button_150">
                        <s:text name="ContractorBadge.CopyCodeToClipboard" />
                    </a>
                    <br>
                    <a class="toggleCode" href="javascript:;">
                        <s:text name="global.ViewCode" />
                    </a>
                </div>
                <textarea>${badge_150}</textarea>               
            </div>

	        <div id="badgeControls">
	            <a href="${con_badge}" class="picsbutton positive"><s:text name="ContractorBadge.AddedTag" /></a>
                <br>
	            <s:if test="!taskCompleted">
	                <s:url var="con_badge" action="ContractorBadge" method="save">
	                    <s:param name="contractor" value="%{contractor.id}" />
	                </s:url>
	
	                <a href="${con_badge}" class="picsbutton">
	                    <s:text name="ContractorBadge.RemoveWithoutAdding" />
	                </a>
	            </s:if>
	        </div>
        </div>
        
        <div id="badgeSteps">
            <s:text name="ContractorBadge.Steps" />
        </div>        

        <div id="badgeInformation">
            <s:text name="ContractorBadge.Information" />
        </div>

    </div>
</body>