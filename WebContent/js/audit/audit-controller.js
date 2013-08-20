(function ($) {
    PICS.define('audit.AuditController', {
        methods: {
            init: function () {
                if ($('#Audit__page').length) {
                    $('#audit-layout').on('click', '.toggle-excess-criteria', $.proxy(this.toggleExcessCriteria, this));
                }

                if ($('.cao-table').length) {
                    $('body').on('click', '.cao-table .policy-reject', $.proxy(this.rejectPolicy, this));
                    $('body').on('click', '.cao-table .button:not(.policy-reject)', $.proxy(this.refreshCaoTable, this));
                }
            },

            createSelect2: function () {
                $('input[data-toggle="select2"]').each(function (key, value) {
                    var $element = $(this),
                        option_group_id = $element.attr('data-option-group-id'),
                        options_url = 'OptionGroupTagit!getItemsInJson.action?optionGroupId=' + option_group_id;

                    PICS.ajax({
                        url: options_url,
                        dataType: 'json',
                        success: function(data, textStatus, jqXHR) {
                            $element.select2({
                                multiple: true,
                                data: data,
                                width: '400px'
                            });
                        }
                    });
                });
            },

            auditNeedsReload: function (needs_reload) {
                if (needs_reload) {
                    this.audit_needs_reload = needs_reload;
                } else {
                    return this.audit_needs_reload || $.error('Reload has not be defined.');
                }
            },

            getAuditParameters: function () {
                return this.audit_parameters || $.error('No audit parameters have been defined.');
            },

            setAuditParameters: function (audit_parameters) {
                this.audit_parameters = audit_parameters;
            },

            refreshCaoTable: function (event) {
                var $element = $(event.target),
                    that = this;

                this.setAuditParameters({
                    auditID: $('#auditID').val(),
                    caoID: $element.find('.bCaoID').val(),
                    status: $element.find('.bStatus').val()
                });

                this.auditNeedsReload($element.attr('data-attr-audit-reload'));

                $('#caoTable').block({
                    message: 'Loading...'
                });

                //call loadResults defined in conHeader.jsp
                loadResults(this.getAuditParameters(), '', this.auditNeedsReload());
            },

            refreshAudit: function () {
                window.location.reload(true);
            },

            rejectPolicy: function (event) {
                var $cao_table = $('#caoTable'),
                    $element = $(event.target),
                    audit_id = $('#auditID').val(),
                    cao_id = $element.find('.bCaoID').val(),
                    status = $element.find('.bStatus').val();

                $element.trigger('reject', [cao_id, function () {

                    $cao_table.trigger('refresh', [audit_id, cao_id, status]);

                    // close modal window
                    var modal = PICS.getClass('widget.Modal');
                    modal.hide();
                }]);
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