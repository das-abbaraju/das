angular.module('PICS.employees')

.factory('EmployeeListResource', function($resource) {
    return $resource('/employee-guard/src/app/employeeguard/employees/json/employee_list.json');
});