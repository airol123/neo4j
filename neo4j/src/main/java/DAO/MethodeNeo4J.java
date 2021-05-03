package DAO;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

/**
 * @author RRH
 */
public class MethodeNeo4J {

    public static void chercherInfo() {
        Driver driver = ConnexionNeo4J.connectDB();
        Session session = driver.session();
        Result result = session.run("Match (s:Store{store_sk:'1'})"
                + "return id(s) as id");
        while (result.hasNext()) {
            Record record = result.next();
            System.out.println(record.get("id").asInt());
        }
        System.out.println("ok");

        session.close();
        driver.close();

    }

    public static void creerSousGraph() {
        try (Driver driver = ConnexionNeo4J.connectDB(); Session session = driver.session()) {
            String query = "CALL gds.graph.create.cypher("
                    + "    'graph-Store-SS_Promotion-Promotion-WS_Promotion-Web_Page-WS_Web_Site_Web_Page-Web_Site',"
                    + "    'MATCH (s:Store)-[sp:SS_Promotion]-(p:Promotion)-[wpm:WS_Promotion]-(wp:Web_Page)-[wsb:WS_Web_Site_Web_Page]-(wn:Web_Site) WHERE date(p.start_date_sk)<date(\"1996-04\") AND (date(p.end_date_sk)>=date(\"1996-03\") or date(p.end_date_sk) is null) RETURN DISTINCT id(s) as id,labels(s) as labels union  MATCH (s:Store)-[sp:SS_Promotion]-(p:Promotion)-[wpm:WS_Promotion]-(wp:Web_Page)-[wsb:WS_Web_Site_Web_Page]-(wn:Web_Site) WHERE date(p.start_date_sk)<date(\"1996-04\") AND (date(p.end_date_sk)>=date(\"1996-03\") or date(p.end_date_sk) is null) RETURN DISTINCT id(p) as id,labels(p) as labels union MATCH (s:Store)-[sp:SS_Promotion]-(p:Promotion)-[wpm:WS_Promotion]-(wp:Web_Page)-[wsb:WS_Web_Site_Web_Page]-(wn:Web_Site) WHERE date(p.start_date_sk)<date(\"1996-04\") AND (date(p.end_date_sk)>=date(\"1996-03\") or date(p.end_date_sk) is null) RETURN DISTINCT id(wp) as id,labels(wp) as labels union MATCH (s:Store)-[sp:SS_Promotion]-(p:Promotion)-[wpm:WS_Promotion]-(wp:Web_Page)-[wsb:WS_Web_Site_Web_Page]-(wn:Web_Site) WHERE date(p.start_date_sk)<date(\"1996-04\") AND (date(p.end_date_sk)>=date(\"1996-03\") or date(p.end_date_sk) is null) RETURN DISTINCT id(wn) as id,labels(wn) as labels ',"
                    + "    'MATCH (s:Store)-[sp:SS_Promotion]-(p:Promotion)-[wpm:WS_Promotion]-(wp:Web_Page)-[wsb:WS_Web_Site_Web_Page]-(wn:Web_Site) WHERE date(p.start_date_sk)<date(\"1996-04\") AND (date(p.end_date_sk)>=date(\"1996-03\") or date(p.end_date_sk) is null) RETURN DISTINCT id(s) as source,id(p) as target,type(sp) as type, sp.distance as distance union  MATCH (s:Store)-[sp:SS_Promotion]-(p:Promotion)-[wpm:WS_Promotion]-(wp:Web_Page)-[wsb:WS_Web_Site_Web_Page]-(wn:Web_Site) WHERE date(p.start_date_sk)<date(\"1996-04\") AND (date(p.end_date_sk)>=date(\"1996-03\") or date(p.end_date_sk) is null) RETURN DISTINCT id(p) as source,id(wp) as target,type(wpm) as type, wpm.distance as distance union MATCH (s:Store)-[sp:SS_Promotion]-(p:Promotion)-[wpm:WS_Promotion]-(wp:Web_Page)-[wsb:WS_Web_Site_Web_Page]-(wn:Web_Site) WHERE date(p.start_date_sk)<date(\"1996-04\") AND (date(p.end_date_sk)>=date(\"1996-03\") or date(p.end_date_sk) is null) RETURN DISTINCT id(wp) as source,id(wn) as target,type(wsb) as type, wsb.distance as distance',"
                    + "    {validateRelationships:false}"
                    + ")";
            session.run(query);
            System.out.println("ok");
        }
    }

