(function ($) {
    PICS.define('csr-assignment.CSRAssignmentsController', {
        methods:{
            init:function() {
                var that = this;
                
                $('#accept_all').on('change', function (event) {
                    that.toggleRecommendations.call(that, event);
                });
                
                $('#apply_assignments').on('click', function (event) {
                    that.applyRecommendedCSR();
                });

                $('.accept-recommended').on('change', function (event) {
                    that.updateSelectedRecords();
                });
            },
            
            applyRecommendedCSR: function () {
                var $accept_recommended = $('.accept-recommended'),
                    accepted_contractor_id = [],
                    contractor_ids = '';

                function getAcceptedValues() {
                    $accept_recommended.each(function () {
                        if ($(this).is(":checked")) {
                            accepted_contractor_id.push(this.value);
                        }
                    });
                }

                getAcceptedValues();

                contractor_ids = accepted_contractor_id.join();

                //update form value and submit form
                $('#acceptedCSR').val(contractor_ids);
                $('#save_approved_csr').submit();

            },

            toggleRecommendations: function (event) {
                var $element = $(event.target),
                    $accept_recommended = $('.accept-recommended'),
                    that = this;
                
                if ($element.is(":checked")) {
                    $accept_recommended.prop('checked', true);
                } else {
                    $accept_recommended.prop('checked', false);
                }

                that.updateSelectedRecords();
            },

            updateSelectedRecords: function () {
                var $accept_recommended = $('.accept-recommended'),
                    record_count = 0,
                    $selected_records = $('#selected_records');

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