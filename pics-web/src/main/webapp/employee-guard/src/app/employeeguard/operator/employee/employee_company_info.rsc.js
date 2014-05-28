angular.module('PICS.employeeguard')

.factory('EmployeeCompanyInfo', function($resource) {
    return $resource('/employee-guard/operators/:siteId/contractors/employees/:id');
});