java -cp hsqldb.jar org.hsqldb.util.ScriptTool -url jdbc:hsqldb:hsql://localhost/ -database eshop -user sa -script createEshopDbNoConstraints.sql