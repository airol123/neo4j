import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

public class Neo4jCypherJavaAPI {
    public static void main(String[] args) {
        //指定 Neo4j 存储路径
        File file = new File("C:\\Users\\carol\\.Neo4jDesktop\\relate-data\\dbmss\\dbms-2117c5c0-9ede-4521-93f9-f74a9b40cac9\\data\\databases\\graph1bis.db");
        //Create a new Object of Graph Database
        GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(file);
        System.out.println("Server is up and Running");

        try(Transaction tx = graphDB.beginTx()){
            //通过Cypher查询获得结果
            StringBuilder sb = new StringBuilder();
            sb.append("MATCH (s:Store) WHERE s.store_sk='1'  ");
            sb.append("RETURN keys(s) as schema");
            Result result = graphDB.execute(sb.toString());
            //遍历结果
            while(result.hasNext()){
                //get("movie")和查询语句的return movie相匹配
                Node store = (Node) result.next().get("schema");
                System.out.println(store.getId() + " : " + store.getProperty("store_sk"));
            }

            tx.success();
            System.out.println("Done successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            graphDB.shutdown();    //关闭数据库
        }
    }
}