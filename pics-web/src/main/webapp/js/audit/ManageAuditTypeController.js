(function ($) {
    PICS.define('audit.ManageAuditType', {
        methods: (function () {
            function init() {
                var $page = $('.ManageAuditType-page');

                if ($page.length) {
                    PICS.getClass('widget.Slugifier').configure({
                        $page: $page,
                        $source_input: $('#save_auditType_name'),
                        $target_input: $('#save_auditType_slug'),
                        action: 'ManageAuditType'
                    });
                }
            }

            return {
                init: init
            };
        }())
    });
}(jQuery));