angular.module('EmployeeGUARD', [
    'PICS.employeeguard'
])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('noCacheInterceptor');
}]);