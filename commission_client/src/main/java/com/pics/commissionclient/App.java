package com.pics.commissionclient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

public class App {

    private static final String INVOICE_ID_FILE = "C:\\files\\invoice_ids.txt";
    private static final String API_KEY = "123";

    public static void main(String[] args) {
        process();
        System.out.println("\nComplete!");
    }

    private static void process() {
        try {
            List<String> invoiceIds = getInvoiceIds();
            if (CollectionUtils.isEmpty(invoiceIds)) {
                return;
            }

            processAllInvoiceCommissions(invoiceIds);

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private static void processAllInvoiceCommissions(List<String> invoiceIds) throws URISyntaxException, IOException {
        HttpClient httpClient = new DefaultHttpClient();

        for (String invoiceId : invoiceIds) {
            System.out.print("InvoiceID = " + invoiceId);
            StringBuilder stringBuilder = new StringBuilder();
            processRequest(invoiceId, httpClient, stringBuilder);
            processResponse(stringBuilder);
        }
    }

    private static URI buildURI(String invoiceId) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("http").setHost("localhost").setPort(8080)
                .setPath("/ProcessCommission.action")
                .setParameter("apiKey", API_KEY)
                .setParameter("invoiceId", invoiceId);

        return uriBuilder.build();
    }

    private static List<String> getInvoiceIds() throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        try {
            List<String> invoiceIds = new ArrayList<String>();
            reader = new BufferedReader(new FileReader(INVOICE_ID_FILE));

            String line;
            while ((line = reader.readLine()) != null) {
                invoiceIds.add(line.trim());
            }

            return invoiceIds;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception nothingWeCanDo) {
                    // can't do anything about this
                }
            }
        }
    }

    private static void processResponse(StringBuilder stringBuilder) {
        String response = stringBuilder.toString();
        if (response.contains("success")) {
            System.out.println(", SUCCESSFUL");
        } else if (response.contains("failure")) {
            System.out.println(", FAILED - " + stringBuilder.toString());
        } else {
            System.out.println("Something bad happened...");
            System.out.println(response);
        }
    }

    private static void processRequest(String invoiceId, HttpClient httpClient, StringBuilder stringBuilder) throws IllegalStateException, IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet(buildURI(invoiceId));
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            InputStream inputStream = null;
            try {
                inputStream = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception nothingWeCanDo) {
                }
            }
        }
    }
}
