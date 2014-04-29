angular.module('PICS.employeeguard')

.controller('operatorDashboardCtrl', function ($scope, $routeParams, SiteList, SiteAssignments, ProjectAssignments, WhoAmI) {

    WhoAmI.get(function(user) {
        $scope.user = user.type.toLowerCase();

        if ($scope.user === 'corporate') {
            SiteList.query(function(sites) {
                $scope.siteList = sites;

                //load first site if none specified in url. Remove when no longer an issue
                if (!$routeParams.siteId) {
                    $scope.loadSelectedSiteData(sites[0].id);
                } else {
                    $scope.loadSelectedSiteData($routeParams.siteId);
                }
            });
        } else {
            $scope.loadSelectedSiteData();
        }
    });

    $scope.loadSelectedSiteData = function(site_id) {
        $scope.selected_site = site_id;
        $scope.site_assignments = SiteAssignments.get({id: site_id}, function(site_details){
            $scope.chartData = [
                site_details.completed + site_details.pending,
                site_details.expiring,
                site_details.expired
            ];
        });
        $scope.project_assignments = ProjectAssignments.query({id: site_id});
    };

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