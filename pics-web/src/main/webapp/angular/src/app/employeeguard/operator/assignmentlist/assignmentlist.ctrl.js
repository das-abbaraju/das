angular.module('PICS.employeeguard')

.controller('operatorAssignmentsCtrl', function ($scope, SiteList, SiteAssignments, ProjectAssignments) {
    $scope.siteList = SiteList.query(function(sites) {
        if ($scope.hasSites(sites)) {
            $scope.loadSelectedSiteData(sites[0].id);
        } else {
            $scope.site_assignments = SiteAssignments.get();
            $scope.project_assignments = ProjectAssignments.query();
        }
    });

    $scope.hasSites = function(sites) {
        return sites.length > 0;
    };

    $scope.loadSelectedSiteData = function(site_id) {
        $scope.selected_site = site_id;
        $scope.selected_site_name = $scope.getSiteNameFromSiteList(site_id);
        $scope.site_assignments = SiteAssignments.get({id: site_id});
        $scope.project_assignments = ProjectAssignments.query({id: site_id});
    };

    $scope.updateSelectedSite = function() {
        if ($scope.selected_site !== 'null') {
            $scope.loadSelectedSiteData($scope.selected_site);
        }
    };

    $scope.getSiteNameFromSiteList = function (id) {
        for (var i = 0; i < $scope.siteList.length; i++) {
            if ($scope.siteList[i].id == $scope.selected_site) {
                return $scope.siteList[i].name;
            }
        }
    };
});