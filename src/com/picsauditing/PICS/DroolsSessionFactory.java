package com.picsauditing.PICS;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public class DroolsSessionFactory {

	public DroolsSessionFactory() {
		//just once
		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();
		System.out.println("Constructing Drools Session Factory...");
	}
	
	protected KnowledgeAgent kagent = null;
	protected List<String> drlResources = new Vector<String>();

	public StatelessKnowledgeSession getStatelessSession() {
		if(kagent == null)
			setup();
		
		return kagent.newStatelessKnowledgeSession();
	}
	
	
	public StatefulKnowledgeSession getStatefulSession() {
		if( kagent == null )
			setup();
		return kagent.getKnowledgeBase().newStatefulKnowledgeSession();
	}

	public List<String> getDrlResources() {
		return drlResources;
	}

	public void setDrlResources(List<String> drlResources) {
		this.drlResources = drlResources;
	}

	public void reset() {
		kagent = null;
	}
	
	protected void setup() {

		try {
			kagent = KnowledgeAgentFactory.newKnowledgeAgent( "MyAgent");
			
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

				kagent.applyChangeSet(resource);
			}
		} catch (Exception e) {
			kagent = null;
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
