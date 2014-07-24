angular.module('PICS.roles')

.controller('roleListCtrl', function ($scope, RoleListResource, WhoAmI) {
    WhoAmI.get(function(user) {
        $scope.userType = user.type.toLowerCase();
    });

    $scope.roles = RoleListResource.query();
});