<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<style>
#primary_navigation
{
    z-index: 9999;
}

#primary_navigation .navbar-inner
{
    border: 0;
}

#primary_navigation .nav > li.account > a
{
    color: #f1f1f2;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);
    background-color: #3f93c0;
    background-image: -moz-linear-gradient(top, #73bbe8, #3f93c0);
    background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#73bbe8), to(#3f93c0));
    background-image: -webkit-linear-gradient(top, #73bbe8, #3f93c0);
    background-image: -o-linear-gradient(top, #73bbe8, #3f93c0);
    background-image: linear-gradient(to bottom, #73bbe8, #3f93c0);
    background-repeat: repeat-x;
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff73bbe8', endColorstr='#ff3f93c0', GradientType=0);
    border-color: #3f93c0 #3f93c0 #336699;
    border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
    *background-color: #3f93c0;
    /* Darken IE7 buttons by default so they stand out more given they won't have borders */

    filter: progid:DXImageTransform.Microsoft.gradient(enabled = false);
}

#primary_navigation .nav > li.account > a:active,
#primary_navigation .nav > li.account > a:hover
{
    color: #fff;
}

#contractor_menu
{
    top: 53px;
}

#contractor_menu .navbar-inner
{
    background: #333;
    background-image: -webkit-gradient(linear,50% 0,50% 100%,color-stop(0%,#252525),color-stop(100%,#333));
    background-image: -webkit-linear-gradient(#252525,#333);
    background-image: -moz-linear-gradient(#252525,#333);
    background-image: -o-linear-gradient(#252525,#333);
    background-image: linear-gradient(#252525,#333);
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff252525',endColorstr='#ff333333',GradientType=0);
    border-bottom: 0;
    border-top: 1px solid #020303;
    color: #f1f1f2;
    height: 29px;
    line-height: 29px;
    min-height: 29px;
    padding: 0 10px;
}

#contractor_menu .nav > li > a
{
    border: 0;
    color: #ccc;
    padding: 4px 15px 5px;
    text-shadow: none;
}

#contractor_menu .nav > li > a:active,
#contractor_menu .nav > li > a:hover
{
    color: #f1f1f2;
}

#contractor_menu .nav li.dropdown.open > .dropdown-toggle,
#contractor_menu .nav li.dropdown.active > .dropdown-toggle,
#contractor_menu .nav li.dropdown.open.active > .dropdown-toggle
{
    color: #f1f1f2;
    background: #252525;
    background-image: -webkit-gradient(linear,50% 0,50% 100%,color-stop(0%,#000),color-stop(100%,#252525));
    background-image: -webkit-linear-gradient(#000,#252525);
    background-image: -moz-linear-gradient(#000,#252525);
    background-image: -o-linear-gradient(#000,#252525);
    background-image: linear-gradient(#000,#252525);
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff000000',endColorstr='#ff252525',GradientType=0);
}

#contractor_menu .nav > li.company > a
{
    padding-left: 0;
}

#contractor_menu .nav > li.account > a
{
    
}
</style>

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
                            <li><a href="#">Notes</a></li>
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
                            <li><a href="#">Approve Contractors</a></li>
                            <li><a href="#">Watch List</a></li>
                            <li><a href="#">Contractor Tags</a></li>
                            <li><a href="#">Flag Criteria</a></li>
                            <li><a href="#">Insurance Criteria</a></li>
                            <li><a href="#">Competencies Criteria</a></li>
                            <li class="dropdown-submenu">
                                <a href="#">Email</a>
                                
                                <ul class="dropdown-menu">
                                    <li><a href="#">Email Wizard</a></li>
                                    <li><a href="#">Email Template Editor</a></li>
                                    <li><a href="#">Email Queue</a></li>
                                </ul>
                            </li>
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
                    <li class="dropdown account">
                        <!-- <a href="#" class="dropdown-toggle" data-toggle="dropdown">Joe Cool <i class="icon-cog icon-large"></i></a> -->
                        
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" style="
                            padding: 8px 15px 9px;
                            display: block;
                        "><div style="
                            text-align: right;
                            line-height: 16px;
                            margin-right: 5px; 
                            display: inline-block;
                            display: inline \9;
                        "><span style="
                            font-size: 10px;
                        ">Logged in as</span><br>Ancon Marine</div>
                        
                        <i class="icon-cog icon-large" style="
                            vertical-align: 5px;
                        "></i></a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Edit Profile</a></li>
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

<div id="contractor_menu" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            
            <div class="nav-collapse collapse">
                <ul class="nav">
                    <li class="dropdown company">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Company</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Notes</a></li>
                            <li><a href="#">Client Sites</a></li>
                            <li><a href="#">Users</a></li>
                            <li><a href="#">Trades</a></li>
                            <li><a href="#">Resources</a></li>
                            <li><a href="#">Billing Details</a></li>
                            <li><a href="#">Payment Options</a></li>
                            <li><a href="#">Company Profile</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">DocuGUARD</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Summary</a></li>
                            <li><a href="#">PQF 2013</a></li>
                            <li><a href="#">Annual Update 2013</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">AuditGUARD</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Summary</a></li>
                            <li><a href="#">Manual Audit 2013</a></li>
                            <li><a href="#">Implementation Audit 2013</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">InsureGUARD</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Summary</a></li>
                            <li><a href="#">Certificates Manager</a></li>
                            <li><a href="#">Automobile Liability 2013</a></li>
                            <li><a href="#">Excess Umbrella Liability 2013</a></li>
                            <li><a href="#">General Liability 2013</a></li>
                            <li><a href="#">Workers' Comp 2013</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">EmployeeGUARD</a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Summary</a></li>
                            <li><a href="#">Employees</a></li>
                            <li><a href="#">Job Roles</a></li>
                            <li><a href="#">Competencies</a></li>
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
                                </ul>
                            </li>
                        </ul>
                    </li>
                </ul>
                <ul class="nav pull-right">
                    <li class="dropdown account">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Joe Cool <i class="icon-cog icon-large"></i></a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Edit Profile</a></li>
                            <li><a href="#">Logout</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
    </div>
</div>