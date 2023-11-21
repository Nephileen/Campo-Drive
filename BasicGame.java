import java.awt.*;
import java.awt.event.*;
import java.util.*;

//A Simple version of the scrolling game, featuring Avoids, Gets, and RareGets
//Players must reach a score threshold to win
//If player runs out of HP (via too many Avoid collisions) they lose
public class BasicGame extends ScrollingGameEngine {
    
    //Dimensions of game window
    protected static final int DEFAULT_WIDTH = 900;
    protected static final int DEFAULT_HEIGHT = 600;  
    
    //Starting Player coordinates
    protected static final int STARTING_PLAYER_X = 20;
    protected static final int STARTING_PLAYER_Y = 100;
    
    //Score needed to win the game
    protected static final int SCORE_TO_WIN = 300;
    
    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    protected static final int MAX_GAME_SPEED = 300;
    //Interval that the speed changes when pressing speed up/down keys
    protected static final int SPEED_CHANGE = 20;    
    
    protected static final String INTRO_SPLASH_FILE = "assets/splash.gif";        
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;
    
    //Interval that Entities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    protected static final int SPAWN_INTERVAL = 45;
    
    
    //A Random object for all your random number generation needs!
    protected static final Random rand = new Random();
    
    
    
    
    
    //Player's current score
    protected int score;


    
    //Stores a reference to game's Player object for quick reference
    //(This Player will also be in the displayList)
    protected Player player;
    

    
    public BasicGame(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public BasicGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }
    
    
    //Performs all of the initialization operations that need to be done before the game starts
    protected void pregame(){
        this.setBackgroundColor(Color.PINK);
        player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        displayList.add(player); 
        setSplashImage(INTRO_SPLASH_FILE);
        score = 0;
    }
    
    
    //Called on each game tick
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities();
        for (int i = 1; i < displayList.size(); i++){
            if (displayList.get(i).isCollidingWith(player)){
                handlePlayerCollision((Consumable)displayList.get(i));  
            }
        }
        //Spawn new entities only at a certain interval
        if (ticksElapsed % SPAWN_INTERVAL == 0){            
            spawnEntities(); //how many enties to spwn 
            garbageCollectOffscreenEntities();
        }
        //Update the title text on the top of the window
        setTitleText("REMAINING LIVES: " + player.getHP() + "  SCORE: " + score); // + "ticks:" + ticksElapsed     
    }
    
    
    //Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities(){
        for (int i = 1; i < displayList.size(); i++){
            Entity image = displayList.get(i); 
            ((Scrollable)image).scroll(); 
        }
    }
    
    
    //Handles "garbage collection" of the displayList
    //Removes entities from the displayList that have scrolled offscreen
    //(i.e. will no longer need to be drawn in the game window).
    protected void garbageCollectOffscreenEntities(){

        for (int i = 1; i < displayList.size(); i++){
            Entity image = displayList.get(i); 

            if (image.getX() < -1 * image.getWidth()){ //image is off the screen when its negative 
                displayList.remove(i); 
            }
        }
       
    }
    
    
    //Called whenever it has been determined that the Player collided with a consumable
    protected void handlePlayerCollision(Consumable collidedWith){ 
    
            score = score + collidedWith.getPointsValue();
            player.setHP(collidedWith.getDamageValue() + player.getHP()); 
            displayList.remove(collidedWith); 
    }
    
    
    //Spawn new Entities on the right edge of the game board
    protected void spawnEntities(){

        ArrayList <Entity> checkList = new ArrayList <Entity>(); 
        for (int i = 0; i < rand.nextInt(0,4); i++ ){ 
            Entity generic;
            int randNum = rand.nextInt(1, 100); 
            
            if(randNum <= 40){ //40% 
                generic = new Avoid (getWindowWidth(), 0); 
                generic.setY(rand.nextInt(getWindowHeight() - generic.getHeight()));
            } else if (randNum > 40 && randNum <= 80){ //40% 
                generic = new Get (getWindowWidth(), 0); 
                generic.setY(rand.nextInt(getWindowHeight() - generic.getHeight()));
             } else {
                generic = new RareGet (getWindowWidth(), 0); 
                generic.setY(rand.nextInt(getWindowHeight() - generic.getHeight()));
            }
            ifCollision(checkList, generic);
        }    
    }

    protected void ifCollision(ArrayList <Entity> checkList, Entity check){
            for (int j = 0; j<checkList.size(); j++){
                if(check.isCollidingWith(checkList.get(j))){
                    return;
                }
            } 
            this.displayList.add(check); 
            checkList.add(check);

    }
    
    
    //Called once the game is over, performs any end-of-game operations
    protected void postgame(){ 
        if (isGameOver() == true){
            isPaused = true; 
        } if (this.score == SCORE_TO_WIN){
            super.setTitleText("GAME OVER! - You Won!");    
        } else {
            super.setTitleText("GAME OVER! - You Lost!");  
        }
    }
    
    
    //Determines if the game is over or not
    //Game can be over due to either a win or lose state
    protected boolean isGameOver(){ 
        if (player.getHP() == 0  || this.score == SCORE_TO_WIN){
            return true; 
        }
        return false;   
    }
    
    
    
    //Reacts to a single key press on the keyboard
    protected void reactToKey(int key){
        setDebugText("Key Pressed!: " + KeyEvent.getKeyText(key) + ",  DisplayList size: " + displayList.size());
       
        //if a splash screen is active, only react to the "advance splash" key... nothing else!
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
                return;
        }

        if (isPaused){
            if(key == KEY_PAUSE_GAME){
                isPaused = !(isPaused); 
            }
            return; 
        }

        //why down and up is switched??? QUESTION
        if(key == RIGHT_KEY && (player.getX()+ player.getWidth() < getWindowWidth())){
            player.setX(player.getX()+player.getMovementSpeed()); 
        }else if (key == LEFT_KEY && (player.getX()> 0)){
            player.setX(player.getX() - player.getMovementSpeed()); 
        } else if (key == DOWN_KEY && (player.getY()+ player.getHeight() < getWindowHeight())){
            player.setY(player.getY()+player.getMovementSpeed()); 
        } else if (key == UP_KEY && (player.getY()> 0)){
            player.setY(player.getY()-player.getMovementSpeed()); 
        } else if(key == KEY_PAUSE_GAME){
            isPaused = true;
        } else if (key == SPEED_UP_KEY && getGameSpeed() + SPEED_CHANGE < MAX_GAME_SPEED){
            setGameSpeed(getGameSpeed() + SPEED_CHANGE);
        } else if (key == SPEED_DOWN_KEY && getGameSpeed() - SPEED_CHANGE > 0){ //var
            setGameSpeed(getGameSpeed() - SPEED_CHANGE);
        } 
    }    
    
    
    //Handles reacting to a single mouse click in the game window
    //Won't be used in Simple Game... you could use it in Creative Game though!
    protected MouseEvent reactToMouseClick(MouseEvent click){
        if (click != null){ //ensure a mouse click occurred
            int clickX = click.getX();
            int clickY = click.getY();
            setDebugText("Click at: " + clickX + ", " + clickY);
        }
        return click;//returns the mouse event for any child classes overriding this method
    } 
}
