package com.picsauditing.jpa.entities;

public class NcmsCategory {
	private String name;
	private String status;

	public NcmsCategory(String name, String status) {
		this.name = name;
		this.status = status;
	}
	
	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	static public String[] columns = { "Asbestos", "Benzene", "Bloodborne Pathogens", "Confined Space", "Crane Operator", "Disciplinary",
			"Diving", "Electrical", "Electrical-Nonqualified", "Fall Protection", "Fire Protection", "First Aid",
			"Forklift", "Ground Fault", "H2S", "Hazcom", "Hazwoper Emergency Response", "Hazwoper RCRA", "Lead",
			"Lifting and Cranes", "Lockout/Tagout", "Noise", "Norm", "PPE", "PSM", "Respiratory", "Rigging",
			"Sandblasting", "Scaffold User", "Surface and Subsurface", "Trenching", "Water Survival", "Welding",
			"Well Control" };
}
