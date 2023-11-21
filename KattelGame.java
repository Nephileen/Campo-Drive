import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class KattelGame extends BasicGame{

    public static final int EASY = 1000; 
    public static final int MED = 2500; 
    private static final String BACKGROUND_IMAGE = "assets/background.gif"; 
    private static final String LOASTGAME_IMAGE = "assets/lostGameImage.gif"; 
    private static final String WINGAME_IMAGE = "assets/winGameImage.gif"; 



    protected void pregame(){
        setBackgroundImage(BACKGROUND_IMAGE);
        player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        displayList.add(player); 
        setSplashImage(INTRO_SPLASH_FILE);
        score = 0;
    }

    protected void updateGame(){
        super.updateGame();

        if (player.getY()+ player.getHeight() < getWindowHeight()){
            player.setY(player.getY()+(player.getMovementSpeed()/3)); 
        }
    }

    protected void postgame(){ 

        if (isGameOver() == true){
            isPaused = true; 
        } if (this.score == SCORE_TO_WIN){
            super.setTitleText("GAME OVER! - You Won!");   
            setSplashImage(WINGAME_IMAGE); 

        } else {
            super.setTitleText("GAME OVER! - You Lost!"); 
            setSplashImage(LOASTGAME_IMAGE); 
        }


    }
    



    protected void spawnEntities(){
        ArrayList <Entity> checkList = new ArrayList <Entity>(); 
        int avoidNum = 0;
        for (int i = 0; i < rand.nextInt(1,4); i++ ){ 
            Entity generic;
            int randNum = rand.nextInt(1, 100); 
         //40% 
            if((ticksElapsed <= EASY && avoidNum<1)|| ((ticksElapsed > EASY && ticksElapsed <= MED) && avoidNum < 2 )|| ticksElapsed > MED && avoidNum < 3){ 
                generic = new Avoid (getWindowWidth(), 0); 
                generic.setY(rand.nextInt(getWindowHeight() - generic.getHeight()));
                avoidNum++; 
                ifCollision(checkList, generic);
                
            }
               
            
            if (randNum <= 60){ //40% 
                generic = new Get (getWindowWidth(), 0); 
                generic.setY(rand.nextInt(getWindowHeight() - generic.getHeight()));
                ifCollision(checkList, generic);
             } else {
                if (player.getHP() == 1){
                    generic = new RareGet (getWindowWidth(), 0); 
                    generic.setY(rand.nextInt(getWindowHeight() - generic.getHeight()));
                    ifCollision(checkList, generic);
                }
 
            }
        }    
    }  

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

        if(key == RIGHT_KEY || key == LEFT_KEY){
            return; 
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

    protected void handlePlayerCollision(Consumable collidedWith){ 
      
        score = score + collidedWith.getPointsValue();
        if( !(collidedWith instanceof RareGet) || player.getHP() < 3){
        player.setHP(collidedWith.getDamageValue() + player.getHP());
        } 
        displayList.remove(collidedWith); 
}



}
