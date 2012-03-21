package com.arvis.andeng;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.opengl.view.RenderSurfaceView;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.ui.activity.LayoutGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.HorizontalAlign;
import org.anddev.andengine.util.MathUtils;

import android.R.bool;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.input.touch.TouchEvent;


/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 02:26:05 - 08.07.2010
 */
//public class First extends BaseGameActivity implements SnakeConstants {
public class First extends LayoutGameActivity  {
	// ===========================================================
	// Constants
	// ===========================================================

//	private static final int CAMERA_WIDTH = 400;
//	private static final int CAMERA_HEIGHT = 800;

	private static final int CAMERA_WIDTH = 320;
	private static final int CAMERA_HEIGHT = 480;
	
	
	private static final int LAYER_COUNT = 4;

	private static final int LAYER_BACKGROUND = 0;
	private static final int LAYER_FOOD = LAYER_BACKGROUND + 1;
	private static final int LAYER_SNAKE = LAYER_FOOD + 1;
	private static final int LAYER_SCORE = LAYER_SNAKE + 1;
	
	private static final int HEART_COLOR_COUNT = 4;
	

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	private int spawnCount=0;
	

	private BitmapTextureAtlas mFontTexture;
	private Font mFont;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas[] heartsTextureAtlasArr;
	private TextureRegion[] heartsTextureReagionArr;
	
	private BitmapTextureAtlas mBackgroundTexture;
	private TextureRegion mBackgroundTextureRegion;

	private BitmapTextureAtlas[] menuButtonsTextureAtlas;
	private TextureRegion[] menuButtonsTextures;
	
	private BitmapTextureAtlas mMenuBackGround;
	
	protected TextureRegion mMenuResetTextureRegion;
	protected TextureRegion mMenuQuitTextureRegion;
	protected TextureRegion mMenuBackgroundTextureRegion; 

	private BitmapTextureAtlas gameOverBackGroundAtlas;
	private TextureRegion gameOverBackGround;
	
	private BitmapTextureAtlas gameOverTouchAtlas;
	private TextureRegion gameOverTouch;
	
	private Scene mScene;
	private Scene mMenuScene;
	private Sprite gameOverBack; 
	private Sprite touchToContinue; 
	
	public int mScore = 0;
	public int lives=3;
	public int gameLevel=1;
	int gameMode=0; // 0 catch all; 1- timed run; 2- catch based on colors
	int secondsLeft=30;
	int[] highScores = {0,0}; 
	
	int[] levelupScores = {0,5,15,45,70,100,200,300}; // when level is going up TODO: think how to implement this better
	int[] heartSpeedArr = {28,27,26,25,24,23,22,21,20,19,18,17,16};
	float[] spawnSpeedArr = {2,2,1.8f,1.7f,1.6f,1.4f,1.2f,1.0f,0.8f,0.6f,0.5f,0.3f,0.3f,0.3f,0.3f,0.3f,0.3f,0.3f,0.3f,0.3f,0.3f};
	
	float heartUpSpeed=2; // for how many seconds heart is crossing screen. Less is faster!
	
	private ChangeableText mLivesText;
	private ChangeableText mScoreText;
	private ChangeableText highScoreText;
	private ChangeableText flyUpText;
	
	
	private TimerHandler spawnHandler;
	private TimerHandler countdownHandler;

	private Sound mGameOverSound;
	private Sound mKissSound;
	private Sound mMissedSound;
	
	protected boolean mGameRunning;

	 public static final String PREFS_NAME = "FlyingHeartsHighScores";
	
	
	
	String PUBLISHER_ID="a14f2c7bf3bf885";
	
	
    final Handler adsHandler = new Handler();

    final Runnable showAdsRunnable = new Runnable() {
        public void run() {
        	showAds();
        }
    };
	
