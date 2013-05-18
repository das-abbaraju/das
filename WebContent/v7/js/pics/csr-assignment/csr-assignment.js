(function ($) {
    PICS.define('csr-assignment.CSRAssignmentsController', {
        methods:{
            init:function() {
                var that = this;

                $('#accept_all').on('change', $.proxy(this, "toggleRecommendations"));
                
                $('.apply_selected_assignments').on('click', $.proxy(this, "applyRecommendedCSR"));

                $('.accept-recommended').on('change', this.updateSelectedRecords);
            },
            
            applyRecommendedCSR: function () {
                var $acceptedCSR = $('#acceptedCSR'),
                    accepted_values = this.getAcceptedValues();

                //update form value and submit form
                $acceptedCSR.val(accepted_values.join());
                $('#save_approved_csr').submit();
            },

            getAcceptedValues: function () {
                var $accept_recommended = $('.accept-recommended'),
                    accepted_values = [];

                $accept_recommended.each(function () {
                    if ($(this).is(":checked")) {
                        accepted_values.push(this.value);
                    }
                });

                return accepted_values;
            },

            toggleRecommendations: function (event) {
                var $element = $(event.target),
                    $accept_recommended = $('.accept-recommended')

                if ($element.is(":checked")) {
                    $accept_recommended.prop('checked', true);
                } else {
                    $accept_recommended.prop('checked', false);
                }

                this.updateSelectedRecords();
            },

            updateSelectedRecords: function () {
                var $accept_recommended = $('.accept-recommended'),
                    $selected_records = $('#selected_records'),
                    record_count = 0;

                $accept_recommended.each(function () {
                    if ($(this).is(":checked")) {
                        record_count++;
                    }
                });

                $selected_records.html(record_count);
            }
        }
    });
}(jQuery));