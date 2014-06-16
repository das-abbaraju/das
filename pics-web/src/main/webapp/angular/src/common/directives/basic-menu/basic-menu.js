angular.module('PICS.directives')

.directive('basicMenu', function (phoneNumberService, userService) {
    return {
        restrict: 'E',
        replace: true,
        templateUrl: '/angular/src/common/directives/basic-menu/basic-menu.tpl.html',
        scope: {
            countryId: '=',
            languageId: '='
        },
        link: function (scope) {
            scope.$watch('countryId', function () {
                phoneNumberService.get({countryId:scope.countryId}, function (data) {
                    scope.phoneNumber = data.name;
                    scope.phoneCountry = data.id;
                });    
            });
        }
    };
});