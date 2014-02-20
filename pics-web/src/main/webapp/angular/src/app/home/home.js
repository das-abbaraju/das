angular.module('MyApp.home', [
    'ngRoute'
])

.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: '/src/app/home/home.tpl.html'
        });
})

.controller('homeCtrl', function ($scope) {
    $scope.title = 'This is a title';
    $scope.content = 'This is content';
})

.directive('test', function () {
    return {
        restrict: 'E',
        template: '<div>This is a custom directive</div>'
    };
});