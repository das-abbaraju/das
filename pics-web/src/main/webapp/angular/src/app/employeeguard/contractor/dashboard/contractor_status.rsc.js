angular.module('PICS.employeeguard')

.factory('ContractorStatus', function($resource, $routeParams) {
    return $resource('/employee-guard/contractor/summary');
});