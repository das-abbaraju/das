<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title>
    <s:text name="NavigationMenu.title" />
</title>

<div id="main" class="container">
    <header>
        <h1>
            Navigation Menu
        </h1>
        <h2>
            PICS Organizer 7.0
        </h2>
    </header>

    <div class="content">
        <div class="row dashboard">
            <div class="span6 image-container">
                <img src="v7/img/reference/navigation/dashboard.png" />
            </div>

            <div class="span6">
                <section class="overview">
                    <h1>
                        Welcome to the new menu
                    </h1>
                    <p>
                        Every time you enter PICS Organizer you land on your Dashboard. This is your hub for information and helps to make sure that everything is on-track. If you ever need to return, just click on the PICS logo or select Dashboard from the Company menu.
                    </p>
                </section>
            </div>
        </div>

        <div class="row settings">
            <div class="span4 image-container">
                <img src="v7/img/reference/navigation/settings.png" />
            </div>
            <div class="span8">
                <section class="overview">
                    <h1>
                        Your settings and logout
                    </h1>
                    <p>
                        To update or edit your account, simply click on your name on the far right of the menu. You’ll see the “cog” icon pop up throughout the PICS redesign. This icon will always lead you to settings and preferences for the section you’re in.
                    </p>
                </section>
            </div>
        </div>

        <div class="row support">
            <div class="span6 image-container">
                <img src="v7/img/reference/navigation/support.png" />
            </div>

            <div class="span6">
                <section class="overview">
                    <h1>
                        We're here to help
                    </h1>
                    <p>
                        PICS has always prided itself on its personal and reliable Customer Support. To view help topics, chat or talk to your CSR, or email us; simply select Support in your menu.
                    </p>
                    <p>
                        You can also this and any other reference material in the Reference section of the Support menu.
                    </p>
                </section>
            </div>
        </div>
    </div>

    <footer>
        <div class="row">
            <div class="span4 offset4">
                <s:url action="Home" var="home_url" />

                <a class="btn btn-success btn-large" href="${home_url}">Start Using PICS Organizer 7.0 <i class="icon-circle-arrow-right"></i></a>
                <p>
                    For a short time, you can choose to revert to the old menu style by going to your Account Settings.
                </p>
            </div>
        </div>
    </footer>
</div>