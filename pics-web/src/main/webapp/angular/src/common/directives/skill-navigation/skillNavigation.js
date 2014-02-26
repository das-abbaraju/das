angular.module('PICS.directives')

.directive('skillnavigationmenu', function () {
    return {
        restrict: 'E',
        scope: {
            employee: '='
        },
        link: function(scope) {
            if (!scope.$parent.updatePartial) {
                throw 'No updatePartial method defined on parent controlller';
            }
            scope.onMenuItemClick = scope.$parent.updatePartial;
        },
        templateUrl: '/src/common/directives/skill-navigation/_skill-navigation.tpl.html'
    };
});