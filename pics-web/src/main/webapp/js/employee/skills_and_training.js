(function ($) {
    PICS.define('employee.SkillsAndTraining', {
        methods: {
            init: function () {
                var element = $('.EmployeeSkillsTraining-page');
                if (element.length) {
                    var that = this;
                    element.delegate('.remove', 'click', that.confirmDelete);
                }
            },

            confirmDelete: function (event) {
                return confirm(translate('JS.ConfirmDeletion'));
            }
        }
    });
})(jQuery);