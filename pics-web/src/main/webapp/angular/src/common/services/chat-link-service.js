angular.module('PICS.services')

.factory('chatLinkService', function ($resource) {
    return $resource('/mibew-base-url/:languageId.action');
});