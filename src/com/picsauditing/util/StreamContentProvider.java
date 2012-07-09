package com.picsauditing.util;

import java.io.IOException;
import java.io.InputStream;

public interface StreamContentProvider {

	public String getResponseFrom(String uri) throws IOException;

	public InputStream openResponseFrom(String uri) throws IOException;

}
