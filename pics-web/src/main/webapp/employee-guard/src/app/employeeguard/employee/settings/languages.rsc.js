angular.module('PICS.employeeguard')

.factory('Language', function($resource) {
    return $resource('/employee-guard/json/employee/settings/languages.json');
});