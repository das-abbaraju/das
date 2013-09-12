UPDATE IGNORE app_translation t SET t.msgValue = '<SubscriptionHeader>
This is an automatic reminder that the following Policies for <CompanyName> have or are about to expire.
<br/>
<br/>
#foreach($audit in $contractor.expiringPoliciesForInsuranceExpirationEmail)
	#foreach($operator in $audit.operators)
		#if($operator.status.pending && $operator.visible)
			$audit.auditType.name for $operator.operator.name Expires On $pics_dateTool.format("yyyy-MM-dd",$audit.expiresDate)<br/>
		#end
	#end
#end
<br/>
<br/>
Please upload a new insurance certificate using the insurance requirements of the above.<br/>
If we do not receive this certificate prior to the expiration you may not be permitted to enter the facility.<br/>
As always we appreciate your cooperation and are here to answer any questions you may have.<br/>
<br/>
Thank you,<br/>
<CSRName><br/>
PICS<br/>
tel: <CSRPhone><br/>
fax: <CSRFax><br/>
<CSREmail><br/>
<br/>
<SubscriptionFooter>' WHERE t.msgKey = 'EmailTemplate.10.translatedBody' AND t.locale = 'en';

UPDATE IGNORE app_translation t SET t.msgValue = '<SubscriptionHeader>
Dies ist eine automatische Erinnerung, dass die folgenden Policen für <CompanyName> abgelaufen sind oder in Kürze ablaufen werden.
<br/>
<br/>
#foreach($audit in $contractor.expiringPoliciesForInsuranceExpirationEmail)
	#foreach($operator in $audit.operators)
		#if($operator.status.pending && $operator.visible)
			$audit.auditType.name for $operator.operator.name Expires On $pics_dateTool.format("yyyy-MM-dd",$audit.expiresDate)<br/>
		#end
	#end
#end
<br/>
<br/>
Bitte laden Sie ein neues Versicherungszertifikat unter Anwendung der oben genannten Versicherungsanforderungen hoch. <br/>
Wenn wir dieses Zertifikat nicht vor Ablauf erhalten, können wir Ihnen nicht erlauben, die Anlage zu betreten. <br/>
Wie immer wissen wir Ihre Mitarbeit zu schätzen und stehen für alle Ihre Fragen gerne zur Verfügung. <br/>
<br/>
Vielen Dank,<br/>
<CSRName><br/>
PICS<br/>
tel: <CSRPhone><br/>
fax: <CSRFax><br/>
<CSREmail><br/>
<br/>
<SubscriptionFooter>' WHERE t.msgKey = 'EmailTemplate.10.translatedBody' AND t.locale = 'de';

UPDATE IGNORE app_translation t SET t.msgValue = '<SubscriptionHeader>
Este es un recordatorio automático de que las siguientes pólizas de <CompanyName> ya vencieron o están a punto de vencer.
<br/>
<br/>
#foreach($audit in $contractor.expiringPoliciesForInsuranceExpirationEmail)
	#foreach($operator in $audit.operators)
		#if($operator.status.pending && $operator.visible)
			$audit.auditType.name para $operator.operator.name vence el $pics_dateTool.format("yyyy-MM-dd",$audit.expiresDate)<br/>
		#end
	#end
#end
<br/>
<br/>
Cargue un nuevo certificado de seguro con los requisitos de seguro de lo anterior.<br/>
Si no recibimos este certificado antes del vencimiento, es posible que no se le permita ingresar a las instalaciones.<br/>
Como siempre, agradecemos su cooperación y estamos a su disposición para responder las preguntas que tenga.<br/>
<br/>
Gracias, <br/>
<CSRName><br/>
PICS<br/>
tel.: <CSRPhone><br/>
fax: <CSRFax><br/>
<CSREmail><br/>
<br/>
<SubscriptionFooter>' WHERE t.msgKey = 'EmailTemplate.10.translatedBody' AND t.locale = 'es_MX';

UPDATE IGNORE app_translation t SET t.msgValue = '<SubscriptionHeader>
Tämä on automaattinen muistutus siitä, että seuraavat <CompanyName>:n menettelytavat ovat vanhentuneet tai vanhentumassa.
<br/>
<br/>
#foreach($audit in $contractor.expiringPoliciesForInsuranceExpirationEmail)
	#foreach($operator in $audit.operators)
		#if($operator.status.pending && $operator.visible)
			$audit.auditType.name for $operator.operator.name Expires On $pics_dateTool.format("yyyy-MM-dd",$audit.expiresDate)<br/>
		#end
	#end
#end
<br/>
<br/>
Lataa uusi vakuutussertifikaatti käyttäen yllä olevia vakuutusvaatimuksiasi.<br/>
Jo emme saa tätä sertifikaattia ennen vanhentumista, sinua ei saateta päästää laitokseen.<br/>
Arvostamme aina yhteistyötäsi ja olemme täällä vastataksemme kaikkiin kysymyksiin, joita sinulla saattaa olla. <br/>
<br/>
Kiitos<br/>
<CSRName><br/>
PICS<br/>
puh: <CSRPhone><br/>
faksi: <CSRFax><br/>
<CSREmail><br/>
<br/>
<SubscriptionFooter>' WHERE t.msgKey = 'EmailTemplate.10.translatedBody' AND t.locale = 'fi';

