angular.module('PICS.directives')
.directive('stackedprogressbar', function () {
    return {
        restrict: 'E',
        scope: {
            values: '&'
        },
        link: function(scope) {
            scope.progress = scope.values();
        },
        templateUrl: '/angular/src/common/directives/stacked-progress-bar/_stacked-progress-bar.tpl.html'
    };
});