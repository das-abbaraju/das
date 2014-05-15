angular.module('PICS.employeeguard')

.factory('EmployeeSkillModel', function ($filter) {
    var Model = function (data) {
        this.data = Model.generateURLSlug(data);
    };

    Model.generateURLSlug = function (data) {
        angular.forEach(data.sites, function(site) {
            site.slug = $filter('removeInvalidCharactersFromUrl')(site.name);

            angular.forEach(site.projects, function(project) {
                project.slug = $filter('removeInvalidCharactersFromUrl')(project.name);
            });
        });

        return data;
    };

    Model.prototype.getData = function () {
        return this.data;
    };

    Model.prototype.getSites = function () {
        return this.getData().sites;
    };

    Model.prototype.getSiteBySlug = function (site_slug) {
        var data = this.getData();

        if (data.sites) {
            sites = $filter('filter')(data.sites, { slug: site_slug})[0];
            if (sites) {
                return sites;
            }
        }
    };

    Model.prototype.getSiteNameBySlug = function (site_slug) {
        var data = this.getData(),
        sites = $filter('filter')(data.sites, { slug: site_slug})[0];

        if (sites) {
            return sites.name;
        }
    };

    Model.prototype.getAllSiteAndProjectSkills = function () {
        var sites = this.getSites(),
            allSkills = [];

        angular.forEach(sites, function(site) {
            allSkills = site.skills ? site.skills : [];

            angular.forEach(site.projects, function(project) {
                allSkills = allSkills.concat(project.skills);
                site.skills = $filter('removeDuplicateItemsFromArray')(allSkills);
            });
        });

        return sites;
    };

    Model.prototype.getAllSiteAndProjectSkillsBySlug = function (site_slug) {
        var site = this.getSiteBySlug(site_slug),
            projectSkills = [],
            allSkills,
            filteredSkills;

        angular.forEach(site.projects, function(project) {
            projectSkills = projectSkills.concat(project.skills);
        });

        allSkills = site.skills ? site.skills.concat(projectSkills) : projectSkills;

        filteredSkills = $filter('removeDuplicateItemsFromArray')(allSkills);

        site.skills = filteredSkills;

        return site;
    };

    Model.prototype.getProjectBySlug = function (project_slug) {
        var data = this.getData(),
            selected_project,
            filteredSkills;

        angular.forEach(data.sites, function(site) {
            angular.forEach(site.projects, function(project) {
                if (project.slug === project_slug) {
                    selected_project = project;
                }
            });
        });

        if (selected_project) {
            filteredSkills = $filter('removeDuplicateItemsFromArray')(selected_project.skills);

            selected_project.skills = filteredSkills;

            return selected_project;
        }

    };

    Model.prototype.getSiteNameByProjectSlug = function (project_slug) {
        var data = this.getData(),
            site_name;

        angular.forEach(data.sites, function(site) {
            angular.forEach(site.projects, function(project) {
                if (project.slug === project_slug) {
                    site_name = site.name;
                }
            });
        });

        return site_name;
    };

    Model.prototype.getProjectNameBySlug = function (project_slug) {
        var data = this.getData(),
            project_name;

        angular.forEach(data.sites, function(site) {
            angular.forEach(site.projects, function(project) {
                if (project.slug === project_slug) {
                    project_name = project.name;
                }
            });
        });

        return project_name;
    };

    return Model;
});