    final Runnable hideAdsRunnable = new Runnable() {
        public void run() {
        	unshowAds();
        }
    };
    
	
	@Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		// par resolution labs
		// http://www.andengine.org/forums/gles1/resolutions-t6036.html
		
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT , new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera).setNeedsSound(true));
		
		
	}

	@Override
	public void onLoadResources() {
		/* Load the font we are going to use. */
		this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24, true, Color.BLACK);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
		
		/* Load all the textures this game needs. */
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		heartsTextureAtlasArr=new BitmapTextureAtlas[HEART_COLOR_COUNT];

		heartsTextureAtlasArr[0]= new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		heartsTextureAtlasArr[1]= new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		heartsTextureAtlasArr[2]= new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		heartsTextureAtlasArr[3]= new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		heartsTextureReagionArr=new TextureRegion[HEART_COLOR_COUNT];
		
		heartsTextureReagionArr[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.heartsTextureAtlasArr[0],
				this, "red_heart.png", 0, 0);
		
		heartsTextureReagionArr[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.heartsTextureAtlasArr[1],
				this, "violet_heart.png", 0, 0);
		
		heartsTextureReagionArr[2] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.heartsTextureAtlasArr[2],
				this, "yellow_heart.png", 0, 0);

		heartsTextureReagionArr[3] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.heartsTextureAtlasArr[3],
				this, "green_heart.png", 0, 0);
		
		
		this.mEngine.getTextureManager().loadTextures(heartsTextureAtlasArr);
		

		menuButtonsTextureAtlas=new BitmapTextureAtlas[2];
		menuButtonsTextures=new TextureRegion[2];
		
		this.menuButtonsTextureAtlas[0] = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.menuButtonsTextureAtlas[1] = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.menuButtonsTextureAtlas[2] = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		this.menuButtonsTextures[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.menuButtonsTextureAtlas[0], this, "dont_fail_in_love.png", 0, 0);
		this.menuButtonsTextures[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.menuButtonsTextureAtlas[1], this, "love_time.png", 0, 0);
		this.mEngine.getTextureManager().loadTextures(menuButtonsTextureAtlas);

		this.mBackgroundTexture = new BitmapTextureAtlas(1024, 512, TextureOptions.DEFAULT);
		this.mMenuBackGround= new BitmapTextureAtlas(1024, 512, TextureOptions.DEFAULT);
		this.mMenuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuBackGround, this, "main_logo.png", 0, 0);

		this.gameOverBackGroundAtlas = new BitmapTextureAtlas(1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.gameOverBackGround  = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameOverBackGroundAtlas, this, "game_over.png", 0, 0);
		
		this.gameOverTouchAtlas = new BitmapTextureAtlas(1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.gameOverTouch  = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameOverTouchAtlas, this, "touch_to_continue_mid.png", 0, 0);
		this.mEngine.getTextureManager().loadTextures(this.gameOverBackGroundAtlas,this.gameOverTouchAtlas);
		
		
		
		this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTexture, this, "back_red.png", 0, 0);
		this.mEngine.getTextureManager().loadTextures(this.mBackgroundTexture, this.mBitmapTextureAtlas, this.mMenuBackGround);

		
		
		
		/* Load all the sounds this game needs. */
		try {
			SoundFactory.setAssetBasePath("mfx/");
			
			this.mGameOverSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "Looser2.wav");
			//this.mMunchSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "munch.ogg");
			this.mKissSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "little_kiss.wav");
			this.mMissedSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "mouth_pop.wav");

		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		for(int i = 0; i < LAYER_COUNT; i++) {
			this.mScene.attachChild(new Entity());
		}

		this.mScene.setBackgroundEnabled(true);
		
		this.mScene.setBackground(new ColorBackground(0.95294117647058823529411764705882f,
				0.65882352941176470588235294117647f , 0.67058823529411764705882352941176f));
		this.mScene.getChild(LAYER_BACKGROUND).attachChild(new Sprite(0, 0, this.mBackgroundTextureRegion));

		this.createMenuScene();
		this.createGameOverScene();
		

		// creating font messages
		this.mLivesText  = new ChangeableText(2,2, this.mFont, "Lives: 3", "Lives: X".length());
		this.mScoreText = new ChangeableText(CAMERA_WIDTH-60,2, this.mFont, "Score: 0000", "Score: XXXX".length());
		this.mScoreText.setPosition(CAMERA_WIDTH-this.mScoreText.getBaseWidth()-5 , 2);
		this.mScoreText.setText("Score: "+mScore);

		this.highScoreText= new ChangeableText(80,100, this.mFont, "High score: 0000",HorizontalAlign.CENTER,25 );
		this.highScoreText.setVisible(false);

		this.mScene.getChild(LAYER_SCORE).attachChild(this.mScoreText);
		this.mScene.getChild(LAYER_SCORE).attachChild(this.mLivesText);
		
		this.highScoreText.setZIndex(3);
		this.mScene.attachChild(this.highScoreText);
		
		/* spawning heart every 2 seconds. */
		this.spawnHandler=new TimerHandler(2f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
					int maxHearts=gameLevel;
					if (maxHearts>5) maxHearts=5;
					
					//on timed mode spawn much more
					if (gameMode==1) maxHearts=4;
				
					for (int i=1;i<=gameLevel;i++){
						First.this.spawnSprite();
					}
			}
		});
		
		
		this.countdownHandler=new TimerHandler(1f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				//if(First.this.mGameRunning) {
				First.this.secondsLeft--;
				setCountDown();
				
				if (First.this.secondsLeft<=0){
					First.this.secondsLeft=0;
					setCountDown();
					First.this.onGameOver();
				}
			}
		});
		
		
		getHighScores();
		showMainMenu();
		
		return this.mScene;
		
		
	}

	
    @Override
    protected int getLayoutID()
    {
    return R.layout.main;
    }

    @Override
    protected int getRenderSurfaceViewID()
    {
    return R.id.xmllayoutRenderSurfaceView;
    }
	
	
	@Override
	public void onLoadComplete() {

		
	}

	void removeSprite(final TargetObject spr ){
		this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                    /* Now it is save to remove the entity! */
                    mScene.detachChild(spr);
            }
    });

	}
	
	
	void outOfScreen(final TargetObject spr){
		
		this.removeSprite(spr);
		if (gameMode==1) return;

		if (mGameRunning){
			lives--;
			this.mLivesText.setText("Lives: "+lives);
			mMissedSound.play();
			if (lives<1){
				this.mGameRunning=false;
				this.onGameOver();
			}
		}

	}
	
	
	void heartTouched(final TargetObject spr){
		// if game over hearts simply run on background
		if (this.mGameRunning){
		
			mScore++;
			this.mScoreText.setText("Score: "+mScore);
			this.mKissSound.play();
			this.removeSprite(spr);
			
			// leveling up
			
			if (gameMode==1) {
				return;
			}
			
			// TODO: more flexible solution
			if (mScore==5 || mScore==15 || mScore==55 || mScore==100 || mScore==200 || mScore==300 || mScore==400 || mScore==500 ){
				this.gameLevel++;
				spawnHandler.setTimerSeconds(spawnSpeedArr[gameLevel]);
			}
		}

	}
	
	void startNewGameClassic(){
		mGameRunning=true;
		
	}
	
	
	String getSpriteName(){
		
		String heartSpriteName="red_heart.png";
		
		Random rnd2=new Random();
		int rndHeart=rnd2.nextInt(3);
		
		if (rndHeart==0)
			heartSpriteName="red_heart.png";
		else if (rndHeart==1)
			heartSpriteName="violet_heart.png";
		else if (rndHeart==2)
			heartSpriteName="yellow_heart.png";
		else if (rndHeart==3)
			heartSpriteName="green_heart.png";

		return heartSpriteName;
	}
	
	void spawnSprite(){
		
		Random rnd2=new Random();
		int rndHeart=rnd2.nextInt(3);
		
		Random randomGenerator = new Random();
	    int randomX = randomGenerator.nextInt(CAMERA_WIDTH-70);
	    int randomY = CAMERA_HEIGHT;//randomGenerator.nextInt(100);
		
		final TargetObject spawnHeart=new TargetObject(randomX, randomY,heartsTextureReagionArr[rndHeart] ){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (First.this.mGameRunning){
					this.setVisible(false);
					First.this.mScene.unregisterTouchArea(this);
					heartTouched(this);
				}
				return true;
			}
		};
		
		Random rnd=new Random();
		float heartSpeedLocal=(rnd.nextInt(10) +heartSpeedArr[gameLevel])/10;
		spawnHeart.setZIndex(5);
		spawnHeart.registerEntityModifier(new MoveModifier(heartSpeedLocal, randomX, rnd.nextInt(CAMERA_WIDTH-70), randomY, 2){
			
			@Override
			protected void onModifierFinished(IEntity pItem) {
				// TODO Auto-generated method stub
				super.onModifierFinished(pItem);
				outOfScreen(spawnHeart);
			}
			
			
		});
		this.mScene.attachChild(spawnHeart);
		this.mScene.registerTouchArea(spawnHeart);
	}
	
	
	private void setCountDown(){
		this.mLivesText.setText("Time: " + this.secondsLeft);
	}
	
	private void stopFlyUpText(final Text flyText){
		
		flyText.setVisible(false);
		this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                    mScene.detachChild(flyText);
            }
		});
		
	}
	
	private void launchFlyUpText(){
		
		String hintText="Get all hearts!";
		int px=70;
		
		if (gameMode==1){
			hintText="Get all hearts you can \n before time runs out!";
			px=10;
		}
		
		final Text flyText=new Text(100,250,this.mFont,hintText,HorizontalAlign.CENTER);
		
		flyText.registerEntityModifier(new MoveModifier(2f, px, px, 250, 0){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				// TODO Auto-generated method stub
				super.onModifierFinished(pItem);
				stopFlyUpText(flyText);
			}
		});
		this.mScene.attachChild(flyText);		
		
		
	}
	
	
	private void showAds () {
		// Show the ad.
			AdView adView = (AdView)findViewById(R.id.adView );
			adView.setVisibility(android.view.View.VISIBLE);
			adView.setEnabled(true);

	        AdRequest request = new AdRequest();
	        
	        adView.loadAd(request);
		}

		private void unshowAds () {
			AdView adView = (AdView)findViewById(R.id.adView );
			adView.setVisibility(android.view.View.INVISIBLE);
			adView.setEnabled(false);
		}

	
	void newGame(){

		this.lives=3;
		secondsLeft=30;

		String livesText="Lives: ";
		if (this.gameMode==0){
			this.lives=3;
			this.gameLevel=1;
			this.mScore = 0;
			secondsLeft=30;
			livesText="Lives: "+this.lives;
			
			spawnHandler.setTimerSeconds(spawnSpeedArr[1]);
			//this.flyUpText.setText("Get every heart!");
			
		}
		else if (this.gameMode==1){
			this.gameLevel=8;
			this.mScore = 0;
			this.secondsLeft=30;
			spawnHandler.setTimerSeconds(0.4f);

			this.mScene.registerUpdateHandler(this.countdownHandler);
			livesText="Time: "+this.secondsLeft;
			//this.flyUpText.setText("Get as meny hearts you can  before time runs out!");

		}
		
		this.mGameRunning=true;
		this.mScene.clearChildScene();
		adsHandler.post(hideAdsRunnable);

		gameOverBack.setVisible(false);
		
		this.mScoreText.setText("Score: "+mScore);
		this.mLivesText.setText(livesText);
		
		this.mScene.registerUpdateHandler(this.spawnHandler);
		launchFlyUpText();
	}
	
	void showMainMenu(){
		this.mScene.clearChildScene();
		this.highScoreText.setVisible(false);
    	
    	if (this.touchToContinue!=null){
        	this.touchToContinue.setVisible(false);
        	this.mScene.unregisterTouchArea(this.touchToContinue);
    		//this.mScene.detachChild(this.touchToContinue);
    	}
    	
		this.mScene.setChildScene(this.mMenuScene, true, true, true);
		//adsHandler.post(showAdsRunnable);
	}
	
	private void createMenuScene() {
		
		this.mMenuScene = new Scene();
		this.mMenuScene.attachChild(new Sprite(0, 0,this.mMenuBackgroundTextureRegion));
		
		// button size is 227x51
		// middle is (SCREEN_SIZE-BUTTON_SIZE)/2
		Sprite newGameButton= new Sprite( ((CAMERA_WIDTH-227)/2) , 200, this.menuButtonsTextures[0]){
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				gameMode=0;
				newGame();
				//System.out.println("menu touched");
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		this.mMenuScene.attachChild(newGameButton);
		this.mMenuScene.registerTouchArea(newGameButton);		
		
		Sprite newTimedGameButton= new Sprite( ((CAMERA_WIDTH-227)/2) , 270, this.menuButtonsTextures[1]){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				gameMode=1;
				newGame();
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		this.mMenuScene.attachChild(newTimedGameButton);
		this.mMenuScene.registerTouchArea(newTimedGameButton);		
		
		this.mMenuScene.setBackgroundEnabled(true);
		
	}
	
	void onTouchToContinue(){
		
    	this.touchToContinue.setVisible(true);
    	this.mScene.registerTouchArea(this.touchToContinue);
	}

	void createGameOverScene(){
		
		this.gameOverBack = new Sprite(0, 0, this.gameOverBackGround);
		this.mScene.attachChild(this.gameOverBack);
		this.gameOverBack.setZIndex(4);
		
		this.touchToContinue = new Sprite(0, 0, this.gameOverTouch){
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				showMainMenu();
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		this.mScene.attachChild(this.touchToContinue);
    	this.touchToContinue.setVisible(false);

	}
	
	
	private void showGameOver(){
		this.mGameRunning = false;
		gameOverBack.setVisible(true);
		this.mGameOverSound.play();
		adsHandler.post(showAdsRunnable);
		
		String highScoreText="High score: " +this.highScores[gameMode];
		
		if (this.mScore>this.highScores[gameMode]){
			highScoreText="New high score: "+this.mScore;
			this.highScores[gameMode]=this.mScore;
			this.setHighScore();
		}
		
		this.highScoreText.setText(highScoreText);
		this.highScoreText.setVisible(true);
		
        Timer timer3 = new Timer();
        timer3.schedule(new TimerTask(){
            public void run(){
            	onTouchToContinue();
            }
        }
        ,4000);
        
	}
	
	private void onGameOver() {
		this.mGameRunning = false;
		this.mScene.unregisterUpdateHandler(spawnHandler);
		
		if (gameMode==1){
			this.mScene.unregisterUpdateHandler(this.countdownHandler);
		}
		
		
		this.showGameOver();
	}
	
	private void getHighScores(){
	       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	       
	       this.highScores[0]=settings.getInt("classicHighScore", 0);
	       this.highScores[1]=settings.getInt("timedHighScore", 0);
	}
	
	private void setHighScore(){
	      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putInt("classicHighScore", this.highScores[0]);
	      editor.putInt("timedHighScore", this.highScores[1]);

	      editor.commit();
		
		
	}
}
