<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/reports.css?v=${version}" />

<div class="server-info">
    <h2>Operating System Info</h2>
    <table class="report">
    <thead>
    <tr>
        <td>Name</td>
        <td>Parameter</td>
    </tr>
    </thead>
    
    <tr>
        <td>
            Name&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td>
            <s:property value="os.name"/>
        </td>
    </tr>
    
    <tr>
        <td>
            Architecture&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td>
            <s:property value="os.arch"/>
        </td>
    </tr>
    
    <tr>
        <td>
            Version&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td>
            <s:property value="os.version"/>
        </td>
    </tr>
    
    <tr>
        <td>
            Available Processors&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td>
            <s:property value="os.availableProcessors"/>
        </td>
    </tr>
    
    <tr>
        <td>
            Load Average&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td>
            <s:property value="os.systemLoadAverage"/>
        </td>
    </tr>
    
    <tr>
        <td>
            Heap Memory Usage&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td>
            <s:property value="heapMemoryUsage/1000000"/>Mb
        </td>
    </tr>
    
    <tr>
        <td>
            Non-Heap Memory Usage&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td>
            <s:property value="nonHeapMemoryUsage/1000000"/>Mb
        </td>
    </tr>
    
    <tr>
        <td>
            Total Memory Usage&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td>
            <s:property value="totalMemoryUsage/1000000"/>Mb
        </td>
    </tr>
    
    </table>
    
    <s:set var="liveClass" value="%{isLiveEnvironment() ? 'active' : 'inactive'}" />
    <s:set var="betaClass" value="%{isBetaEnvironment() ? 'active' : 'inactive'}" />
    <s:set var="qaClass" value="%{isQaEnvironment() ? 'active' : 'inactive'}" />
    <s:set var="alphaClass" value="%{isAlphaEnvironment() ? 'active' : 'inactive'}" />
    <s:set var="localhostClass" value="%{isLocalhostEnvironment() ? 'active' : 'inactive'}" />
    
    <h2>
        Environment
    </h2>
    <ul class="environment-tester">
        <li class="${liveClass}">Stable</li>
        <li class="${betaClass}">Beta</li>
        <li class="${qaClass}">QA</li>
        <li class="${alphaClass}">Alpha</li>
        <li class="${localhostClass}">Localhost</li>
    </ul>
</div>