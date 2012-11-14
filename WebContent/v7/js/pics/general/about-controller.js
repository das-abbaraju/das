(function ($) {
    PICS.define('general.AboutController', {
        methods: {
            init: function () {
                if ($('#About__page').length > 0) {
                    var that = this;
                    
                    $('.show-privacy-policy').on('click', function (event) {
                        var element = $(this);
                        
                        element.remove();
                        
                        that.togglePrivacyPolicy();
                    });
                }
            },
            
            togglePrivacyPolicy: function () {
                PICS.ajax({
                    url: 'PrivacyPolicy.action',
                    success: function (data, textStatus, jqXHR) {
                        $('.privacy-policy').html(data);
                    }
                });
            }
        }
    });
}(jQuery));