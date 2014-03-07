angular.module('PICS.employeeguard')

.controller('operatorDashboardCtrl', function ($rootScope, $scope, SiteResource, SiteList, SiteDetails) {
    $scope.dataSrc = SiteResource.get().$promise;

    $scope.siteList = SiteList.query();

    $scope.siteList.$promise.then(checkForCorporateSites);

    function checkForCorporateSites(data) {
        if (data.length > 0) {
            requestCorporateSiteData(data);
        } else {
            requestOperatorSiteData();
        }
    }

    function requestCorporateSiteData(data) {
        $scope.site = SiteDetails.get({id: data[0].id});
        $scope.operator_site = data[0].id;
        $scope.dataSrc = SiteDetails.get({id: data[0].id}).$promise;
    }

    function requestOperatorSiteData() {
        $scope.site = SiteResource.get();
        $scope.dataSrc = SiteResource.get().$promise;
    }


    $scope.getSiteInfo = function () {
        $scope.site = SiteDetails.get({id: $scope.operator_site});
        $scope.dataSrc = SiteDetails.get({id: $scope.operator_site}).$promise;

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