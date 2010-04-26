package com.picsauditing.PICS;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatelessKnowledgeSession;

import com.picsauditing.util.FileUtils;

public class DroolsSessionFactory {

	protected KnowledgeBase kbase = null;
	protected KnowledgeBuilder kbuilder = null;

	protected List<String> drlResources = new Vector<String>();

	public StatelessKnowledgeSession getStatelessSession() {
		if (kbase == null)
			setup();

		StatelessKnowledgeSession ksession = kbase
				.newStatelessKnowledgeSession();
		return ksession;
	}

	// stateful stssion stuff here

	public List<String> getDrlResources() {
		return drlResources;
	}

	public void setDrlResources(List<String> drlResources) {
		this.drlResources = drlResources;
	}

	public void reset() {
		kbase = null;
	}
	
	protected void setup() {

		try {

			kbase = KnowledgeBaseFactory.newKnowledgeBase();
			kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

			for (String resourceToLoad : drlResources) {
				Resource resource = null;

				if (resourceToLoad.toLowerCase().startsWith("classpath")) {
					resource = ResourceFactory.newClassPathResource(
							resourceToLoad, DroolsSessionFactory.class);
				} else {

					String ftpDir = System.getProperty("pics.ftpDir");
					if (ftpDir== null || ftpDir.length() == 0)
						ftpDir = "C:/temp";

					File file = new File(ftpDir);
					if (file.isDirectory()) {
						file = new File(file, resourceToLoad);
						if (file.isFile()) {
							resource = ResourceFactory.newFileResource(file);
						}
					}
				}

				ResourceType rt = null;

				try {
					rt = ResourceType.getResourceType(FileUtils.getExtension(
							resourceToLoad).toUpperCase());
				} catch (Exception problemFiguringOutType) {
					rt = ResourceType.DRL;
				}

				kbuilder.add(resource, rt);

				if (kbuilder.hasErrors()) {
					throw new RuntimeException(kbuilder.getErrors().toString());
				}
				kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
			}
		} catch (Exception e) {
			kbase = null;
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
