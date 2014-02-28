angular.module('PICS.employeeguard')

.factory('EmployeeSkills', function($resource) {
    // return $resource('/angular/json/dummyData.json');
    return $resource('/angular/json/newdata.json');
    // return $resource('/employee-guard/operators/skills/employees/29');
});