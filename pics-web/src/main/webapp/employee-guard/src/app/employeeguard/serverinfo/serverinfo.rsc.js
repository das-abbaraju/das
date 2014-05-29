angular.module('PICS.employeeguard')

.factory('ServerInfo', function($resource) {
    // return $resource('/angular/json/serverinfo/serverinfo.json');
    return $resource('/employee-guard/serverInfo');
});