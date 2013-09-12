(function ($) {
    PICS.define('operator.DefineCompetencies', {
        methods: {
            init: function () {
                var element = $('.DefineCompetencies-page');

                if (element.length) {
                    var that = this;

                    element.delegate('#add_competency_link', 'click', that.loadCompetencyForm);
                    element.delegate('.edit', 'click', that.loadCompetencyForm);
                }
            },

            loadCompetencyForm: function () {
                var competency = $(this).attr('data-competency') || 0;
                var operator = $(this).attr('data-operator');
                var title = $(this).attr('data-title');

                $('#competency_form').html('<img src="images/ajax_process.gif" alt="'
                    + translate('JS.Loading') + '" /> '
                    + translate('JS.Loading'));

                PICS.ajax({
                    url: 'DefineCompetencies!load.action',
                    data: {
                        competency: competency,
                        operator: operator
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        $('#competency_form').html(data);
                    }
                });
            }
        }
    });
})(jQuery);