
/**
 * ExportServicesCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

    package com.picsauditing.actions.imports.oqsg;

    /**
     *  ExportServicesCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class ExportServicesCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public ExportServicesCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public ExportServicesCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for T_GetNewRecords method
            * override this method for handling normal response from T_GetNewRecords operation
            */
           public void receiveResultT_GetNewRecords(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.T_GetNewRecordsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from T_GetNewRecords operation
           */
            public void receiveErrorT_GetNewRecords(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for GetCompanies method
            * override this method for handling normal response from GetCompanies operation
            */
           public void receiveResultGetCompanies(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.GetCompaniesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from GetCompanies operation
           */
            public void receiveErrorGetCompanies(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for KS_GetLastExportDate method
            * override this method for handling normal response from KS_GetLastExportDate operation
            */
           public void receiveResultKS_GetLastExportDate(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.KS_GetLastExportDateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from KS_GetLastExportDate operation
           */
            public void receiveErrorKS_GetLastExportDate(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for AuthenticateUser method
            * override this method for handling normal response from AuthenticateUser operation
            */
           public void receiveResultAuthenticateUser(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.AuthenticateUserResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from AuthenticateUser operation
           */
            public void receiveErrorAuthenticateUser(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for KS_GetDateRecords method
            * override this method for handling normal response from KS_GetDateRecords operation
            */
           public void receiveResultKS_GetDateRecords(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.KS_GetDateRecordsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from KS_GetDateRecords operation
           */
            public void receiveErrorKS_GetDateRecords(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for SetLastExportDates method
            * override this method for handling normal response from SetLastExportDates operation
            */
           public void receiveResultSetLastExportDates(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.SetLastExportDatesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from SetLastExportDates operation
           */
            public void receiveErrorSetLastExportDates(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for T_GetLastExportDate method
            * override this method for handling normal response from T_GetLastExportDate operation
            */
           public void receiveResultT_GetLastExportDate(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.T_GetLastExportDateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from T_GetLastExportDate operation
           */
            public void receiveErrorT_GetLastExportDate(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for T_GetDateRecords method
            * override this method for handling normal response from T_GetDateRecords operation
            */
           public void receiveResultT_GetDateRecords(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.T_GetDateRecordsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from T_GetDateRecords operation
           */
            public void receiveErrorT_GetDateRecords(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for KS_GetNewRecords method
            * override this method for handling normal response from KS_GetNewRecords operation
            */
           public void receiveResultKS_GetNewRecords(
                    com.picsauditing.actions.imports.oqsg.ExportServicesStub.KS_GetNewRecordsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from KS_GetNewRecords operation
           */
            public void receiveErrorKS_GetNewRecords(java.lang.Exception e) {
            }
                


    }
    