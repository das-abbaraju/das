<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:url action="ManageReports" var="manage_reports_url" />

<title>Dynamic Reporting</title>

<div id="main" class="container">
    <header>
        <h1>Dynamic Reporting</h1>
        <h2>PICS Organizer 7.0</h2>
    </header>
    
    <div class="content">
        <div class="row report-overview">
            <div class="span8 image-container">
                <img src="/v7/img/tutorial/report/overview.png" />
            </div>
            <div class="span4">
                <section class="overview">
                    <h1>Easy and flexible reports</h1>
                    <p>
                        Dynamic Reporting is a powerful new tool that allows you to customize the information that you're looking for, utilizing PICS's deep database. This is a quick overview, broken up into three distinct sections, of some of the areas that make Dynamic Reporting such a special and unique tool:
                    </p>
                
                    <dl>
                        <dt><a href="#report-information-separator">Report Information</a></dt>
                        <dd>What your report is and what you can do with it</dd>
                        <dt><a href="#report-data-separator">Report Data</a></dt>
                        <dd>All the information that you're looking for</dd>
                        <dt><a href="#data-filters-separator">Data Filters</a></dt>
                        <dd>Options to customize your report data</dd>
                    </dl>
                </section>
            </div>
        </div>

        <div id="report-information-separator" class="row section-separator-container">
            <div class="span8 offset2 section-separator">
            </div>
        </div>
                
        <div class="row section-header">
            <div class="span4 image-container">
                <img src="/v7/img/tutorial/report/report-information.png" />
            </div>
            <div class="span8">
                <h2>Report Information</h2>
            </div>
        </div>
        
        <div class="row report-summary">
            <div class="span6 image-container">
                <img src="/v7/img/tutorial/report/title.png" />
            </div>
            <div class="span6">
                <section class="overview">
                    <h1>Report's Name and Description</h1>
                    <p>
                        The title of your report and its description will always appear at the top-left of the page. If you own the report, or have edit permissions, you can change this to better suit the data at any time. Just access your report's settings&hellip;
                    </p>
                </section>
            </div>
        </div>
        
        <div class="row report-settings">
            <div class="span5 image-container">
                <img src="/v7/img/tutorial/report/settings.png" />
            </div>
            <div class="span7">
                <section class="overview">
                    <h1>Save and Settings</h1>
                    <p>
                        The top-right of the page contains the report's Save button, as well as the Settings button (the cog icon). Save will update your report if you own it, or offer you the option to duplicate it and save it as your own if you don't. The Settings button contains the majority of the report's options:
                    </p>
                    
                    <dl>
                        <dt>Settings</dt>
                        <dd>Rename and edit your report's description, mark it as one of your favorites, and specify whether it is searchable for other users to utilize your template for their data.</dd>
                        <dt>Duplicate</dt>
                        <dd>Having all the same options as Settings, this allows you to make a copy of your current report. Duplicate also allows you to copy someone else's template and make one of your own so you have full control over it.</dd>
                        <dt>Share</dt>
                        <dd>Think that someone you know could really use this report? Share it with them. Select an individual or group and select whether you want to allow full editing permissions or not. (Remember, if you grant full permissions, they can do everything you can &mdash; even delete the report!).</dd>
                        <dt>Export</dt>
                        <dd>Move your data out to your favorite spreadsheet application or make a PDF that's great for handouts and emailing.</dd>
                        <dt>Print</dt>
                        <dd>Specially formatted and printer friendly options for your report</dd>
                    </dl>
                </section>
            </div>
        </div>

        <div id="report-data-separator" class="row section-separator-container">
            <div class="span8 offset2 section-separator">
            </div>
        </div>
        
        <div class="row section-header">
            <div class="span4 image-container">
                <img src="/v7/img/tutorial/report/report-data.png" />
            </div>
            <div class="span8">
                <h2>Report Data</h2>
            </div>
        </div>
        
        <div class="row report-data">
            <div class="span8 image-container">
                <img src="/v7/img/tutorial/report/data.png" />
            </div>
            <div class="span4">
                <section class="overview">
                    <h1>Columns and Headers</h1>
                    <p>
                        Here's where all your information will display. Feel free to customize this information to fit your needs. Nearly every part of PICS Organizer can be accessed for you to compare and view on.
                    </p>
                    <p>
                        Clicking on a column header will bring up its options. Sort your content or apply a function to the data. This is also where you can choose to remove the column. Columns can be rearranged by dragging them in front or behind other columns, and you can adjust the width of the column by dragging the right-hand side of the column.
                    </p>
                </section>
            </div>
        </div>
        
        <div class="report-toolbar">
            <img src="/v7/img/tutorial/report/toolbar.png" />
        </div>
        
        <div class="row report-data-options">
            <div class="span6 offset3">
                <section class="overview">
                    <h1>Data Options</h1>
                    <p>
                        The blue bar at the top of the data section contains tools and information for the data that you’re looking at. The first icon on the left is your Refresh button to make sure what you’re seeing is up-to-date. The next section deals with pagination when dealing with lots of results, and tells you just how much you’re looking at. You can increase the number of results that appear on each page in the drop down. Most importantly, the button on the far right allows you to add more columns to your report so it’s exactly what you need.
                    </p>
                </section>
            </div>
        </div>

        <div id="data-filters-separator" class="row section-separator-container">
            <div class="span8 offset2 section-separator">
            </div>
        </div>
        
        <div class="row section-header">
            <div class="span4 image-container">
                <img src="/v7/img/tutorial/report/report-filter.png" />
            </div>
            <div class="span8">
                <h2>Data Filters</h2>
            </div>
        </div>
        
        <div class="row report-filters">
            <div class="span4 image-container">
                <img src="/v7/img/tutorial/report/filters.png" />
            </div>
            <div class="span8">
                <section class="overview">
                    <h1>Your data exactly the way you like it</h1>
                    <p>
                        Filters will let you refine your data down to the most granular level. The Filter panel is hidden by default. Once you’re happy with your data columns you can select the Filter icon on the far left side (next to the Refresh icon). This pulls out the panel for you to edit. Add Filters with the button in the top right, and hide the panel again by clicking on the arrow or the word “Filter”. Some tips for getting the most out of filters:
                    </p>
                    
                    <dl>
                        <dt>You don’t have to have the column visible</dt>
                        <dd>Want only Green Flagged Companies, but don’t need to see a column of green flags? Don’t worry about it! Just add the filter and your data will update intelligently.</dd>
                        <dt>Refresh your data</dt>
                        <dd>Most of the time your data will update live. If it doesn’t seem to have updated your results after you applied or removed a filter, just hit the Refresh button. That will make sure your data is completely up to date.</dd>
                        <dt>Removing filters</dt>
                        <dd>Hover over a filter or click on it. A red remove button will appear in the top-right of the filter. Click on it to remove the filter and its associated effects.</dd>
                    </dl>
                </section>
            </div>
        </div>
        
        <div class="row report-formula">
            <div class="span5 image-container">
                <img src="/v7/img/tutorial/report/formula.png" />
            </div>
            <div class="span7">
                <section class="overview">
                    <h1>Filter Formula</h1>
                    <p>
                        Advanced users can even filter their filters (I know, right!) using the Filter Formula. Clicking on the charcoal bar will open the Filter Formula area. Simply type the number of the associated filter (they’re listed numerically from top to bottom and listed on the left-hand side of the filter) and its relation to other filters. The functions “AND,” “OR,” and parentheses “(  )” are all supported. Clicking the Remove icon hides the Filter Formula and removes its effects.
                    </p>
                </section>
            </div>
        </div>
    </div>
    
    <footer>
        <a href="${manage_reports_url}" class="btn btn-success btn-large">Start Dynamic Reporting <i class="icon-circle-arrow-right icon-large"></i></a>
    </footer>
</div>