    public static void minimum_tree_write() {
        try (Driver driver = ConnexionNeo4J.connectDB(); Session session = driver.session()) {
            String query = "MATCH (n:Place {id: 'D'}) " +
                    "CALL gds.alpha.spanningTree.minimum.write({ " +
                    "  nodeProjection: 'Place', " +
                    "  relationshipProjection: { " +
                    "    LINK: { " +
                    "      type: 'LINK', " +
                    "      properties: 'cost', " +
                    "      orientation: 'UNDIRECTED' " +
                    "    } " +
                    "  }, " +
                    "  startNodeId: id(n), " +
                    "  relationshipWeightProperty: 'cost', " +
                    "  writeProperty: 'MINST', " +
                    "  weightWriteProperty: 'writeCost' " +
                    "}) " +
                    "YIELD createMillis, computeMillis, writeMillis, effectiveNodeCount " +
                    "RETURN createMillis, computeMillis, writeMillis, effectiveNodeCount;";
            session.run(query);
            System.out.println("ok");
        }
    }

    public static void minimum_tree() {
        try (Driver driver = ConnexionNeo4J.connectDB(); Session session = driver.session()) {
            minimum_tree_write();
            String query = "MATCH path = (n:Place {id: 'D'})-[:MINST*]-() " +
                    "WITH relationships(path) AS rels " +
                    "UNWIND rels AS rel " +
                    "WITH DISTINCT rel AS rel " +
                    "RETURN startNode(rel).id AS source, endNode(rel).id AS destination, rel.writeCost AS cost";
            Result result = session.run(query);

            while (result.hasNext()) {
                Record record = result.next();
                //int source = record.get("source").asInt();
                String source = record.get("source").asString();

                System.out.println("sourcececec : " + source);
                //String labels = record.get("label_source").get(0).asString();
                //System.out.println("labelss : " + labels);
                //int destination = record.get("destination").asInt();
                String destination = record.get("destination").asString();

                //String labeld = record.get("label_destination").get(0).asString();
                int cost = record.get("cost", 0);
                //           System.out.println("source : " + source + " label_source : " + labels + " destination : " + destination + " labeld : " + labeld + " cost : " + record.get("cost"));

                System.out.println("source : " + source + " destination : " + destination + " cost : " + record.get("cost"));
            }

        }
    }

    public static String getLabel(int id) {
        try (Driver driver = ConnexionNeo4J.connectDB(); Session session = driver.session()) {
            String query = "match(n) where id(n)=" + id + " return labels(n) as label ";
            Result result = session.run(query);
            String label = null;
            while (result.hasNext()) {
                Record record = result.next();
                // System.out.println(record.get("label").get(0));
                label = record.get("label").get(0).asString();
            }
            return label;
        }
    }

