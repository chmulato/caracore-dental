# Compile the project
mvn compile

# Run BCryptUtil with Maven exec plugin (simple approach)
mvn exec:java "-Dexec.mainClass=com.caracore.cca.util.BCryptUtil" "-Dexec.cleanupDaemonThreads=false"
