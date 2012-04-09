(function ($) {
    PICS.define('audit.AuditController', {
        methods: {
            init: function () {
                if ($('#Audit__page').length) {
                    var cao_table = $('#caoTable');
                    
                    cao_table.delegate('.policy-reject', 'click', function (event) {
                        var element = $(this);
                        var cao_id = element.find('.bCaoID').val();
                        
                        cao_table.trigger('insurance-policy-reject', [cao_id, function () {
                            console.log('finished');
                            
                            // close modal window
                            var modal = PICS.getClass('modal.Modal');
                            modal.hide();
                        }]);
                    });
                }
            }
        }
    });
})(jQuery);