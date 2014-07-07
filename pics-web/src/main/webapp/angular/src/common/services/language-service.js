angular.module('PICS.services')

.factory('languageService', function ($resource) {
    return $resource('/languages.action');
});