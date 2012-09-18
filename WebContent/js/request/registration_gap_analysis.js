(function ($) {
    PICS.define('request.RegistrationGapAnalysis', {
        methods: {
            init: function () {
                var element = $('.RegistrationGapAnalysis-page');

                if (element.length) {
                    element.delegate('.add', 'click', this.confirmCopyAndDeactivation);
                    element.delegate('.remove', 'click', this.confirmDeactivation);
                }
            },
            
            confirmCopyAndDeactivation: function(event) {
                var contractor = $(this).attr('data-contractor');
                var request = $(this).attr('data-request');
                
                return confirm(translate('JS.RegistrationGapAnalysis.ConfirmCopyAndDeactivation', [request, contractor]));
            },
            
            confirmDeactivation: function(event) {
                var request = $(this).attr('data-request');
                
                return confirm(translate('JS.RegistrationGapAnalysis.ConfirmDeactivation', [request]));
            }
        }
    });
})(jQuery);