angular.module('PICS.employeeguard')

.factory('EmployeeCompanyInfo', function($resource) {
    return $resource('/employee-guard/operators/contractors/employees/:id');
});