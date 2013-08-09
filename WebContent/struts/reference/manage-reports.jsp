<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:url action="ManageReports!search.action" var="reports_manager_url" />

<title>Reports Manager</title>

<div id="main" class="container">
    <header>
        <h1>
            Reports Manager
        </h1>
        <h2>
            PICS Organizer 7.0
        </h2>
    </header>
    
    <div class="content">
        <div class="row menu">
            <div class="span6 image-container">
                <img src="v7/img/reference/manage-report/menu.png" />
            </div>

            <div class="span6">
                <section class="overview">
                    <h1>
                        Full control over all your reports
                    </h1>
                    <p>
                        Now that you have near limitless control of your information with Dynamic Reporting, you should also have that same level of control with your reports. This quick overview will show you how your information is managed and how to share what you've found with others:
                    </p>
                    
                    <dl>
                        <dt>Favorites</dt>
                        <dd>The reports you want to have easy access to</dd>
                        <dt>Owned by Me</dt>
                        <dd>All the reports that you're the owner of</dd>
                        <dt>Shared with Me</dt>
                        <dd>Everything that someone else has shared with you</dd>
                        <dt>Search for Reports</dt>
                        <dd>Look for other reports that may already do what you need</dd>
                        <dt>Report Access</dt>
                        <dd>Manage sharing and permissions to a report</dd>
                    </dl>
                </section>
            </div>
        </div>
        
        <hr />

        <h2>Favorites</h2>
        
        <div class="row favorites">
            <div class="span7 image-container">
                <img src="v7/img/reference/manage-report/favorites.png" />
            </div>
            <div class="span5">
                <section class="overview">
                    <h1>
                        Quick and easy access
                    </h1>
                    <p>
                        It's possible to favorite any report that you see by either selecting the star icon or from the report's Options menu. Your top 10 favorite reports will even show up in your navigation menu.
                    </p>
                    <p>
                        Reports that are favorited are automatically added to the top of your list. However, if you're happy with favorites list order, you can start "pinning" the location of certain reports. This will keep their exact location in your list—so if you want your top three reports to always stay your top three, just pin them in those spots.
                    </p>
                </section>
            </div>
        </div>
        
        <div class="row favorites-pinning">
            <div class="span3 image-container">
                <img src="v7/img/reference/manage-report/pinned.png" />
            </div>
            <div class="span9">
                <section class="overview">
                    <h1>
                        Moving and Pinning
                    </h1>
                    <p>
                        It's easy to rearrange your list of favorite reports by selecting the Options selection on the right of the report's name. From their you can move your report up or down in the list. This is especially helpful if you want to make sure that a report shows up in your navigation bar or not.
                    </p>
                </section>
            </div>
        </div>

        <hr />
        
        <h2>Owned by Me</h2>
        
        <div class="row owned-by">
            <div class="span7 image-container">
                <img src="v7/img/reference/manage-report/owned-by.png" />
            </div>

            <div class="span5">
                <section class="overview">
                    <h1>
                        You have the keys
                    </h1>
                    <p>
                        These reports you've either duplicated yourself and own, or someone else has assigned you as owner of. As an owner of a report, there are a few things that you can do that no one else can (groups cannot be owners of reports, only individuals).
                    </p>
                </section>
            </div>
        </div>
        
        <div class="row owned-by">
            <div class="span10 offset2">
                <ul class="unstyled">
                    <li>
                        <i class="icon-key"></i>
                        
                        <section class="overview">
                            <h1>
                                Sharing, Permissions, and Ownership
                            </h1>
                            <p>
                                Since you're the report's owner you have full permission to share it with anyone you'd like. When you select Share&ellips; from the Options menu you'll be taken to the Report Access section that will give you all your sharing and permission options.
                            </p>
                            <p>
                                You can also assign someone else as the owner of a report. You will automatically be granted edit permissions but will lose the couple of abilities that owners have over everyone else, like deleting a report.
                            </p>
                        </section>
                    </li>
                    <li>
                        <i class="icon-search"></i>
                        
                        <section class="overview">
                            <h1>
                                Show/Hide from Search
                            </h1>
                            <p>
                                If you've come up with an awesome new report that you think may help others then you may want to allow it to be searchable. You'll see a search icon on all the reports you've allowed to be shown in Search for Reports.
                            </p>
                            <p>
                                Note: YOU ARE NOT MAKING YOUR DATA VISIBLE. The way dynamic reporting works is like a template to viewing data. Every user's reports are filled with data relevant and accessible to them. By letting others see your report it's allowing them to view their data in a way that may be helpful to them.
                            </p>
                        </section>
                    </li>
                    <li>
                        <i class="icon-minus-sign"></i>
                        
                        <section class="overview">
                            <h1>
                                Delete
                            </h1>
                            <p>
                                Only owners can delete reports. If an owner deletes a report it will be removed frohm everyone's accounts. If you're the owner and feel like you don't need a certain report—but someone else may—then maybe you should look into transferring ownership and then removing it from your Shared with Me list (we'll get into that right now&ellips;).
                            </p>
                        </section>
                    </li>
                </ul>
            </div>
        </div>
        
        <hr />
        
        <h2>Shared with Me</h2>
        
        <div class="row shared-with">
            <div class="span7 image-container">
                <img src="v7/img/reference/manage-report/shared-with.png" />
            </div>

            <div class="span5">
                <section class="overview">
                    <h1>
                        Work as a team
                    </h1>
                    <p>
                        Reports in this section have been shared with you by someone else, and you'll have either View or Edit permission. View permission allows you to open and use the report, but not make permanent changes to it. Edit permission (which is marked with an edit icon) is like View, but you can save any changes that you make and it will permanently save for all users of the report. Also , with Edit permission, you can share the report and modify user's and groups' permissions to it. 
                    </p>
                </section>
            </div>
        </div>
        
        <div class="row shared-with">
            <div class="span10 offset2">
                <ul class="unstyled">
                    <li>
                        <i class="icon-remove-sign"></i>
                        
                        <section class="overview">
                            <h1>
                                Remove
                            </h1>
                            <p>
                                Any of the reports that show up in your Shared with Me list can be removed. What this will do is remove the report from your list. It does not remove permissions to the report and you can search for it again and open the report to re-add it to your Shared with Me section. This functionality is sole to de-clutter your list if you've been shared something that is not useful or relevant to you.
                            </p>
                        </section>
                    </li>
                </ul>
            </div>
        </div>
        
        <hr />
        
        <h2>Search for Reports</h2>
        
        <div class="row search">
            <div class="span6 image-container">
                <img src="v7/img/reference/manage-report/search.png" />
            </div>

            <div class="span6">
                <section class="overview">
                    <h1>
                        Find exactly what you need
                    </h1>
                    <p>
                        This is where you can look for any report that you have access to. As new reports are created or allowed to be searchable, they'll show here as well. By default you'll see some recommended reports. Once you satrt typing in the search area your results will be live-updated. Once you find what you're looking for then just open the report or instantly add it to your Favorites by selecting the star icon.
                    </p>
                </section>
            </div>
        </div>
        
        <hr />
        
        <h2>Report Access</h2>
        
        <div class="row access">
            <div class="span12 image-container">
                <img src="v7/img/reference/manage-report/access.png" />
            </div>
            
            <div class="span8 offset2">
                <section class="overview">
                    <h1>
                        Full control over who can see and touch
                    </h1>
                    <p>
                        Here’s where you set the permissions and access to a report—either adding or removing individuals and groups. To add to your list, just start typing in the search area. All of your changes update automatically, so once you’re done you can just navigate away from this section.
                    </p>
                </section>
            </div>
        </div>
        
        <div class="row access">
            <div class="span3 image-container">
                <img src="v7/img/reference/manage-report/access-permissions.png" />
            </div>
            
            <div class="span9">
                <section class="overview">
                    <h1>
                        Editing Permissions
                    </h1>
                    <p>
                        The drop down on the right side of the person or group’s box is where you can modify what their permission is. This is also where you would remove their permission to the report as well.
                    </p>
                    <p>
                        Remember, only individuals can be a report’s owner.
                    </p>
                    <p>
                        Groups and individuals with View permissions to a report cannot edit or view the Report Access section.
                    </p>
                </section>
            </div>
        </div>
    </div>

    <footer>
        <div class="row">
            <div class="span4 offset4">
                <a class="btn btn-primary btn-large" href="${reports_manager_url}">Use Reports Manager <i class="icon-circle-arrow-right"></i></a>
            </div>
        </div>
    </footer>
</div>