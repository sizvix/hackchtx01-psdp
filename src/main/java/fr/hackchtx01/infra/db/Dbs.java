package fr.hackchtx01.infra.db;

public enum Dbs {
	PSDP("psdp");
	
	private final String dbName;
	
	private Dbs(String dbName) {
		this.dbName = dbName;
	}
	
	public String getDbName() {
		return dbName;
	}
}
