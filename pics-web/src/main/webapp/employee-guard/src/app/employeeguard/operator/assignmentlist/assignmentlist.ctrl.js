angular.module('PICS.employeeguard')

.controller('operatorAssignmentsCtrl', function ($scope, $routeParams, SiteList, SiteAssignments, ProjectAssignments, WhoAmI) {
    WhoAmI.get(function (user) {
        $scope.userType = user.type.toLowerCase();
        if ($scope.userType === 'corporate') {
            SiteList.query(function (sites) {
                var site_id = $routeParams.siteId || sites[0].id;

                $scope.siteList = sites;
                $scope.loadAssignmentsBySiteId(site_id);
            });
        } else {
            $scope.loadAssignments();
        }
    });

    $scope.loadAssignmentsBySiteId = function(site_id) {
        $scope.selected_site = site_id;
        $scope.site_assignments = SiteAssignments.get({id: site_id});
        $scope.project_assignments = ProjectAssignments.query({id: site_id});
    };

    $scope.loadAssignments = function() {
        $scope.site_assignments = SiteAssignments.get();
        $scope.project_assignments = ProjectAssignments.query();
    };
});