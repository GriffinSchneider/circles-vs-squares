package circlesvssquares;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.jbox2d.common.Vec2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import circlesvssquares.Enemy.EnemyType;

import pbox2d.PBox2D;

public class LevelEditor {
    private static String lastFileName;
    
    // Some nastyness to prevent "References to generic type HashMap should be parameterized"
    // warnings in saveLevel. This way, we don't need to @SuppressWarnings("unchecked") the
    // entire method and possibly lose some real warnings.
    @SuppressWarnings("unchecked")
    private static Map<Object,Object> asMap(JSONObject j) {
        return j;
    } 
    @SuppressWarnings("unchecked")
    private static ArrayList<Object> asArrayList(JSONArray j) {
        return j;
    } 
    
    public static void saveLevel(ArrayList<Box2DObjectNode> objectList) {
        JSONArray level = new JSONArray();
        // Save objects
        for (int i = objectList.size()-1; i >=0; i--) {
            Box2DObjectNode n = objectList.get(i);
            if (n.getClass() != Bullet.class) {
                JSONObject object = new JSONObject();
                Vec2 pos = n.getPhysicsPosition();
                asMap(object).put("x", new Float(pos.x));
                asMap(object).put("y", new Float(pos.y));
                asMap(object).put("class", n.getClass().toString());

                if (n.getClass() == Ground.class) {
                    Ground g = (Ground) n;
                    asMap(object).put("w", new Float(g.w));
                    asMap(object).put("h", new Float(g.h));
                }
                else if (n.getClass() == Enemy.class) {
                    Enemy e = (Enemy) n;
                    asMap(object).put("w", new Float(e.w));
                    asMap(object).put("h", new Float(e.h));
                    asMap(object).put("type", e.getType().name());
                }
                else if (n.getClass() == EndPoint.class) {
                    EndPoint e = (EndPoint) n;
                    asMap(object).put("r", new Float(e.r));
                }

                asArrayList(level).add(object);
            }
        }

        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) { 
            e.printStackTrace();  
        } 
 
        // create a file chooser 
        JFileChooser fc; 
        if (lastFileName == null)
            fc = new JFileChooser("levels/"); 
        else {
            fc = new JFileChooser(lastFileName); 
        }
 
        // in response to a button click: 
        int returnVal = fc.showSaveDialog(null); 
 
        if (returnVal == JFileChooser.APPROVE_OPTION) { 
            File file = fc.getSelectedFile(); 
            lastFileName = file.getAbsolutePath();
            try {
                FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
                fileWriter.write(level.toJSONString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void loadLevel(PBox2D box2d) {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) { 
            e.printStackTrace();  
        } 
 
        // create a file chooser 
        JFileChooser fc; 
        if (lastFileName == null)
            fc = new JFileChooser("levels/"); 
        else {
            fc = new JFileChooser(lastFileName); 
        }
 
        // in response to a button click: 
        int returnVal = fc.showOpenDialog(null); 
 
        if (returnVal == JFileChooser.APPROVE_OPTION) { 
            File file = fc.getSelectedFile(); 
            lastFileName = file.getAbsolutePath();
            loadLevel(file.getAbsolutePath(), box2d);
        }
    }

    public static void loadLevel(String slevel, PBox2D box2d) {
        JSONParser parser = new JSONParser();

        try {
            Object list = parser.parse(new FileReader(slevel));
            JSONArray level = (JSONArray) list;
            for (Object obj : level.toArray()) {
                JSONObject node =  (JSONObject) obj;

                String sClass = (String) node.get("class");
                float x = ((Double) node.get("x")).floatValue();
                float y = ((Double) node.get("y")).floatValue();
                if (sClass.equals(Ground.class.toString())) {
                    float w = ((Double) node.get("w")).floatValue();
                    float h = ((Double) node.get("h")).floatValue();

                    new Ground(new Vec2(x, y), w, h, box2d);
                }
                else if (sClass.equals(Enemy.class.toString())) {
                    float w = ((Double) node.get("w")).floatValue();
                    float h = ((Double) node.get("h")).floatValue();

                    String stype = (String) node.get("type");
                    EnemyType type = EnemyType.Simple;
                    // Older maps don't have an enemyType
                    if (stype != null) type = EnemyType.valueOf(stype);
                    
                    switch(type) {
                    default:
                    case Simple:
                        Enemy.createSimple(new Vec2(x, y), box2d);
                        break;
                    case Cluster:
                        Enemy.createCluster(new Vec2(x, y), box2d);
                        break;
                    }
                }
                else if (sClass.equals(EndPoint.class.toString())) {
                    float r = ((Double) node.get("r")).floatValue();

                    new EndPoint(new Vec2(x, y), r, box2d);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
