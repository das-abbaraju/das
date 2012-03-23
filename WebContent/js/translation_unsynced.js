(function ($) {
    PICS.define('translation.Unsynced', {
        methods: {
            init: function () {
                $('#UnsyncedTranslations-page').delegate('.master', 'click', this.selectAllCheckboxes);
                $('#UnsyncedTranslations-page').delegate('.submit', 'click', this.includeSearchFilters);
            },
            
            selectAllCheckboxes: function(event) {
                var checked = $(this).is(':checked');
                $('#UnsyncedTranslations-page .selectable').attr('checked', checked);
            },
            
            includeSearchFilters: function(event) {
                event.preventDefault();
                console.log($(this).attr("name"));
            }
        }
    });
})(jQuery);