angular.module('PICS.registration', [
    'ngRoute',
    'ngResource'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/registration.action', {
            templateUrl: '/angular/src/app/registration/registration.tpl.html',
            title: 'ContractorRegistration.title',
            resolve: {
                text: function (translationsService) {
                    return translationsService.getTranslations();
                }
            }
        })
        .when('/registration/address-confirmation.action', {
            templateUrl: '/angular/src/app/registration/address-confirmation/address-confirmation.tpl.html'
        })
        .when('/registration/address-editor.action', {
            templateUrl: '/angular/src/app/registration/address-editor/address-editor.tpl.html'
        })
        .otherwise({
            redirectTo: function () {
                // rediect to struts registration page (page 1)
            }
        });
});