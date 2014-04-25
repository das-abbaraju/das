angular.module('PICS.employeeguard')

.factory('EmployeeCompanyInfo', function($resource) {
    return $resource('/employee-guard/operators/contractors/employees/:id');
    // return $resource('/angular/json/operator/employee_skills/employee_info.json');
});