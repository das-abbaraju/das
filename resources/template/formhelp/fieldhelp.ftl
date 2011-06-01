<@s.if test="hasKey('${labelKey}.fieldhelp')">
	<div class="fieldhelp">
		<@s.if test="hasKey('${labelKey}.fieldhelptitle')">
			<h3><@s.text name="${labelKey}.fieldhelptitle"/></h3>
		</@s.if>
		<@s.else>
			<h3><@s.text name="${labelKey}"/></h3>
		</@s.else>
		<@s.text name="${labelKey}.fieldhelp"/>
	</div>
</@s.if>