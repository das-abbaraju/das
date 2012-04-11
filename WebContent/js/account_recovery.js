var RecaptchaOptions = {
   theme : 'white',
   custom_translations : {
       instructions_visual : translate('JS.Recaptcha.TypeTheTwoWords'),
       instructions_audio : translate('JS.Recaptcha.TypeWhatYouHear'),
       play_again : translate('JS.Recaptcha.PlaySoundAgain'),
       cant_hear_this : translate('JS.Recaptcha.DownloadSoundAsMP3'),
       visual_challenge : translate('JS.Recaptcha.GetAVisualChallenge'),
       audio_challenge : translate('JS.Recaptcha.GetAnAudioChallenge'),
       refresh_btn : translate('JS.Recaptcha.GetANewChallenge'),
       help_btn : translate('JS.global.Help')
   }
};

$(function() {
	$('a.showUser').click(function(){
		$('.showEmail').toggle();
		$('.showUser').toggle();
	});
	
	$('a.showEmail').click(function(){
		$('.showEmail').toggle();
		$('.showUser').toggle();
	});
});