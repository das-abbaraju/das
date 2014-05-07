angular.module('PICS.employeeguard')

.controller('operatorDashboardCtrl', function ($scope, $routeParams, SiteList, SiteAssignments, ProjectAssignments, WhoAmI) {

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
        SiteAssignments.get({id: site_id}, onLoadAssignmentsSuccess);
        $scope.project_assignments = ProjectAssignments.query({id: site_id});
    };

    $scope.loadAssignments = function() {
        SiteAssignments.get(onLoadAssignmentsSuccess);
        $scope.project_assignments = ProjectAssignments.query();
    };

    function onLoadAssignmentsSuccess(site_details){
        $scope.site_assignments = site_details;

        if (site_details.employees > 0) {
            $scope.chartData = [
                site_details.completed + site_details.pending,
                site_details.expiring,
                site_details.expired
            ];
        }
    }

    $scope.calculateStatusPercentage = function (amount, total) {
        return (amount / total) * 100;
    };

    $scope.getProjectStatus = function (project) {
        var progress_bar = {},
            total = project.completed + project.pending + project.expiring + project.expired;

        progress_bar.success = {
            amount: (project.pending + project.completed),
            width: $scope.calculateStatusPercentage((project.pending + project.completed), total)
        };

        progress_bar.warning = {
            amount: project.expiring,
            width: $scope.calculateStatusPercentage(project.expiring, total)
        };

        progress_bar.danger = {
            amount: project.expired,
            width: $scope.calculateStatusPercentage(project.expired, total)
        };

        return progress_bar;
    };
});