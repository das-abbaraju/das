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

            #primary_navigation [data-toggle=collapse]
            {
                border: 0;
                background-color: transparent;
                display: none;
                float: right;
                line-height: 50px;
            }

#primary_navigation .nav > .account > a
{
    color: #fff;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);
    background-color: #3f93c0;
    background-image: -moz-linear-gradient(top, #73bbe8, #3f93c0);
    background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#73bbe8), to(#3f93c0));
    background-image: -webkit-linear-gradient(top, #73bbe8, #3f93c0);
    background-image: -o-linear-gradient(top, #73bbe8, #3f93c0);
    background-image: linear-gradient(to bottom, #73bbe8, #3f93c0);
    background-repeat: repeat-x;
    display: block;
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff73bbe8', endColorstr='#ff3f93c0', GradientType=0);
    border-color: #3f93c0 #3f93c0 #336699;
    border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
    *background-color: #3f93c0;
    /* Darken IE7 buttons by default so they stand out more given they won't have borders */

    filter: progid:DXImageTransform.Microsoft.gradient(enabled = false);
    padding: 8px 15px 9px;
}

#primary_navigation .nav > li.account > a:hover,
#primary_navigation .nav > li.account > a:focus
{
    background-color: #3f93c0;
    color: #fff;
    text-decoration: none;
    background-position: 0 -15px;
    -webkit-transition: background-position .1s linear;
    -moz-transition: background-position .1s linear;
    -o-transition: background-position .1s linear;
    transition: background-position .1s linear;
}

#primary_navigation .nav > li.account > a:active
{
    background-color: #3f93c0 \9;
    background-color: #3f93c0;
    background-image: none;
    outline: 0;
    -webkit-box-shadow: inset 0 2px 4px rgba(0,0,0,0.15),0 1px 2px rgba(0,0,0,0.05);
    -moz-box-shadow: inset 0 2px 4px rgba(0,0,0,0.15),0 1px 2px rgba(0,0,0,0.05);
    box-shadow: inset 0 2px 4px rgba(0,0,0,0.15),0 1px 2px rgba(0,0,0,0.05);
}

            #primary_navigation .navbar-search .icon-search
            {
                left: 24px;
                position: absolute;
                top: 17px;
            }
            
            #primary_navigation .navbar-search .search-query
            {
                padding: 4px 14px 4px 32px;
            }
            
            #primary_navigation .navbar-search .search-query:focus ~ .icon-search
            {
                color: #73bbe8;
            }

#primary_navigation .nav .account .switch-to
{
    display: inline-block;
    display: inline \9;
    line-height: 16px;
    margin-right: 5px;
    text-align: right;
}

#primary_navigation .nav .account .switch-to span
{
    font-size: 10px;
}

#primary_navigation .nav .account .icon-cog
{
    vertical-align: 5px;
}

            #secondary_navigation
            {
                top: 53px;
            }
            
            #secondary_navigation .navbar-inner
            {
                background: #333;
                background-image: -webkit-gradient(linear,50% 0,50% 100%,color-stop(0%,#252525),color-stop(100%,#333));
                background-image: -webkit-linear-gradient(#252525,#333);
                background-image: -moz-linear-gradient(#252525,#333);
                background-image: -o-linear-gradient(#252525,#333);
                background-image: linear-gradient(#252525,#333);
                filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff252525',endColorstr='#ff333333',GradientType=0);
                border-bottom: 0;
                border-top: 1px solid #000;
                color: #f1f1f2;
                line-height: 29px;
                min-height: 29px;
                padding-bottom: 0;
                padding-top: 0;
            }

            #secondary_navigation [data-toggle=collapse]
            {
                border: 0;
                background-color: transparent;
                color: #f1f1f2;
                display: none;
                float: right;
                line-height: 28px;
            }

            #secondary_navigation .nav > li > a
            {
                border: 0;
                color: #ccc;
                padding: 4px 15px 5px;
                text-shadow: none;
            }
            
            #secondary_navigation .nav > li > a:hover
            {
                color: #f1f1f2;
            }

            #secondary_navigation .nav li.dropdown.open > .dropdown-toggle,
            #secondary_navigation .nav li.dropdown.active > .dropdown-toggle,
            #secondary_navigation .nav li.dropdown.open.active > .dropdown-toggle
            {
                color: #73bbe8;
                background: #252525;
                background-image: -webkit-gradient(linear,50% 0,50% 100%,color-stop(0%,#000),color-stop(100%,#252525));
                background-image: -webkit-linear-gradient(#000,#252525);
                background-image: -moz-linear-gradient(#000,#252525);
                background-image: -o-linear-gradient(#000,#252525);
                background-image: linear-gradient(#000,#252525);
                filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff000000',endColorstr='#ff252525',GradientType=0);
            }

            #secondary_navigation .nav .account span
            {
                color: #73bbe8;
                display: block;
                font-size: 13px;
                font-weight: bold;
                padding: 4px 15px 5px;
            }

@media (max-width: 979px) {
                .navbar-fixed-top .navbar-inner,
                .navbar-fixed-bottom .navbar-inner
                {
                    padding: 5px 10px;
                }
    
                .nav-collapse .dropdown-menu
                {
                    margin: 0;
                }
    
                #primary_navigation,
                #secondary_navigation
                {
                    margin-bottom: 0;
                }
    
                #primary_navigation .navbar-inner
                {
                    background: #f1f1f2;
                }
    
                #primary_navigation .brand
                {
                    margin-left: 5px;
                }
    
                #primary_navigation [data-toggle=collapse],
                #secondary_navigation [data-toggle=collapse]
                {
                    display: block;
                }
    
                #primary_navigation .nav > li > a,
                #primary_navigation .nav > .account > a
                {
                    border-radius: 0;
                    padding: 6px 5px;
                }
    
                #primary_navigation .nav-collapse .nav > li > .dropdown-menu,
                #secondary_navigation .nav-collapse .nav > li > .dropdown-menu
                {
                    background: #eaeaea;
                    display: none;
                }
                
                #primary_navigation .nav-collapse .nav > .open > .dropdown-menu,
                #secondary_navigation .nav-collapse .nav > .open > .dropdown-menu
                {
                    display: block;
                }
    
                #primary_navigation .nav-collapse .dropdown-menu a,
                #secondary_navigation .nav-collapse .dropdown-menu a
                {
                    border-radius: 0;
                    color: #454545;
                    font-weight: normal;
                }
    
                #primary_navigation .nav-collapse .dropdown-menu a:hover
                {
                    color: #226396;
                    background: #dcdcdc;
                }
    
                #primary_navigation .nav-collapse .open > a
                {
                    background: #f1f1f2;
                    border: 1px solid transparent;
                }
    
    
                #primary_navigation .navbar-search
                {
                    border: 1px solid transparent;
                }
    
                #primary_navigation .search-query
                {
                    box-sizing: border-box;
                    display: block;
                    min-height: 30px;
                    width: 100%;
                }
    
    #primary_navigation .nav > .account > a,
    #primary_navigation .nav-collapse .nav > .account > a:hover
    {
        background: #3f93c0;
    }
    
    #primary_navigation .nav .account span
    {
        display: none;
    }
    
    #primary_navigation .nav .account .icon-cog
    {
        vertical-align: 0;
    }
    
                #primary_navigation .dropdown-submenu:hover > a,
                #primary_navigation .dropdown-submenu:focus > a
                {
                    background: #eaeaea;
                }
    
                #secondary_navigation .navbar-inner
                {
                    background: #252525;
                }
    
                #secondary_navigation .nav-collapse .dropdown-menu a:hover
                {
                    background: #252525;
                }
    
                .dropdown-submenu .dropdown-menu
                {
                    margin-left: 10px;
                }
}
</style>

<div id="primary_navigation" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <button type="button" data-toggle="collapse" data-target=".apple">
                <i class="icon-reorder icon-large"></i>
            </button>
            
            <button type="button" data-toggle="collapse" data-target="#primary_navigation_search">
                <i class="icon-search icon-large"></i>
            </button>
            
            <a class="brand" href="/"></a>
            
            <div class="apple nav-collapse collapse">
                <ul class="nav pull-left">
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
            </div>
            <div class="apple nav-collapse collapse">
                <ul class="nav pull-right">
                    <li class="dropdown account">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                        <div class="switch-to"><span>Switched to<br /></span>Dennis Truitt</div>
                        
                        <i class="icon-cog icon-large"></i></a>
                        
                        <ul class="dropdown-menu">
                            <li><a href="#">Edit Profile</a></li>
                            <li><a href="#">(Switch Back)</a></li>
                            <li><a href="#">Logout</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
                <div id="primary_navigation_search" class="nav-collapse collapse">
                    <form action="/" class="navbar-search pull-right">
                        <input type="hidden" name="button" value="search" />
                        <input type="text" name="searchTerm" class="search-query span2" placeholder="Search" />
                        <i class="icon-search icon-large"></i>
                    </form>
                </div>
        </nav>
    </div>
</div>

<div id="secondary_navigation" class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <nav class="container">
            <button type="button" data-toggle="collapse" data-target="#secondary_navigation_collapse">
                <i class="icon-reorder"></i>
            </button>
            
            <div id="secondary_navigation_collapse" class="nav-collapse collapse">
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
                    <li class="account">
                        <span>Ancon Marine</span>
                    </li>
                </ul>
            </div>
        </nav>
    </div>
</div>