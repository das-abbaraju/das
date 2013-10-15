(function(window, $) {
    function toSlug() {
        slug = this
            // Make all letters lower-case
            .toLowerCase()
            // Replace 1 or more hyphens with a single space
            .replace(/-+/g, ' ')
            // Replace 1 or more spaces with a single hyphen
            .replace(/\s+/g, '-');

        if (isValidSlug(slug)) {
            return slug;
        }

        return false;
    }

    function isValidSlug(slug) {
        var regex_uri = /^((?:[a-z0-9.-]|%[0-9A-F]{2}){3,})(?::(\d+))?((?:\/(?:[a-z0-9-._~!$&'()*+,;=:@]|%[0-9A-F]{2})*)*)(?:\?((?:[a-z0-9-._~!$&'()*+,;=:\/?@]|%[0-9A-F]{2})*))?(?:#((?:[a-z0-9-._~!$&'()*+,;=:\/?@]|%[0-9A-F]{2})*))?$/i;

        return regex_uri.test(slug);
    }

    $.extend(window.String.prototype, {
        toSlug: toSlug
    });
}(window, jQuery));