angular.module('PICS.employeeguard')

.factory('SkillModel', function ($filter) {
    var Model = function (data) {
        this.data = generateURLSlugs(data);
    };

    function generateURLSlugs(data) {
        angular.forEach(data.projects, function(project) {
            project.slug = $filter('removeInvalidCharactersFromUrl')(project.name);
        });

        angular.forEach(data.roles, function(role) {
            role.slug = $filter('removeInvalidCharactersFromUrl')(role.name);
        });

        return data;
    }

    Model.prototype.getData = function () {
        return this.data;
    };

    Model.prototype.getRoles = function () {
        return this.getData().roles;
    };

    Model.prototype.getProjects = function () {
        return this.getData().projects;
    };

    Model.prototype.getAllRequiredSkills = function () {
        var siteAndCorpSkills,
            projectSkills,
            allRequiredSkills;

        siteAndCorpSkills = this.getSiteAndCorpRequiredSkills();
        projectSkills = this.getAllProjectRequiredSkills();

        if (siteAndCorpSkills && projectSkills) {
            allRequiredSkills = siteAndCorpSkills.concat(projectSkills);
        }

        allRequiredSkills = $filter('removeDuplicateItemsFromArray')(allRequiredSkills);

        return allRequiredSkills;
    };

    Model.prototype.getAllProjectRequiredSkills = function () {
        var data = this.getData(),
            requiredSkills = [];

        angular.forEach(data.projects, function(project) {
            requiredSkills = project.required ? requiredSkills.concat(project.required.skills) : [];
        });

        return requiredSkills;
    };

    Model.prototype.getProjectRequiredSkillsBySlug = function (project_slug) {
        var data = this.getData(),
            projectRequiredSkills,
            projects = $filter('filter')(data.projects, { slug: project_slug})[0];

        projectRequiredSkills = projects.required ? projects.required.skills : [];

        return projectRequiredSkills;
    };

    Model.prototype.getProjectAndSiteRequiredSkillsBySlug = function (slug) {
        var data = this.getData(),
            Model = this,
            projectSkills,
            siteAndCorpSkills;

        //Add site and corp to list
        siteAndCorpSkills = Model.getSiteAndCorpRequiredSkills();
        projectSkills = Model.getProjectRequiredSkillsBySlug(slug);

        return siteAndCorpSkills.concat(projectSkills);
    };

    Model.prototype.getSiteAndCorpRequiredSkills = function () {
        var data = this.getData(),
            siteAndCorpRequiredSkills;

        siteAndCorpRequiredSkills = data.required ? data.required.skills : [];

        return siteAndCorpRequiredSkills;
    };

    Model.prototype.getRoleBySlug = function (role_slug) {
        var data = this.getData(),
            roles = $filter('filter')(data.roles, { slug: role_slug})[0];

        if (!roles) {
            throw "Role with slug '" + role_slug + "' not found";
        }

        return roles;
    };

    Model.prototype.getRoleNameBySlug = function (role_slug) {
        var data = this.getData(),
            roles = $filter('filter')(data.roles, { slug: role_slug})[0];

        if (!roles) {
            throw "Role with slug '" + role_slug + "' not found";
        }

        return roles.name;
    };

    Model.prototype.getProjectBySlug = function (project_slug) {
        var data = this.getData(),
            projects = $filter('filter')(data.projects, { slug: project_slug})[0];

        if (!projects) {
            throw "Project with slug '" + project_slug + "' not found";
        }

        return projects;
    };

    Model.prototype.getProjectNameBySlug = function (project_slug) {
        var data = this.getData(),
            projects = $filter('filter')(data.projects, { slug: project_slug})[0];

        if (!projects) {
            throw "Project with slug '" + project_slug + "' not found";
        }

        return projects.name;
    };

    return Model;
});