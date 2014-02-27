angular.module('PICS.employeeguard')

.factory('EmployeeSkills', function($resource) {
    return $resource('/angular/json/dummyData.json');
});