(function(window, $) {
    function toSlug(strict) {
        var slug = this;

        slug = slug
            // Make all letters lower-case
            .toLowerCase()
            // Replace 1 or more hyphens with an empty string
            .replace(/-+/g, '')
            // Replace 1 or more spaces with a single hyphen
            .replace(/\s+/g, '-');

        if (strict == 'strict') {
            // Remove any character that is not alphanumeric, a hyphen, or an underscore
            slug = slug.replace(/[^A-Za-z0-9-_]+/g,'');
        }

        // Remove leading or trailing spaces or hyphens
        slug = slug.replace(/^[-\s]|[-\s]$/g,'')

        // Replace 1 or more hyphens with a single hyphen (sometimes results from above operations)
        slug = slug.replace(/-{2,}/g, '-')

        if (isValidUri(slug)) {
            return slug;
        }

        return false;
    }

    function isValidUri(slug) {
        var regex_uri = /^((?:[a-z0-9.-]|%[0-9A-F]{2}){3,})(?::(\d+))?((?:\/(?:[a-z0-9-._~!$&'()*+,;=:@]|%[0-9A-F]{2})*)*)(?:\?((?:[a-z0-9-._~!$&'()*+,;=:\/?@]|%[0-9A-F]{2})*))?(?:#((?:[a-z0-9-._~!$&'()*+,;=:\/?@]|%[0-9A-F]{2})*))?$/i;

        return regex_uri.test(slug);
    }

    $.extend(window.String.prototype, {
        toSlug: toSlug
    });
}(window, jQuery));