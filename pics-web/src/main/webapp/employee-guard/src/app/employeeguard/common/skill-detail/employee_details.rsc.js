angular.module('PICS.employeeguard')

.factory('EmployeeDetail', function($resource) {
    return $resource('/employee-guard/json/eg-common/skill-detail/employee-detail.json');
});