(function ($) {
    
    PICS.define('registration.ConPaymentOptions', {
        
        methods: {
            init: function ()  {
                
                //if ($('#ConPaymentOptions-page').length) {
                    this.addFieldHelp();
                //}
            },
            
            addFieldHelp: function () {
                var element = $('.help-text');

                element.each(function (key, value) {
                    alert(value);
                    var html = $(this).html();
                    var label = $(this).siblings('label');
                    var input = $(this).siblings('input[type=text], input[type=password], select');
        
                    label.attr('title', label.html().replace(':', ''));
                    label.attr('data-content', html.replace('"', "'"));
        
                    label.popover({
                        placement: 'bottom',
                        trigger: 'manual'
                    });
        
                    input.bind('focus', function (event) {
                        label.popover('show');
                    });
        
                    input.bind('blur', function (event) {
                        label.popover('hide');
                    });      
                });                
            }
        }      
    });
          
})(jQuery);