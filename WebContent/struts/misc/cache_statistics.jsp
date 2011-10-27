<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Cache Statistics</title>
</head>
<body>

<h1>Cache Statistics</h1>

<s:if test="caches.size() == 0">
	<div class="info">Ehcache Empty</div>
</s:if>
<s:else>
	<s:div cssClass="alert">
		<h4>Total Memory Usage: <s:property value="totalMemoryUsage/1000000" /> MBs</h4>
		<h4>Total Elements In Memory: <s:property value="totalSize" /></h4>
	</s:div>
	<s:iterator value="caches">
		<s:div cssClass="alert">
			<h3><s:property value="name" /></h3>
			<form>
				<s:hidden name="cacheName" value="%{name}" />
				<p>
					# of Items: <strong><s:property value="size" /></strong><br/>
					Memory Used: <strong><s:property value="calculateInMemorySize()/1000000" /> MBs</strong><br/>
					Average Element Size: <strong><s:property value="calculateInMemorySize()/size/1000" /> KBs</strong><br/>
					Total Possible Size: <strong><s:property value="cacheConfiguration.maxElementsInMemory" /></strong><br/>
					Total Approximate Memory Usage: <strong><s:property value="((calculateInMemorySize()/size)*cacheConfiguration.maxElementsInMemory)/1000000" /> MBs</strong><br />
					Time To Live: <strong><s:property value="cacheConfiguration.timeToLiveSeconds == 0 ? '∞' : cacheConfiguration.timeToLiveSeconds + 's'" /></strong><br />
					Time To Idle: <strong><s:property value="cacheConfiguration.timeToIdleSeconds == 0 ? '∞' : cacheConfiguration.timeToIdleSeconds + 's'" /></strong><br />
					Eviction Policy: <strong><s:property value="cacheConfiguration.memoryStoreEvictionPolicy" /></strong>
					<s:submit method="getElements" cssClass="picsbutton" cssStyle="float:right;" value="View Elements In Cache" />
				</p>
			</form>
		</s:div>
	</s:iterator>
</s:else>

</body>
</html>