angular.module('PICS.services')

.factory('addCompanyService', function ($resource) {
    return $resource('/NewContractorSearch!add.action?contractor=:id');
});