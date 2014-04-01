angular.module('PICS.employeeguard')

.controller('operatorAssignmentsCtrl', function ($scope, SiteList, SiteAssignments, ProjectAssignments) {
    $scope.siteList = SiteList.query(function(sites) {
        if ($scope.hasSites(sites)) {
            $scope.loadSelectedSiteData(sites[0].id);
        }
    });

    $scope.hasSites = function(sites) {
        return sites.length > 0;
    };

    $scope.loadSelectedSiteData = function(site_id) {
        $scope.selected_site = site_id;
        $scope.site_assignments = SiteAssignments.get({id: site_id});
        $scope.project_assignments = ProjectAssignments.query({id: site_id});
    };

    $scope.updateSelectedSite = function() {
        $scope.loadSelectedSiteData($scope.selected_site);
    };
});