(function ($) {
    PICS.define('audit.AuditController', {
        methods: {
            init: function () {
                if ($('#Audit__page').length) {

                    $('#audit-layout').on('click', '.toggle-excess-criteria', $.proxy(this, 'toggleExcessCriteria'));

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
            },

            toggleExcessCriteria: function (event) {
                var $element = $(event.target),
                    $insurance_criteria = $element.closest('.insurance-criteria'),
                    $excess_criteria = $insurance_criteria.find('.excess-criteria');

                if ($excess_criteria.hasClass('hide')) {
                    $excess_criteria.removeClass('hide');
                    $element.html(translate('JS.Audit.InsuranceLimit.ShowLess'))
                } else {
                    $excess_criteria.addClass('hide');
                    $element.html(translate('JS.Audit.InsuranceLimit.ShowAll'))
                }
            }
        }
    });
})(jQuery);