<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
    <%-- <title><s:text name="%{conAudit.auditType.getI18nKey('name')}" /> for <s:property value="conAudit.contractorAccount.name" /></title> --%>

    <link rel="stylesheet" type="text/css" href="css/style.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" href="css/print.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" href="v7/css/vendor/bootstrap.css?v=${version}" />
    <link rel="stylesheet" type="text/css" href="v7/css/vendor/bootstrap-responsive.css?v=${version}" />
</head>
<body id="${actionName}_${methodName}_page" class="${actionName}-page page iol-print-template">
    <div class="row">
        <button id="print" class="btn btn-primary span1 offset11" href="#" onclick="window.print(); return false;"><s:text name="global.print"/></button>
    </div>

    <header>
        <h1>Products and Chemicals Contractor Safety Management Practice</h1>
        <p>Risk Mitigation Plan</p>
        <p>RMP Template</p>
    </header>
    <hr />
    <section>
        <div class="row">
            <div class="span4">
                <p>
                    <label>Company:</label>
                    <span>Bob's Mowing</span>
                </p>
                <p>
                    <label>Developed By:</label>
                    <span>Captain Underpants, Snoopy, John Smith</span>
                </p>
            </div>
            <div class="span4">
                <p>
                    <label>Risk level:</label>
                    <span>54</span>
                </p>
            </div>
            <div class="span4">
                <p>
                    <label>Work Level:</label>
                    <span>2</span>
                </p>
                <p>
                    <label>Review Date:</label>
                    <span>02/02/14</span>
                </p>
            </div>
        </div>
    </section>

    <section>
        <h1>Executive Summary</h1>
        <div class="executive-summary table-striped">
            <div>
                <label>Summarize the Services Provided by Contractor:</label>
                <small>(include descrption of regional areas and/or business untis covered)</small>
                <ul>
                    <li>hi</li>
                </ul>
            </div>
            <div>
                <label>Summarize the Risk:</label>
                <small>(in terms of last 3-yr average TRI/LTI freq., safety mgmt system gaps, other incidents/violations, mgmt commitment)</small>
                <ul>
                    <li>hi</li>
                </ul>
            </div>
            <div>
                <label>Decision:</label>
                <small>(for use or continued use of contractor)</small>
                <ul>
                    <li>hi</li>
                </ul>
            </div>
            <div>
                <label>Justification:</label>
                <small>(brief summary of why thsi decision was made)</small>
                <ul>
                    <li>hi</li>
                </ul>
            </div>
            <div>
                <label>Summarize the Risk Mitigation Plan:</label>
                <small>(Describe the key aspects of the focused safety improvement plan for the Contractor)</small>
                <ul>
                    <li>hi</li>
                </ul>
            </div>
            <div class="row rmp-info">
                <div class="span4">
                    <p>
                        <label>RMP Owner:</label>
                        <span>Mr. Maggot</span>
                    </p>
                    <p>
                        <label>BU/Site:</label>
                        <span>Inferno</span>
                    </p>
                    <p>
                        <label>Name:</label>
                    <span>Sgt. Spandex</span>
                    </p>
                </div>
                <div class="span4">
                    <p>
                        <label>RMP User Contacts: BU/Site:</label>
                        <ul>
                            <li>hi</li>
                            <li>hi</li>
                            <li>hi</li>
                        </ul>
                    </p>
                </div>
                <div class="span3">
                    <p>
                        <label>Name:</label>
                        <ul>
                            <li>hi</li>
                            <li>hi</li>
                            <li>hi</li>
                            <li>hi</li>
                        </ul>
                    </p>
                </div>
            </div>
        </div>
    </section>

    <section>
        <h1>Risk Mitigation Plan (RMP)</h1>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Specific Improvment Items</th>
                    <th>Resp.</th>
                    <th>Target Date</th>
                    <th>Comments/Status</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>1</td>
                    <td>Words</td>
                    <td>More Words</td>
                    <td>Date</td>
                    <td>Comments for you.</td>
                </tr>
                <tr>
                    <td>1</td>
                    <td>Words</td>
                    <td>More Words</td>
                    <td>Date</td>
                    <td>Comments for you.</td>
                </tr>
                <tr>
                    <td>1</td>
                    <td>Words</td>
                    <td>More Words</td>
                    <td>Date</td>
                    <td>Comments for you.</td>
                </tr>
            </tbody>
        </table>
    </section>

    <section>
        <h1>Subcontractors</h1>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Company Name</th>
                    <th>IOL Endorsed (Y/N)</th>
                    <th>Resp. (if not endorsed)</th>
                    <th>Target Date (if not endorsed)</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>1</td>
                    <td>Company</td>
                    <td>Y</td>
                    <td>What</td>
                    <td>Date</td>
                </tr>
            </tbody>
        </table>
    </section>

    <section>
        <h1>RMP Stewardship</h1>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th></th>
                    <th>Monthly</th>
                    <th>Quarterly</th>
                    <th>Semi-Annually</th>
                    <th>Annually</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <strong>Frequency of Review of Status of Improvement Items</strong>
                        <p><small>Check off appropriate frequency</small></p>
                    </td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
            </tbody>
        </table>
    </section>

    <section id="signatures">
        <h1>Planned Date of Next Annual RMP Review/Update</h1>
        <label>Date:</label>
        <span>02/02/52</span>

        <ol>
            <li>
                <label>Contractor's Approval of Risk Mitigation Plan</label>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span>Bob Smengee / Corporate Badass</span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
            <li>
                <label>RMP owner's Endorsement of Risk Mitigation Plan</label>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span>Bob Smengee / Corporate Badass</span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
            <li>
                <label>IOL Approval of Risk Mitigation Plan and Use of Contractor:</label>
                <small>(see required approval level in RMP Development User's Guide)</small>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span>Bob Smengee / Corporate Badass</span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
            <li>
                <label>Other IOL Endorsements</label>
                <small>(e.g. includes local IOL Buddy Manager and others requested by IOL Approver)</small>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span>Bob Smengee / Corporate Badass</span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
            <li>
                <label>Other Contractor Endorsements</label>
                <small>(e.g. local Superintendent or Contractor Buddy Manager)</small>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span>Bob Smengee / Corporate Badass</span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
        </ol>
    </section>
</body>