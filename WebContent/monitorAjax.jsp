<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<%@page import="net.sf.ehcache.CacheManager"%>
<%@page import="net.sf.ehcache.Cache"%>
<%@page import="net.sf.ehcache.Statistics"%>
<table class="report">
	<tr>
		<td><strong>Cache Name</strong></td>
		<td><strong>Size</strong></td>
		<td><strong>Hits</strong></td>
		<td><strong>Misses</strong></td>
	</tr>

<%
CacheManager.create();
String[] cacheNames = CacheManager.getInstance().getCacheNames();

for( String cn : cacheNames )
{
	Cache cache = CacheManager.getInstance().getCache(cn);

	Statistics stats = cache.getStatistics();
	%>
	<tr>
		<td><%= cn %></td>
		<td><%= stats.getObjectCount() %></td>
		<td><%= stats.getCacheHits() %>
		<br />M:<%= stats.getInMemoryHits() %>
		<br />D:<%= stats.getOnDiskHits() %>
		</td>
		<td><%= stats.getCacheMisses() %></td>
	</tr>
<% 	} %>
</table>
