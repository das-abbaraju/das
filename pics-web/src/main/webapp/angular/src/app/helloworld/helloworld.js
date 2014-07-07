angular.module('helloworld',  [
    'ngRoute'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/helloworld.action', {
            templateUrl: '/angular/src/app/helloworld/helloworld/helloworld.tpl.html'
        });
});