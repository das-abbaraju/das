angular.module('PICS.services')

.factory('tradeService', function ($resource) {
    return $resource('/TradeAutocomplete!tokenJson.action?extraArgs=true&limit=25&q=:query');
});