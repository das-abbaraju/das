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

    Model.prototype.getProjects = function () {
        return this.getData().projects;
    };

    Model.prototype.getSiteBySlug = function (site_slug) {
        var data = this.getData(),
        sites = $filter('filter')(data.sites, { slug: site_slug})[0];

        if (sites) {
            return sites;
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
            skills = [];

        angular.forEach(sites, function(site) {
            site.skills = site.required ? site.required.skills : [];

            angular.forEach(site.projects, function(project) {
                site.skills = site.skills.concat(project.skills);
                site.skills = $filter('removeDuplicateItemsFromArray')(site.skills);
            });
        });

        return sites;
    };

    Model.prototype.getSiteSkillsBySlug = function (site_slug) {
        var data = this.getData();

        if (data.sites) {
            sites = $filter('filter')(data.sites, { slug: site_slug})[0];

            if (sites && sites.required) {
                return sites.required;
            }
        }
    };

    Model.prototype.getProjectBySlug = function (project_slug) {
        var data = this.getData(),
            selected_project,
            filteredSkills;

        angular.forEach(data.sites, function(site) {
            angular.forEach(site.projects, function(project) {
                if (project.slug === project_slug) {
                    selected_project = project;
                    if (site.required) {
                        selected_project.skills = site.required.skills.concat(project.skills);
                    }
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