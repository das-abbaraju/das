angular.module('PICS.directives')

.directive('toggleSwitch', function ($timeout) {
    return {
        restrict: 'E',
        replace: true,
        templateUrl: '/angular/src/common/directives/toggle-switch/toggle-switch.tpl.html',
        scope: {
            ngModel: '=',
            yesLabel: '@',
            noLabel: '@'
        },
        link: function (scope, element) {
            scope.isChecked = scope.ngModel;

            scope.showHint = false;

            scope.label = scope.isChecked ? scope.yesLabel : scope.noLabel;

            scope.toggle = function ($event) {
                scope.isChecked = !scope.isChecked;

                scope.showHint = true;

                $timeout(function () {
                    scope.showHint = false;
                }, 1500);

                scope.label = scope.isChecked ? scope.yesLabel : scope.noLabel;
            };
        }
    };
});