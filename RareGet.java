//A RareGet is a special kind of Get that spawns more infrequently than the regular Get
//When consumed, RareGets restores the Player's HP in addition to awarding points
//Otherwise, behaves the same as a regular Get
public class RareGet extends Get{

protected static final int RARE_WIDTH = 140; //75
   protected static final int RARE_HEIGHT = 85; //75
    
    //Location of image file to be drawn for a RareGet
    protected static final String RAREGET_IMAGE_FILE = "assets/rare_get.gif";
    
    public RareGet(){
        this(0, 0);        
    }
    
    public RareGet(int x, int y){ //how to chnage size?? 
        super(x, y, RARE_WIDTH, RARE_HEIGHT, RAREGET_IMAGE_FILE);  
    }


    public int getPointsValue(){

        return 20;
    }
    
    //Colliding with a Get does not affect the player's HP
    public int getDamageValue(){
        return 1;
    }
       
}
