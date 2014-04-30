angular.module('PICS.employeeguard')

.controller('operatorDashboardCtrl', function ($scope, SiteList, SiteAssignments, ProjectAssignments, WhoAmI) {
    $scope.siteList = SiteList.query(function(sites) {
        if ($scope.hasSites(sites)) {
            $scope.loadSelectedSiteData(sites[0].id);
        } else {
            $scope.loadSelectedSiteData();
        }
    });

    WhoAmI.get(function(user) {
        $scope.user = user.type.toLowerCase();
    });

    $scope.hasSites = function(sites) {
        return sites.length > 0;
    };

    $scope.loadSelectedSiteData = function(site_id) {
        if (site_id !== 'null') {
            $scope.selected_site = site_id;
            $scope.site_assignments = SiteAssignments.get({id: site_id}, function(site_details){
                $scope.chartData = [
                    site_details.completed + site_details.pending,
                    site_details.expiring,
                    site_details.expired
                ];
            });
            $scope.project_assignments = ProjectAssignments.query({id: site_id});
        }
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