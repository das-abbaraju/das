(function ($) {
    PICS.define('widget.Slugifier', {
        methods: (function () {
            var $page,
                $source_input,
                $target_field,
                action;

            function configure(config) {
                $page = config.$page;
                $source_input = config.$source_input;
                $target_input = config.$target_input;
                action = config.action;

                $source_input.addClass('slugifier-source');
                $target_input.addClass('slugifier-target');

                bindEvents();
            }

            function bindEvents() {
                $page.on('change', '.slugifier-source', onNameChange);
                $page.on('change', '.slugifier-target', onSlugChange);
            }

            function onNameChange(event) {
                var name = $(event.target).val(),
                    slug = $target_input.val();

                // Don't overwrite existing slug values
                if (!slug) {
                    suggestSlugFromName(name);
                }
            }

            function onSlugChange(event) {
                var slug_input = $(event.target),
                    slug = $(event.target).val();

                // Prevent duplicate in case a previous change generated an error
                removeSlugError();

                validateSlug({
                    slug: slug,
                    valid_callback: removeSlugError,
                    invalid_callback: showInvalidSlugError
                });
            }

            function suggestSlugFromName(name) {
                requestSlug(name, showSlugSuggestion);
            }

            function requestSlug(str, callback) {
                var callback = callback || function() {};

                if (!str) {
                    return;
                }

                $.ajax({
                    url: action + '!generateSlug.action',
                    data: {
                        stringToSlugify: str
                    },
                    success: function (data) {
                        callback(data.slug);
                    }
                });
            }

            function showSlugSuggestion(slug) {
                if (slug) {
                    $target_input.val(slug);
                }
            }

            // TODO: Replace validation with validation similar to employee-guard.FormValidation
            function validateSlug(args) {
                var slug = args.slug,
                    valid_callback = args.valid_callback || function(){},
                    invalid_callback = args.invalid_callback || function(){};

                if (!slug) {
                    return;
                }

                $.ajax({
                    url: action + '!validateSlug.action',
                    data: {
                        slug: slug
                    },
                    success: function (data) {
                        (data.isURI && data.isUnique) ? valid_callback() : invalid_callback()
                    }
                });
            }

            function removeSlugError() {
                var $error_el = $target_input.next('.error');

                $error_el.remove();
            }

            function showInvalidSlugError() {
                var slug_input = $target_input;

                $error_el = $('<span class="error">' + 'Invalid slug' + '</span>'),

                slug_input.after($error_el);
            }

            return {
                configure: configure
            }
        }())
    });
}(jQuery));