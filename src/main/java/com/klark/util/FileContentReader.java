// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.util;

/**
 * Description here!
 *
 *
 * @author
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.klark.async.email.InvitationEmailThread;

public class FileContentReader {

    private static final Log logger = LogFactory.getLog(InvitationEmailThread.class.getName());

    public static String getContent(String file) throws IOException, URISyntaxException {
        StringBuffer sCurrentLine = new StringBuffer();
        BufferedReader reader = null;
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sCurrentLine.append(line);
            }
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ex) {
                logger.error(ex);
                ex.printStackTrace();
            }
        }
        return sCurrentLine.toString();
    }
}