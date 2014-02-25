angular.module('PICS.directives')

.directive('pagetitle', function () {
    return {
        restrict: 'E',
        scope: {
            title: '@title',
            subtitle: '@subtitle'
        },
        templateUrl: '/src/common/directives/page-title/_page-title.tpl.html'
    };
});