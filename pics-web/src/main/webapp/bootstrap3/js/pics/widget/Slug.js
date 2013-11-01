(function ($) {
    PICS.define('widget.Slug', {
        methods: (function () {
            function create(config) {
                this.$page = config.$page;
                this.source_field_selector = config.source_field_selector;
                this.target_field_selector = config.target_field_selector;

                return this;
            }

            function bindEvents() {
                this.$page.on('blur', source_field_selector, onNameBlur);
                this.$page.on('change', target_field_selector, onSlugChange);
            }

            function onNameBlur(event) {
                var name = $(event.target).val();

                suggestSlugFromName(name);
            }

            function onSlugChange(event) {
                var slug_field = $(event.target),
                    slug = $(event.target).val();

                // prevent duplicate in case a previous change generated an error
                removeSlugError();

                validateSlug({
                    slug: slug,
                    valid_callback: removeSlugError,
                    invalid_callback: showInvalidSlugError
                });
            }

            function get$slugField() {
                if (!get$slugField.cache) {
                    get$slugField.cache = $(target_field_selector);
                }

                return get$slugField.cache;
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
                    url: page + '!generateSlug.action',
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
                    get$slugField().val(slug);
                }
            }

            // TODO: Replace validateSlug with whole-form validation with field errors response object (see employee-guard.FormValidation)
            function validateSlug(args) {
                var slug = args.slug,
                    valid_callback = args.valid_callback || function(){},
                    invalid_callback = args.invalid_callback || function(){};

                $.ajax({
                    url: page + '!validateSlug.action',
                    data: {
                        slug: slug
                    },
                    success: function (data) {
                        (data.isURI && data.isUnique) ? valid_callback() : invalid_callback()
                    }
                });
            }

            function removeSlugError() {
                var $error_el = get$slugField().next('.error');

                $error_el.remove();
            }

            function showInvalidSlugError() {
                var slug_field = get$slugField();

                $error_el = $('<span class="error">' + 'Invalid slug' + '</span>'),

                slug_field.after($error_el);
                slug_field.focus();
            }

            return {
                create: create,
                bindEvents: bindEvents
            }
        }())
    });
}(jQuery));