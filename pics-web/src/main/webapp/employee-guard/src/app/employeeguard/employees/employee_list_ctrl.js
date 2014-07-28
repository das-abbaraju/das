angular.module('PICS.employees')

.controller('employeeListCtrl', function ($scope, EmployeeListResource, WhoAmI) {
    WhoAmI.get(function(user) {
        $scope.userType = user.type.toLowerCase();
    });

    $scope.employees = EmployeeListResource.query();
});