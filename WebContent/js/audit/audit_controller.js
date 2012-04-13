(function ($) {
    PICS.define('audit.AuditController', {
        methods: {
            init: function () {
                if ($('#Audit__page').length) {
                    var cao_table = $('#caoTable');
                    
                    cao_table.delegate('.policy-reject', 'click', function (event) {
                        var element = $(this);
                        
                        var audit_id = $('#auditID').val();
                        var cao_id = element.find('.bCaoID').val();
                        var status = element.find('.bStatus').val();
                        
                        element.trigger('reject', [cao_id, function () {
                            cao_table.trigger('refresh', [audit_id, cao_id, status]);
                            
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