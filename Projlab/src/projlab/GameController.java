/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projlab;

import java.io.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import java.util.Map.Entry;

/**
 * FXML Controller class
 *
 * @author lengy
 */
public class GameController implements Initializable {
    
    //1. játékos, A, D és space gombbal irányítjuk
    private Orangutan o1;
    
    //2. játékos, bal nyíl, jobb nyíl, és enter gombbal irányítjuk
    private Orangutan o2;
    
    //1. játékos pillanatnyilag kiválasztott csempéje. Ide próbál átlépni az o1, ha a játékos megnyomja a space gombot.
    private Tile selectedTileO1;
    
    //2. játékos pillanatnyilag kiválasztott csempéje. Ide próbál átlépni az o2, ha a játékos megnyomja az enter gombot.
    private Tile selectedTileO2;
    
    @FXML
    private AnchorPane pGame;
    @FXML 
    private ImageView mapPic; 
    @FXML
    private Button bStart;
    @FXML
    private Text player1Points;
    @FXML
    private Text player2Points;
    @FXML
    private Text pl1Text;
    @FXML
    private Text pl2Text;

    private Map<String, Panda> pandas = new HashMap<String, Panda>();  
    private Map<String, Cupboard> cupboards = new HashMap<String, Cupboard>();
    private Map<String, GameMachine> gamemachines = new HashMap<String, GameMachine>();
    private Map<String, ChocoMachine> chocomachines = new HashMap<String, ChocoMachine>(); 
    private Map<String, Armchair> armchairs = new HashMap<String, Armchair>(); 
    
    private TreeMap<String, Tile> tiles = new TreeMap<String, Tile>();
    private Map<String, Text> texts = new HashMap<String, Text>();
    
    AnimationTimer timer = new AnimationTimer(){
            @Override
            public void handle(long now){
                    update();
            }
        };
   
    public String getTileName(Tile t0){

        for (Entry<String, Tile> entry : tiles.entrySet()) {
            if (entry.getValue() == t0) {
                return entry.getKey();
            }
        }
    return null;
    }
    
    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }//példa: String keyString = getKey(tiles, vmilyenTile);
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {}  
    
    public Object type(String s){
        int i = 0; 
        switch(s){
            case "jpanda":
                JingleFearPanda jfpanda = new JingleFearPanda();
                pandas.put(isIn(pandas, "JFP", i), jfpanda);
                return new JingleFearPanda();
            case "ppanda":
                PipingFearPanda pfpanda = new PipingFearPanda(); 
                pandas.put(isIn(pandas, "PFP", i), pfpanda);
                return new PipingFearPanda(); 
            case "spanda":
                SleepyPanda spanda = new SleepyPanda(); 
                pandas.put(isIn(pandas, "SP", i), spanda);
                return new SleepyPanda(); 
            case "orangutan":
                return new Orangutan();
            case "cupboard":
                Cupboard cupboard = new Cupboard(); 
                cupboards.put(isIn(cupboards, "CB", i), cupboard); 
                return new Cupboard();
            case "gamemachine":
                GameMachine gamemachine = new GameMachine(); 
                gamemachines.put(isIn(gamemachines, "GM", i), gamemachine); 
                return new GameMachine();
            case "chocomachine":
                ChocoMachine chocomachine = new ChocoMachine(); 
                chocomachines.put(isIn(chocomachines, "CM", i), chocomachine); 
                return new ChocoMachine(); 
            case "armchair":
                Armchair armchair = new Armchair(); 
                armchairs.put(isIn(armchairs, "AR", i), armchair);
                return new Armchair();
            case "exit":
                return new Exit();
            default:
                return null;
        }
    }
    
    public String isIn(Map<String, ?> m, String s, int i){
        if (m.containsKey(s+i)){
            i++;
            isIn(m, s+i, i);
        }
        return s+1;
    }
    
    //Ide kéne a pálya felépítési, az elemek inicializálása.
    public void createMap(){
        try{BufferedReader nbr = new BufferedReader(new FileReader("TileNames.txt"));
            BufferedReader cbr = new BufferedReader(new FileReader("TileConnect.txt"));
            BufferedReader tbr = new BufferedReader(new FileReader("TextConnect.txt"));
            BufferedReader pbr = new BufferedReader(new FileReader("Placing.txt"));
            
            String line = nbr.readLine();
                
            while(line != null){
                if(line.charAt(0)=='B') tiles.put(line, new BreakableTile());
                else if (line.charAt(0)=='T') tiles.put(line, new Tile());
                line = nbr.readLine();
            }
            
            line = cbr.readLine();
            while(line != null){
                String[] names = line.split(",");
                tiles.get(names[0]).addNeighbor(tiles.get(names[1]));
                line = cbr.readLine();
            }
            line = tbr.readLine();
            int idx = 1;
            while(line!=null) {
                String name = "Tile"+Integer.toString(idx);
                String[] koord = line.split(" ");
                texts.put(name, new Text(Integer.parseInt(koord[0]), Integer.parseInt(koord[1]),""));
                pGame.getChildren().add(texts.get(name));
                idx++;
                line = tbr.readLine();
            }
            
            line = pbr.readLine();
            while(line != null){
                String[] names = line.split(",");
                if (names.length < 3)
                    tiles.get(names[0]).setElement(type(names[1]));
                else
                    tiles.get(names[0]).setElement(new Exit(tiles.get(names[2])));
                line = pbr.readLine();
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    //Itt lesz a fő loop, a random dolgok meg ilyesmi.
    public void update(){
        
    }
    
    //Ez fut le, ha megnyomod a bStart gombot
    @FXML
    private void startGame(ActionEvent event) throws IOException {
        bStart.setVisible(false);
        mapPic.setVisible(true);
        pl1Text.setVisible(true);
        pl2Text.setVisible(true);
        player1Points.setVisible(true);
        player1Points.setVisible(true);
        createMap();
        pGame.getScene().setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A:
                    selectedTileO1 = o1.getTile().getLeftNeighborOf(selectedTileO1);
                    break;
                case D:
                    selectedTileO1 = o1.getTile().getLeftNeighborOf(selectedTileO1);
                    break;    
                case SPACE:
                    o1.move(o1.getTile().getNeighborIndex(selectedTileO1));
                    selectedTileO1 = o1.getTile().getNeighbor(0);
                    break;
                case LEFT:
                    selectedTileO2 = o2.getTile().getLeftNeighborOf(selectedTileO2);
                    break;
                case RIGHT:
                    selectedTileO2 = o2.getTile().getLeftNeighborOf(selectedTileO2);
                    break;
                case ENTER:
                    o2.move(o2.getTile().getNeighborIndex(selectedTileO2));
                    selectedTileO1 = o2.getTile().getNeighbor(0);
                    break;
                case ESCAPE:
                    System.exit(0);
                    break;
            }
        });
        
        //Elindítja a timert, megy az update loop
        timer.start();
    }
    
}
