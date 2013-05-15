package com.picsauditing.coverage;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.*;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.lang.String.format;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"ContractorCronSmallSetCodeCoverage-context.xml"})
public class ContractorCronSmallSetCodeCoverage {
    private static Logger logger = LoggerFactory.getLogger(ContractorCronSmallSetCodeCoverage.class);
    private static final String cronUrl = "http://localhost:8080/ContractorCronAjax.action?conID=%s&steps=All&button=Run";
    private static final int MAX_CONTRACTORS_WITH_NO_IMPROVEMENT = 200;

    public static final String JACOCO_EXEC_PATH = "/tmp/jacoco.exec";
    public static final String JACOCO_EXEC_PATH_BACKUP = "/tmp/jacoco.exec.backup";
    public static final String OPERATOR_TAG = "SmokeTestContractor";
    private String address = "localhost";
    private int port = 9009;
    private boolean dump = true;
    private boolean reset = false;
    private boolean append = true;
    private ExecutionDataStore executionDataStore;
    private OperatorTag operatorTag;
    private List<Integer> contractorIdsNotYetRun;

    private int totalContractorsInSet = 0;
    private int totalContractorsRun = 0;
    private int numberOfTriesWithNoCoverageIncrease = 0;
    private int previousTotalLinesCovered = 0;

    @Autowired
    private ContractorAccountDAO contractorDAO;
    @Autowired
    private OperatorTagDAO operatorTagDAO;
    @Autowired
    private ContractorTagDAO contractorTagDAO;

    @Test
    public void run() throws Exception {
        resetForNewRun();
        while (numberOfTriesWithNoCoverageIncrease < MAX_CONTRACTORS_WITH_NO_IMPROVEMENT) {
            logger.debug("Number of tries with no increase is {}", numberOfTriesWithNoCoverageIncrease);
            logger.debug("Current lines covered is {}", previousTotalLinesCovered);
            Integer id = nextContractorIdAndRemoveFromNotYetRun();
            logger.debug("Running contractor {}", id);
            backupCoverageDataForNextContractor();
            if (runContractorCron(id)) {
                manageCoveragePostCronRun(id);
            }
            totalContractorsRun++;
        }
        logger.info("Total contractors in coverage set: " + totalContractorsInSet);
        logger.info("Total contractors run: " + totalContractorsRun);
        logger.info("Total lines of code covered: " + previousTotalLinesCovered);
    }

    private Integer nextContractorIdAndRemoveFromNotYetRun() {
        Integer id = contractorIdsNotYetRun.get((int)(Math.random()*(contractorIdsNotYetRun.size() - 1)));
        contractorIdsNotYetRun.remove(id);
        return id;
    }

    private void manageCoveragePostCronRun(Integer id) throws Exception {
        logger.debug("Analyzing code coverage impact of contractor {}: ", id);
        checkIfContractorAddsCoverage(id);
    }

    private void checkIfContractorAddsCoverage(Integer id) throws Exception {
        dump();
        int newLinesCovered = analyze();
        if (newLinesCovered > previousTotalLinesCovered) {
            logger.debug("Contractor {} increased coverage from {} to {}", new Object[] {id, previousTotalLinesCovered, newLinesCovered});
            saveContractorTag(newContractorTag(contractorDAO.find(id)));
            totalContractorsInSet++;
            previousTotalLinesCovered = newLinesCovered;
            numberOfTriesWithNoCoverageIncrease = 0;
        } else {
            numberOfTriesWithNoCoverageIncrease++;
        }
    }

    private boolean runContractorCron(Integer id) throws Exception {
        String urlToRun = format(cronUrl, id);
        logger.debug("Running ContractorCron with url: {}", urlToRun);
        InputStream inputStream = executeUrl(urlToRun);
        String response = stringFromInputStream(inputStream);
        logger.debug("Cron responded with {}", response);
        if (!response.startsWith("INFO: Completed")) {
            logger.error("Error running CCron for contractor {}: {}", id, response);
            restoreBackupOfCoverageData();
            return false;
        }
        return true;
    }

    private void backupCoverageDataForNextContractor() throws IOException {
        File sourceFile = new File(JACOCO_EXEC_PATH);
        if (sourceFile.exists()) {
            FileUtils.copyFile(sourceFile, new File(JACOCO_EXEC_PATH_BACKUP));
        }
    }

