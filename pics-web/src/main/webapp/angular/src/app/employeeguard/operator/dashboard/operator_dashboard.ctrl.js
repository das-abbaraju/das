angular.module('PICS.employeeguard')

.controller('operatorDashboardCtrl', function ($scope, SiteResource, SiteList, SiteDetails) {
    $scope.siteList = SiteList.query(function(site_list) {
        if ($scope.isCorporateSiteList(site_list)) {
            $scope.site_list_select = site_list[0].id;
            $scope.updateSelectedSite();
        } else {
            $scope.selected_site_details = requestSiteDetailsForSiteOperator().then(function(site_details) {
               $scope.selected_site_details = site_details;
            });
        }
    });

    $scope.isCorporateSiteList = function(site_list) {
        return site_list.length > 0;
    };

    $scope.requestSiteDetailsById = function(site_id) {
        return SiteDetails.get({id: site_id}).$promise;
    };

    function requestSiteDetailsForSiteOperator() {
        return SiteResource.get().$promise;
    }

    $scope.updateSelectedSite = function() {
        $scope.requestSiteDetailsById($scope.site_list_select).then(function(site_details) {
            $scope.selected_site_details = site_details;
        });
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