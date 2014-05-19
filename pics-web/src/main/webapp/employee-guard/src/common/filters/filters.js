angular.module('PICS.filters', [])

.filter('removeInvalidCharactersFromUrl', function () {
    return function (text) {
        var str = text.replace(/\s+/g, '-');
            str = str.replace(/-{2,}/g, '-');
            str = str.replace(/[\'\",:\/#%&*{}<>?\\\+]+/g, '');

        return str.toLowerCase();
    };
})

.filter('removeDuplicateItemsFromArray', function ($filter) {
    return function (original_array) {
        var new_array = [],
            matches;

        angular.forEach(original_array, function(original_item) {
            matches = $filter('filter')(new_array, original_item, true);

            if (!matches.length) {
                new_array.push(original_item);
            }
        });

        return new_array;
    };
});