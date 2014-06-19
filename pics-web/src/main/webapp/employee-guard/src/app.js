angular.module('EmployeeGUARD', [
    'PICS.employeeguard',
    'PICS.employeeguard.skills'
])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('noCacheInterceptor');
}]);