(function ($) {
    PICS.define('employee.Detail', {
        methods: {
            init: function () {
                var element = $('.EmployeeDetail-page');
                if (element.length) {
                    $('.cluetip').cluetip({
                        closeText: "<img src='images/cross.png' width='16' height='16'>",
                        arrows: true,
                        cluetipClass: 'jtip',
                        local: true,
                        clickThrough: false
                    });

                    element.delegate('#toggle_qualifications', 'click', this.toggleAllQualifications);
                    element.delegate('.toggle-single-qualification', 'click', this.toggleSingleQualification);
                    element.delegate('.view-site-tasks', 'click', this.viewSiteTask);
                }
            },

            toggleAllQualifications: function (event) {
                event.preventDefault();

                if ($('.assessmentResults').is(':visible')) {
                    $('.assessmentResults').hide();
                } else {
                    $('.assessmentResults').show();
                }
            },

            toggleSingleQualification: function (event) {
                event.preventDefault();
                var task = $(this).attr('data-task');
                $('#jt_' + task).toggle();
            },

            viewSiteTask: function (event) {
                event.preventDefault();
                var site = $(this).attr('data-site');
                $('#jst_' + site).toggle('slow');
            }
        }
    });
})(jQuery);