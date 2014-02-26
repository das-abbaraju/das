angular.module('PICS.employeeguard')

.factory('EmployeeSkills', function($resource) {
    return $resource('json/dummyData.json');
});