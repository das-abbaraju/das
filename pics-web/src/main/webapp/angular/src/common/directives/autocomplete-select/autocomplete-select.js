angular.module('PICS.directives')

.directive('autocompleteSelect', function (tradeService) {
    function getFormat(data) {
        var resultArr = [
            '<div class="item"><p' + data.id + data.name + '">',
                data.name,
            '</p></div>'
        ];

        return resultArr.join('');
    } 

    return {
        restrict: 'A',
        scope: {
            listItems: '=',
            onKeyup: '=',
            select2El: '='
        },
        link: function (scope, element) {
            var inputEl;

            scope.select2El = element.select2({
                query: function (query) {
                    scope.onKeyup(inputEl.val(), query);
                },
                width: '100%',
                escapeMarkup:function (markup) {
                    return markup;
                },
                multiple: true,
                formatResult: getFormat,
                formatSelection: getFormat
            });

            inputEl = element.siblings().filter('.select2-container').find('.select2-input');
        }
    };
});