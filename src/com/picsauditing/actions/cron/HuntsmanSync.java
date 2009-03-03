package com.picsauditing.actions.cron;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;

public class HuntsmanSync extends PicsActionSupport {

	private AppPropertyDAO appPropDao = null;

	public HuntsmanSync(AppPropertyDAO appPropDao) {
		this.appPropDao = appPropDao;
	}

	public String execute() throws Exception {

		String server = appPropDao.find("huntsmansync.ftp.server").getValue();
		String username = appPropDao.find("huntsmansync.ftp.user").getValue();
		String password = appPropDao.find("huntsmansync.ftp.password").getValue();
		String folder = appPropDao.find("huntsmansync.ftp.folder").getValue();

		// there may be other files in that folder. we can use this to filter
		// down to the ones we want.
		// String pattern =
		// appPropDao.find("huntsmansync.ftp.filePattern").getValue();

		FTPClient ftp = new FTPClient();
		ftp.connect(server);
		ftp.login(username, password);

		ftp.changeWorkingDirectory(folder);

		FTPFile[] files = ftp.listFiles();

		if (files != null) {

			for (FTPFile ftpFile : files) {

				BufferedReader reader = null;

				InputStream retrieveFileStream = ftp.retrieveFileStream(ftpFile
						.getName());

				if (retrieveFileStream != null) {

					reader = new BufferedReader(new InputStreamReader(
							retrieveFileStream));

					String line = null;

					while ((line = reader.readLine()) != null) {

						if (line.length() > 0) {

							String[] data = line.split(",");

							if (data.length == 2) {
								// contractor id
								Integer contractorId = Integer
										.parseInt(data[0]);
								System.out.println(contractorId);
								// the other field. comes in as a Y/N.
								String yn = data[1];
								
							} else {
								// maybe append this to a report that gets
								// emailed
								System.out.println("bad data");
							}
						}
					}
				} else {
					// maybe append this to a report that gets emailed
					System.out.println("unable to open connection: "
							+ ftp.getReplyCode() + ":" + ftp.getReplyString());
				}
			}
		}

		ftp.logout();
		ftp.disconnect();

		return SUCCESS;
	}
}