    public static void adamicAdar(String label) {

        try (Driver driver = ConnexionNeo4J.connectDB(); Session session = driver.session()) {
            ArrayList<Integer> nodes = getLabelNodes(label);
            double max = 0d;
            int id1 = 0;
            int id2 = 0;
            for (int i = 0; i < nodes.size(); i++) {

                for (int j = i; j < nodes.size(); j++) {
                    //double score = adamicAdar(nodes.get(i), nodes.get(j));
                    String query = "match(s1) where id(s1)=" + nodes.get(i)
                            + "match(s2) where id(s2)=" + nodes.get(j)
                            + "RETURN gds.alpha.linkprediction.adamicAdar(s1, s2) AS score ";
                    Result result = session.run(query);
                    while (result.hasNext()) {
                        Record record = result.next();
                        if (max < record.get("score").asDouble()) {
                            max = record.get("score").asDouble();
                            id1 = nodes.get(i);
                            id2 = nodes.get(j);
                        }
                    }
                }
                double result = (double) i / (double) nodes.size();
                DecimalFormat df = new DecimalFormat("0.00%");
                String r = df.format(result);
                System.out.println("progress>>>>>>>>>>>" + r);
            }
            System.out.println("id1 : " + id1 + " id2 : " + id2 + "max : " + max);

            //return max;
        }

    }

    public static void adamicAdar_bis(String label1, String label2) {

        try (Driver driver = ConnexionNeo4J.connectDB(); Session session = driver.session()) {
            ArrayList<Integer> nodes1 = getLabelNodes(label1);
            ArrayList<Integer> nodes2 = getLabelNodes(label2);

            double max = 0d;
            int id1 = 0;
            int id2 = 0;
            for (int i = 0; i < nodes1.size(); i++) {

                for (int j = 0; j < nodes2.size(); j++) {
                    //double score = adamicAdar(nodes.get(i), nodes.get(j));
                    String query = "match(s1) where id(s1)=" + nodes1.get(i)
                            + "match(s2) where id(s2)=" + nodes2.get(j)
                            + "RETURN gds.alpha.linkprediction.adamicAdar(s1, s2) AS score ";
                    Result result = session.run(query);
                    while (result.hasNext()) {
                        org.neo4j.driver.Record record = result.next();
                        if (max < record.get("score").asDouble()) {
                            max = record.get("score").asDouble();
                            id1 = nodes1.get(i);
                            id2 = nodes2.get(j);
                        }
                    }
                }
                double result = (double) i / (double) nodes1.size();
                DecimalFormat df = new DecimalFormat("0.00%");
                String r = df.format(result);
                System.out.println("progress>>>>>>>>>>>" + r);
            }
            System.out.println("id1 : " + id1 + " id2 : " + id2 + "max : " + max);

            //return max;
        }
    }

    public static ArrayList<Integer> getLabelNodes(String label) {
        try (Driver driver = ConnexionNeo4J.connectDB(); Session session = driver.session()) {
            //Mettre la première lettre en majuscule
            char[] cs = label.toCharArray();
            if (cs[0] >= 97) {
                cs[0] -= 32;
            }
            label = String.valueOf(cs);

            String query = "match(n:" + label + ") return id(n) as node";
            Result result = session.run(query);
            ArrayList<Integer> nodes = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                nodes.add(record.get("node").asInt());
            }

            return nodes;
        }

    }

    public static void main(String[] args) throws Exception {
//minimum_tree_write();        
        minimum_tree();
        //adamicAdar_bis("web_Page", "web_Site");  //if have _, the little after _ needs in capital
        //System.out.println("Score : " + adamicAdar(1092, 1093));
        /*
        ArrayList<Integer> nodes = getLabelNodes("store");
        System.out.println("nodes:" + nodes);
        double max = 0d;
        int id1 = 0;
        int id2 = 0;
        for (int i = 0; i < nodes.size() - 41; i++) {
        
        for (int j = i; j < nodes.size() - 37; j++) {
        double score = adamicAdar(nodes.get(i), nodes.get(j));
        if (score > max) {
        max = score;
        id1 = nodes.get(i);
        id2 = nodes.get(j);
        }
        }
        }
        System.out.println("id1 : " + id1 + " id2 : " + id2 + "max : " + max);*/
        System.out.println("----------------End--------------");
        // System.out.println("!!!!!label"+ getLabel(1093));//1092  1093
    }

}
