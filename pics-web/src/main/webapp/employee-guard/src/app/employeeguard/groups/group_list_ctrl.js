angular.module('PICS.groups')

.controller('groupListCtrl', function ($scope, GroupListResource, WhoAmI) {
    WhoAmI.get(function(user) {
        $scope.userType = user.type.toLowerCase();
    });

    $scope.groups = GroupListResource.query();
});