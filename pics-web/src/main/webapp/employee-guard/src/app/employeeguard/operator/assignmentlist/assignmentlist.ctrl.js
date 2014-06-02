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
         SiteAssignments.get({id: site_id}, function(site_data) {
            $scope.site_assignments = site_data;
            setTotalAssignments(site_data);
        });
        $scope.project_assignments = ProjectAssignments.query({id: site_id});
    };

    $scope.loadAssignments = function() {
        SiteAssignments.get(function(site_data) {
            $scope.site_assignments = site_data;
            setTotalAssignments(site_data);
        });
        $scope.project_assignments = ProjectAssignments.query();
    };

    function setTotalAssignments(site_assignments) {
        $scope.totalAssignments = site_assignments.completed + site_assignments.pending + site_assignments.expiring + site_assignments.expired;
    }

    angular.extend($scope, {
        setTotalAssignments: setTotalAssignments
    });
});