UPDATE IGNORE app_translation t SET t.msgValue = '<SubscriptionHeader>
Ce ci est un automatique rappel que les politiques suivantes pour <CompanyName> sont ou ont sur le point d''expirer.
<br/>
<br/>
#foreach($audit in $contractor.expiringPoliciesForInsuranceExpirationEmail)
	#foreach($operator in $audit.operators)
		#if($operator.status.pending && $operator.visible)
			$audit.auditType.name for $operator.operator.name Expires On $pics_dateTool.format("yyyy-MM-dd",$audit.expiresDate)<br/>
		#end
	#end
#end
<br/>
<br/>
Veuillez télécharger un nouveau certificat d''assurance en utilisant les exigences d''assurance de la ci-dessus.<br/>
Si nous ne recevons pas ce certificat avant son expiration, vous ne serez pas autorisés d''entrer au site de votre client(s).<br/>
Comme toujours, nous vous remercions de votre coopération et nous sommes là pour répondre à toutes les questions que vous pourriez avoir.<br/>
<br/>
Merci,<br/>
<CSRName><br/>
PICS<br/>
tel: <CSRPhone><br/>
fax: <CSRFax><br/>
<CSREmail><br/>
<br/>
<SubscriptionFooter>
 ' WHERE t.msgKey = 'EmailTemplate.10.translatedBody' AND t.locale = 'fr';

UPDATE IGNORE app_translation t SET t.msgValue = '<SubscriptionHeader>
Dit is een automatische herinnering dat het volgende beleid voor <CompanyName> is verlopen of spoedig zal verlopen.
<br/>
<br/>
#foreach($audit in $contractor.expiringPoliciesForInsuranceExpirationEmail)
	#foreach($operator in $audit.operators)
		#if($operator.status.pending && $operator.visible)
			$audit.auditType.name voor $operator.operator.name verloopt op $pics_dateTool.format("dd-mm-jjjj",$audit.expiresDate)<br/>
		#end
	#end
#end
<br/>
<br/>
Upload een nieuw verzekeringscertificaat waarbij u rekening houdt met de bovenstaande verzekeringseisen.<br/>
Indien we dit certificaat niet voor de verloopdatum ontvangen, kunt u niet op de vestiging worden toegelaten.<br/>
Zoals altijd waarderen wij uw medewerking en staan wij voor u klaar om u te helpen indien u vragen heeft.<br/>
<br/>
Hartelijk dank,<br/>
<CSRName><br/>
PICS<br/>
tel: <CSRPhone><br/>
fax: <CSRFax><br/>
<CSREmail><br/>
<br/>
<SubscriptionFooter>' WHERE t.msgKey = 'EmailTemplate.10.translatedBody' AND t.locale = 'nl';

UPDATE IGNORE app_translation t SET t.msgValue = '<SubscriptionHeader>
Dette er en automatisk påminnelse om at følgende poliser for <CompanyName> har eller holder på å utløpe.
<br/>
<br/>
#foreach($audit in $contractor.expiringPoliciesForInsuranceExpirationEmail)
	#foreach($operator in $audit.operators)
		#if($operator.status.pending && $operator.visible)
			$audit.auditType.name for $operator.operator.name Utløper den $pics_dateTool.format("yyyy-MM-dd",$audit.expiresDate)<br/>
		#end
	#end
#end
<br/>
<br/>
Last opp en ny forsikringsattest ved å bruke forsikringskravene i ovennevnte.<br/>
Om vi ikke mottatt denne attesten før utløpsdato, har dere ikke tillatelse til å gå inn på anlegget.<br/>
Som alltid er vi takknemlige for deres samarbeidsvilje og er tilgjengelige for eventuelle spørsmål.<br/>
<br/>
Takk,<br/>
<CSRName><br/>
PICS<br/>
tel.: <CSRPhone><br/>
faks.: <CSRFax><br/>
<CSREmail><br/>
<br/>
<SubscriptionFooter>' WHERE t.msgKey = 'EmailTemplate.10.translatedBody' AND t.locale = 'no';

UPDATE IGNORE app_translation t SET t.msgValue = '<SubscriptionHeader>
Detta är en automatisk påminnelse om att följande riktlinjer för <CompanyName> har eller håller på att gå ut.
<br/>
<br/>
#foreach($audit in $contractor.expiringPoliciesForInsuranceExpirationEmail)
	#foreach($operator in $audit.operators)
		#if($operator.status.pending && $operator.visible)
			$i18nCache.getText($audit.auditType.name.key, $locale) för $operator.operator.name Går ut $pics_dateTool.format("yyyy-MM-dd",$audit.expiresDate)<br/>
		#end
	#end
#end
<br/>
<br/>
Ladda upp ett nytt försäkringsintyg med hjälp av försäkringskraven ovan.<br/>
Om vi inte får in det här intyget före förfallodatum kan det hända att ni inte får tillstånd att gå in i anläggningen.<br/>
Som alltid är vi tacksamma för er samarbetsvillighet och är tillgängliga för eventuella frågor.<br/>
<br/>
Tack!<br/>
<CSRName><br/>
PICS<br/>
tel: <CSRTelefon><br/>
fax: <CSRFax><br/>
<CSREmail><br/>
<br/>
<SubscriptionFooter>' WHERE t.msgKey = 'EmailTemplate.10.translatedBody' AND t.locale = 'sv';
