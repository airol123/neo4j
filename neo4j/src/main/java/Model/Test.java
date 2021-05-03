/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import DAO.ConnexionNeo4J;

import java.util.ArrayList;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.graphdb.Node;

/**
 *
 * @author RuohanREN.
 */
public class Test {

   

    public static void getAllNodes() {
        try ( Driver driver = ConnexionNeo4J.connectDB();  Session session = driver.session()) {
            ArrayList<Integer> nodes = new ArrayList<>();
            String query = "match (n) return id(n) as node";
            Result result = session.run(query);
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                nodes.add(record.get("node", -1));
            }

        }
    }

public static void getLabelNodes(String label) {
        try ( Driver driver = ConnexionNeo4J.connectDB();  Session session = driver.session()) {
            //Mettre la première lettre en majuscule
            char[] cs = label.toCharArray();
            cs[0] -= 32;
            label = String.valueOf(cs);

            String query = "match(n:" + label + ") return n as node limit 1";
            Result result = session.run(query);
           //ArrayList<Integer> nodes = new ArrayList<>();
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                System.out.println("testtest: "+record.get("node"));
            }

            //return nodes;
        }

    }
    public static void main(String[] args) {
        getLabelNodes("store");
    }
}
