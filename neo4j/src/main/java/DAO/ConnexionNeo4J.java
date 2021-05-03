
package DAO;

import org.neo4j.driver.*;



public class ConnexionNeo4J {
    
    
    public static Driver connectDB(){
        //Config noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
 Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic("neo4j", "1234"));
 return driver;
    }


    
    
}