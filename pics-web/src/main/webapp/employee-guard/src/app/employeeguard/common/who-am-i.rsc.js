angular.module('PICS.employeeguard')

.factory('WhoAmI', function($resource) {
    return $resource('/employee-guard/who-am-i');
});