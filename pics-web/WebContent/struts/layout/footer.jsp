<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<footer>
	<ul class="footer-menu">
		<li>
			Copyright © 2011
		</li>
		<li>
			<s:a href="http://www.picsauditing.com/">PICS</s:a>
		</li>
		<li>
			<s:a action="Contact"><s:text name="FOOTER.contact" /></s:a>
		</li>
		<li>
			<s:a action="Privacy"><s:text name="FOOTER.Privacy" /></s:a>
		</li>
	</ul>
	
	<div class="stats">
		<span class="version"><s:text name="FOOTER.version" /> ${version}</span>
	</div>
</footer>