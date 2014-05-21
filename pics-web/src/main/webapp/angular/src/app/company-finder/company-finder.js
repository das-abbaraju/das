angular.module('PICS.companyFinder', [
    'ngRoute',
    'ngResource'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/company-finder.action', {
            templateUrl: '/angular/src/app/company-finder/company-finder.tpl.html'
        });
});