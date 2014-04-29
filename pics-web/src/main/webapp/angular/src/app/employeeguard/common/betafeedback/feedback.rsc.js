angular.module('PICS.employeeguard')

.factory('Feedback', function($resource) {
    return $resource('/employee-guard/beta/feedback');
});