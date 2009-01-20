package com.picsauditing.servlet.upload;

public class UploadProcessorFactory {
	public static final int PREQUAL_OSHA = 0;
	public static final int CONTRACTOR = 2;
	public static final int FORM = 3;
	public static final int PQF = 4;
	public static final int OPERATOR = 5;

	public Uploadable getUploadProcessor(int i){
		Uploadable uploadable = null;
		switch(i){
			case PREQUAL_OSHA:
				uploadable = new Prequal_OSHAProcessor();				
				break;
			case CONTRACTOR:
				uploadable = new ContractorProcessor();
				break;
			case FORM:
				uploadable = new FormProcessor();
				break;
			case PQF:
				uploadable = new PQFProcessor();
				break;
			case OPERATOR:
				uploadable = new OperatorProcessor();
				break;
			default:
				uploadable = new SimpleUploadProcessor();
		}
		return uploadable;
	}
} 