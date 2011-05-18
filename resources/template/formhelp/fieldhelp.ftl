<div class="fieldhelp">
	<@s.if test="hasKey(getTranslationName(parameters.name) + '.fieldhelptitle')">
		<h3><@s.text name="%{getTranslationName(parameters.name)}.fieldhelptitle"/></h3>
	</@s.if>
	<@s.else>
		<h3><@s.text name="%{getTranslationName(parameters.name)}"/></h3>
	</@s.else>
	<@s.text name="%{getTranslationName(parameters.name)}.fieldhelp"/>
</div>