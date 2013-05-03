(function ($) {
    PICS.define('audit.AuditController', {
        methods: {
            init: function () {
                if ($('.Audit-page').length) {
                    var that = this;
                    
                    that.rejectPolicy();
                }
            },

            createMultiSelect: function () {
                $('.multiselect[name="auditData.answer"]').each(function (key, value) {
                    var $element = $(this),
                        option_group_id = $element.attr('data-option-group-id'),
                        options_url = 'OptionGroupTagit!getItemsInJson.action?optionGroupId=' + option_group_id;

                    PICS.ajax({
                        url: options_url,
                        dataType: 'json',
                        success: function(data, textStatus, jqXHR) {
                            $element.select2({
                                multiple: true,
                                data: data
                            });
                        }
                    });
                });
            },

            rejectPolicy: function () {
                var $cao_table = $('#caoTable');

                $cao_table.delegate('.policy-reject', 'click', function (event) {
                    var $element = $(this);

                    var $audit_id = $('#auditID').val();
                    var $cao_id = $element.find('.bCaoID').val();
                    var $status = $element.find('.bStatus').val();

                    $element.trigger('reject', [$cao_id, function () {
                        $cao_table.trigger('refresh', [$audit_id, $cao_id, $status]);

                        // close modal window
                        var modal = PICS.getClass('modal.Modal');
                        modal.hide();
                    }]);
                });
            }
        }
    });
})(jQuery);