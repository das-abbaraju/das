<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<div id="primary_navigation" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            
            <div class="nav-collapse collapse">
                <a class="brand" href="/"></a>
                <ul class="nav">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Company</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Users</a></li>
                            <li><a href="#">Resources</a></li>
                            <li><a href="#">Company Profile</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Reports</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Reports Manager</a></li>
                            <li><a href="#">Legacy Reports</a></li>
                            <li><a href="#">(Favorites)</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Manage</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Search for New</a></li>
                            <li><a href="#">Search by Question</a></li>
                            <li><a href="#">CSR Assignments</a></li>
                            <li><a href="#">QuickBooks Sync</a></li>
                            <li><a href="#">QuickBooks Sync Edit</a></li>
                            <li class="dropdown-submenu">
                                <a href="#">Email</a>
                                
                                <ul class="dropdown-menu">
                                    <li><a href="#">Email Wizard</a></li>
                                    <li><a href="#">Email Template Editor</a></li>
                                    <li><a href="#">Email Webinar</a></li>
                                    <li><a href="#">Email Subscriptions</a></li>
                                    <li><a href="#">Email Exclusion List</a></li>
                                    <li><a href="#">Email Queue</a></li>
                                    <li><a href="#">Email Error Report</a></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Configure</a>
                        
                        <ul class="dropdown-menu">
                            <li class="dropdown-submenu">
                                <a href="#">Translations</a>
                                
                                <ul class="dropdown-menu">
                                    <li><a href="#">Manager</a></li>
                                    <li><a href="#">Import/Export</a></li>
                                    <li><a href="#">Batch Insert</a></li>
                                    <li><a href="#">View Traced</a></li>
                                    <li><a href="#">Update Expired</a></li>
                                    <li><a href="#">Unsynced</a></li>
                                </ul>
                            </li>
                            <li class="dropdown-submenu">
                                <a href="#">Audits</a>
                                
                                <ul class="dropdown-menu">
                                    <li><a href="#">Calendar</a></li>
                                    <li><a href="#">Schedule & Assign</a></li>
                                    <li><a href="#">Workflow Manager</a></li>
                                    <li><a href="#">Options Manager</a></li>
                                    <li><a href="#">Definitions</a></li>
                                    <li><a href="#">Type Rules</a></li>
                                    <li><a href="#">Category Rules</a></li>
                                    <li><a href="#">Category Matrix</a></li>
                                </ul>
                            </li>
                            <li><a href="#">Contractor Simulator</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dev</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">App Properties</a></li>
                            <li class="dropdown-submenu">
                                <a href="#">Logging</a>
                                
                                <ul class="dropdown-menu">
                                    <li><a href="#">System</a></li>
                                    <li><a href="#">Page</a></li>
                                    <li><a href="#">Exceptions</a></li>
                                </ul>
                            </li>
                            <li class="dropdown-submenu">
                                <a href="#">Crons</a>
                                
                                <ul class="dropdown-menu">
                                    <li><a href="#">Contractor</a></li>
                                    <li><a href="#">Mail</a></li>
                                    <li><a href="#">Subscription</a></li>
                                    <li><a href="#">Audit Schedule Builder</a></li>
                                </ul>
                            </li>
                            <li class="dropdown-submenu">
                                <a href="#">Cache</a>
                                
                                <ul class="dropdown-menu">
                                    <li><a href="#">Clear</a></li>
                                    <li><a href="#">Statistics</a></li>
                                </ul>
                            </li>
                            <li><a href="#">PICS Style Guide</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Support</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Help Center</a></li>
                            <li><a href="#">Live Chat</a></li>
                            <li><a href="#">Contact PICS</a></li>
                            <li><a href="#">About PICS Organizer</a></li>
                            <li class="dropdown-submenu">
                                <a href="#">Reference</a>
                                
                                <ul class="dropdown-menu">
                                    <li><a href="#">Trade Taxonomy</a></li>
                                    <li><a href="#">Navigation Menu</a></li>
                                    <li><a href="#">Dynamic Reporting</a></li>
                                    <li><a href="#">Reports Manager</a></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </ul>
                <ul class="nav pull-right">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Joe Cool <i class="icon-cog icon-large"></i></a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Edit Profile</a></li>
                            <li><a href="#">My Schedule</a></li>
                            <li><a href="#">(Switch Back)</a></li>
                            <li><a href="#">Logout</a></li>
                        </ul>
                    </li>
                </ul>
                <form action="/" class="navbar-search pull-right">
                    <i class="icon-search icon-large"></i>
                    
                    <input type="hidden" name="button" value="search" />
                    <input type="text" name="searchTerm" class="search-query span2" placeholder="Search" />
                </form>
            </div>
        </nav>
    </div>
</div>