angular.module('PICS.employeeguard')

.factory('EmployeeCompanyInfo', function($resource, $routeParams) {
    // return $resource('/employee-guard/operators/skills/employees/' + $routeParams.id);
    return $resource('/angular/json/operator/employee_skills/employee_info.json');
});