    private void resetForNewRun() throws Exception {
        findSmokeTestOperatorTag();
        resetContractorTags();
        new File(JACOCO_EXEC_PATH).delete();
        contractorIdsNotYetRun = contractorDAO.findContractorsNeedingRecalculation(Integer.MAX_VALUE, new HashSet<Integer>());
    }

    private void resetContractorTags() throws Exception {
        List<ContractorTag> contractorTags = contractorTagDAO.getTagsByTagID(operatorTag.getId());
        for (ContractorTag contractorTag : contractorTags) {
            contractorTagDAO.remove(contractorTag.getId());
        }
    }


    private void restoreBackupOfCoverageData() throws IOException {
        FileUtils.copyFile(new File(JACOCO_EXEC_PATH_BACKUP), new File(JACOCO_EXEC_PATH));
    }

    private void saveContractorTag(ContractorTag tag) {
        contractorTagDAO.save(tag);
    }

    private ContractorTag newContractorTag(ContractorAccount contractor) {
        ContractorTag tag = new ContractorTag();
        tag.setContractor(contractor);
        tag.setTag(operatorTag);
        tag.setAuditColumns(new User(User.SYSTEM));
        return tag;
    }

    private void findSmokeTestOperatorTag() throws Exception {
        operatorTag = operatorTagDAO.findByTagAndOperator(OPERATOR_TAG, OperatorAccount.PicsConsortium);
        if (operatorTag == null) {
            throw new Exception("We must have an operator tag named " + OPERATOR_TAG);
        }
    }

    private int analyze() throws Exception {
        loadExecutionData();
        final CoverageBuilder builder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(executionDataStore, builder);
        analyzer.analyzeAll(new File("target/classes"));
        int totalLinesCovered = 0;
        Collection<IClassCoverage> classCoverages = builder.getClasses();
        for (IClassCoverage classCoverage : classCoverages) {
            ICounter counter  = classCoverage.getInstructionCounter();
            totalLinesCovered += counter.getCoveredCount();
        }
        return totalLinesCovered;
    }

    private void loadExecutionData() throws IOException {
        final ExecFileLoader loader = new ExecFileLoader();
        InputStream in = null;
        try {
            loader.load(openInputStream());
        } catch (final IOException e) {
            throw e;
        } finally {
            if (in != null) {
                in.close();
            }
        }
        executionDataStore = loader.getExecutionDataStore();
    }

    private void dump() throws Exception {
        OutputStream output = null;
        try {
            final Socket socket = new Socket(InetAddress.getByName(address), port);
            logger.info("Connecting to {}", socket.getRemoteSocketAddress());
            final RemoteControlWriter remoteWriter = new RemoteControlWriter(socket.getOutputStream());
            final RemoteControlReader remoteReader = new RemoteControlReader(socket.getInputStream());

            output = openOutputStream();
            final ExecutionDataWriter outputWriter = new ExecutionDataWriter(output);
            remoteReader.setSessionInfoVisitor(outputWriter);
            remoteReader.setExecutionDataVisitor(outputWriter);

            remoteWriter.visitDumpCommand(dump, reset);
            remoteReader.read();

            socket.close();
        } catch (final IOException e) {
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private InputStream executeUrl(String url) {
        try {
            HttpMethod method = new GetMethod(url);
            HttpClient client = new HttpClient();
            int responseCode = client.executeMethod(method);
            if (responseCode != 200) {
                return null;
            }
            return method.getResponseBodyAsStream();
        } catch (HttpException e) {
            logger.error("HttpException trying to get lat-long from address, using url {}: {}", url, e.getMessage());
        } catch (IOException e) {
            logger.error("IOException trying to get lat-long from address, using url {}: {}", url, e.getMessage());
        }
        return null;
    }


    private InputStream openInputStream() throws IOException {
        return new FileInputStream(new File(JACOCO_EXEC_PATH));
    }

    private OutputStream openOutputStream() throws IOException {
        File destfile = new File(JACOCO_EXEC_PATH);
        logger.info("Dumping execution data to {}", destfile.getAbsolutePath());
        destfile.createNewFile();
        return new FileOutputStream(destfile, append);
    }

    private static String stringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
