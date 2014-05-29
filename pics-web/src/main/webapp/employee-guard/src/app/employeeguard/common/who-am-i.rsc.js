angular.module('PICS.employeeguard')

.factory('WhoAmI', function($resource) {
    return $resource('/employee-guard/operators/who-am-i');
});