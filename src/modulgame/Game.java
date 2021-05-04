/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.Random;
/**
 *
 * @author user
 */
public class Game extends Canvas implements Runnable{
    Window window;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    private int score = 0;
    private int score2 = 0;
    private int lama_waktu = 0;
    private String username;
    private String mode;
    private String jumlah_player;
    private int time;
    private int time2;
    private int rand;
    private int key;
    private int key2;
    private Audio audio;
    private Thread thread;
    private boolean running = false;
    private Handler handler;
    
    dbConnection dbcon = new dbConnection();
    
    public enum STATE{
        Game,
        GameOver
    };
    
    public STATE gameState = STATE.Game;
    
    public Game(String usrnm, String md, String pl){
        username = usrnm;
        mode = md;
        jumlah_player = pl;
        
        if(mode.equals("Easy")){
            time = 20;
        }
        else if(mode.equals("Normal")){
            time = 10;
        }
        else{
            time = 5;
        }
        
        // System.out.println("mode = " + mode);
        window = new Window(WIDTH, HEIGHT, "TP5PBO2021 | Sarah Hanifah | 1909331", this);
        
        handler = new Handler();
        
        this.addKeyListener(new KeyInput(handler, this));
        
        if(gameState == STATE.Game){
            handler.addObject(new Items(100,150, ID.Item));
            handler.addObject(new Items(200,350, ID.Item));
            handler.addObject(new Player(200,200, ID.Player)); //player 1
            if(jumlah_player.equals("2 Player")){
                handler.addObject(new Player(400,400, ID.Player2)); //player 2
            }
            handler.addObject(new Musuh(300,300, ID.Musuh)); //enemy
        }
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        rand = time;
        key = 1;
        
        // playSound("/BGM.wav");
        audio = new Audio();
        audio.init("/BGM.wav");
        audio.play();
        
        if(mode.equals("Easy")){
            key2 = 3;
        }
        else if(mode.equals("Normal")){
            key2 = 2;
        }
        else{
            key2 = 1;
        }
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running){
                render();
                frames++;
            }
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                //System.out.println("FPS: " + frames);
                frames = 0;
                if(gameState == STATE.Game){
                    if(time>0){
                        time--;
                        lama_waktu++;
                    }else{
                        audio.stop();
                        dbcon.submit_data(username, score, lama_waktu, score+lama_waktu);
                        gameState = STATE.GameOver;
                    }
                }
            }
        }
        stop();
    }
    
    private void tick(){
        handler.tick();
        int speed = 0;
        int key3;
        if(mode.equals("Easy")){
            speed = 1;
            key3 = 3;
        }
        else if(mode.equals("Normal")){
            speed = 5;
            key3 = 2;
        }
        else{
            speed = 11;
            key3 = 1;
        }
        for(int i=0;i< handler.object.size(); i++){
            if(handler.object.get(i).getId() == ID.Musuh){
                java.util.Random x = new java.util.Random();
                // System.out.println("time : " + time + " r: " + rand + " key: " + key + " key2: " + key2 );
                
                // perpindahan musuh, terdapat 8 pergerakan
                if(key == 1){
                   handler.object.get(i).setVel_x(+speed); 
                   if(time == rand-key2){
                       key2 = key3;
                       rand = time;
                       key = x.nextInt(8) + 1;
                       if(key == 1) {
                           key = 3;
                       }
                   }
                }
                else if(key == 2){
                   handler.object.get(i).setVel_x(-speed); 
                   if(time == rand-key2){
                       key2 = key3;
                       rand = time;
                       key = x.nextInt(8) + 1;
                       if(key == 2) {
                           key = 1;
                       }
                   }
                }
                else if(key == 3){
                   handler.object.get(i).setVel_y(+speed); 
                   if(time == rand-key2){
                       key2 = key3;
                       rand = time;
                       key = x.nextInt(8) + 1;
                       if(key == 3) {
                           key = 4;
                       }
                   }
                }
                else if(key == 4){
                   handler.object.get(i).setVel_y(-speed); 
                   if(time == rand-key2){
                       key2 = 3;
                       rand = time;
                       key = x.nextInt(8) + 1;
                       if(key == 4) {
                           key = 2;
                       }
                   }
                }
                else if(key == 5){
                   handler.object.get(i).setVel_x(+speed);
                   handler.object.get(i).setVel_y(+speed); 
                   if(time == rand-key2){
                       key2 = key3;
                       rand = time;
                       key = x.nextInt(8) + 1;
                       if(key == 5) {
                           key = 3;
                       }
                   }
                }
                else if(key == 6){
                   handler.object.get(i).setVel_x(-speed); 
                   handler.object.get(i).setVel_y(-speed); 
                   if(time == rand-key2){
                       key2 = key3;
                       rand = time;
                       key = x.nextInt(8) + 1;
                       if(key == 6) {
                           key = 1;
                       }
                   }
                }
                else if(key == 7){
                   handler.object.get(i).setVel_x(-speed);
                   handler.object.get(i).setVel_y(+speed); 
                   if(time == rand-key2){
                       key2 = key3;
                       rand = time;
                       key = x.nextInt(8) + 1;
                       if(key == 7) {
                           key = 4;
                       }
                   }
                }
                else if(key == 8){
                   handler.object.get(i).setVel_x(+speed);
                   handler.object.get(i).setVel_y(-speed); 
                   if(time == rand-key2){
                       key2 = 3;
                       rand = time;
                       key = x.nextInt(8) + 1;
                       if(key == 8) {
                           key = 2;
                       }
                   }
                }
            }
        }
        if(gameState == STATE.Game){
            GameObject playerObject = null;
            GameObject playerObject2 = null;
            for(int i=0;i< handler.object.size(); i++){
                if(handler.object.get(i).getId() == ID.Player){
                   playerObject = handler.object.get(i);
                }
                if(handler.object.get(i).getId() == ID.Player2){
                   playerObject2 = handler.object.get(i);
                }
            }
            if(playerObject != null){
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Item){
                        if(checkCollision(playerObject, handler.object.get(i))){
                            playSound("/Eat.wav");
                            handler.removeObject(handler.object.get(i));
                            // tambah item posisi random
                            java.util.Random x = new java.util.Random();
                            handler.addObject(new Items(x.nextInt(740) + 20 ,x.nextInt(540) + 20, ID.Item));
                            
                            // tambah waktu dan score random
                            score2 = x.nextInt(15) + 1;
                            score = score + score2;
                            time2 = x.nextInt(10) + 1;
                            time = time + time2;
                            rand = time;
                            key2--;
                            break;
                        }
                    }
                    if(handler.object.get(i).getId() == ID.Musuh){
                        if(checkCollision(handler.object.get(i), playerObject)){
                            playSound("/Dead.wav");
                            handler.removeObject(playerObject);
                            // game selesai
                            time = 0;
                            break;
                        }
                    }
                }
            }
            
            
            if(playerObject2 != null){
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Item){
                        if(checkCollision(playerObject2, handler.object.get(i))){
                            playSound("/Eat.wav");
                            handler.removeObject(handler.object.get(i));
                            // tambah item posisi random
                            java.util.Random x = new java.util.Random();
                            handler.addObject(new Items(x.nextInt(740) + 20 ,x.nextInt(540) + 20, ID.Item));
                            
                            // tambah waktu dan score random
                            score2 = x.nextInt(15) + 1;
                            score = score + score2;
                            time2 = x.nextInt(10) + 1;
                            time = time + time2;
                            rand = time;
                            key2--;
                            break;
                        }
                    }
                    if(handler.object.get(i).getId() == ID.Musuh){
                        if(checkCollision(handler.object.get(i), playerObject2)){
                            playSound("/Dead.wav");
                            handler.removeObject(playerObject2);
                            // game selesai
                            time = 0;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public static boolean checkCollision(GameObject player, GameObject item){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeItem = 20;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int itemLeft = item.x;
        int itemRight = item.x + sizeItem;
        int itemTop = item.y;
        int itemBottom = item.y + sizeItem;
        
        if((playerRight > itemLeft ) &&
        (playerLeft < itemRight) &&
        (itemBottom > playerTop) &&
        (itemTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        
        g.setColor(Color.decode("#F1f3f3"));
        g.fillRect(0, 0, WIDTH, HEIGHT);
                
        
        
        if(gameState ==  STATE.Game){
            handler.render(g);
            
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), 20, 20);
            g.setColor(Color.decode("#377537"));
            g.drawString("           +" +Integer.toString(score2), 20, 40);

            g.setColor(Color.BLACK);
            g.drawString("Time: " +Integer.toString(time), WIDTH-120, 20);
            g.setColor(Color.decode("#377537"));
            g.drawString("         +" +Integer.toString(time2), WIDTH-120, 40);
            g.setColor(Color.BLACK);
            g.drawString("         " + Integer.toString(lama_waktu), WIDTH-120, 60);
        }else{
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", WIDTH/2 - 120, HEIGHT/2 - 30);

            currentFont = g.getFont();
            Font newScoreFont = currentFont.deriveFont(currentFont.getSize() * 0.5F);
            g.setFont(newScoreFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), WIDTH/2 - 50, HEIGHT/2 - 10);
            
            g.setColor(Color.BLACK);
            g.drawString("Press Space to Continue", WIDTH/2 - 100, HEIGHT/2 + 30);
        }
                
        g.dispose();
        bs.show();
    }
    
    public static int clamp(int var, int min, int max){
        if(var >= max){
            return var = max;
        }else if(var <= min){
            return var = min;
        }else{
            return var;
        }
    }
    
    public void close(){
        window.CloseWindow();
    }
    
    public void playSound(String filename){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    
    }
    public int getScore(){
        return score;
    }
}
