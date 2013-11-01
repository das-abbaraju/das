(function ($) {
    PICS.define('audit.ManageQuestion', {
        methods: (function () {
            function init() {
                var $page = $('.ManageQuestion-page');

                if ($page.length) {
                    PICS.getClass('widget.Slugifier').configure({
                        $page: $page,
                        $source_input: $('#save_question_title'),
                        $target_input: $('#save_question_slug'),
                        action: 'ManageQuestion'
                    });
                }
            }

            return {
                init: init
            };
        }())
    });
}(jQuery));