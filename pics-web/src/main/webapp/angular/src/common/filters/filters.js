angular.module('PICS.filters', [])

.filter('removeInvalidCharactersFromUrl', function () {
    return function (text) {

        var str = text.replace(/\s+/g, '-').toLowerCase();
        return str;
    };
});