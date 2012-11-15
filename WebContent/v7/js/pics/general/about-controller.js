(function ($) {
    PICS.define('general.AboutController', {
        methods: {
            init: function () {
                if ($('#About__page').length > 0) {
                    var that = this;
                    
                    $('.show-privacy-policy').on('click', function (event) {
                        that.onShowPrivacyPolicy.call(that, event);
                    });
                    
                    this.initVersionDetailTooltip();
                }
            },
            
            initVersionDetailTooltip: function () {
                var version_detail_tooltip = $('.version a');
                
                version_detail_tooltip.tooltip({
                    placement: 'right',
                    trigger: 'manual'
                });
                
                version_detail_tooltip.on('click', function (event) {
                    $(this).data('tooltip').toggle();
                    
                    event.preventDefault();
                });
            },
            
            onShowPrivacyPolicy: function (event) {
                var element = $(event.target);
                
                element.toggleClass('active');
                
                this.togglePrivacyPolicy();
            },
            
            togglePrivacyPolicy: function () {
                var element = $('.privacy-policy'),
                    content = element.html();
                
                if (content.length == 0) {
                    PICS.ajax({
                        url: 'PrivacyPolicy.action',
                        success: function (data, textStatus, jqXHR) {
                            element.html(data);
                        }
                    });
                } else if (element.is(':visible')) {
                    element.hide();
                } else {
                    element.show();
                }
            }
        }
    });
}(jQuery));