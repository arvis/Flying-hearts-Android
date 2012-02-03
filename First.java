package com.arvis.andeng;


import java.io.IOException;
import java.util.Random;

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
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import com.arvis.andeng.adt.Direction;
import com.arvis.andeng.adt.SnakeSuicideException;
import com.arvis.andeng.entity.Frog;
import com.arvis.andeng.entity.Snake;
import com.arvis.andeng.entity.SnakeHead;
import com.arvis.andeng.util.constants.SnakeConstants;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.HorizontalAlign;
import org.anddev.andengine.util.MathUtils;

import android.R.bool;
import android.graphics.Color;
import android.graphics.Typeface;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.input.touch.TouchEvent;


/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 02:26:05 - 08.07.2010
 */
public class First extends BaseGameActivity implements SnakeConstants {
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
	
	//private TextureRegion mTailPartTextureRegion;
	//private TiledTextureRegion mHeadTextureRegion;
	//private TiledTextureRegion mFrogTextureRegion;

	private BitmapTextureAtlas mBackgroundTexture;
	private TextureRegion mBackgroundTextureRegion;

	//private BitmapTextureAtlas mOnScreenControlTexture;
	//private TextureRegion mOnScreenControlBaseTextureRegion;
	//private TextureRegion mOnScreenControlKnobTextureRegion;
	
	private BitmapTextureAtlas[] menuButtonsTextureAtlas;
	private TextureRegion[] menuButtonsTextures;
	
	private BitmapTextureAtlas mMenuBackGround;
	
	protected TextureRegion mMenuResetTextureRegion;
	protected TextureRegion mMenuQuitTextureRegion;
	protected TextureRegion mMenuBackgroundTextureRegion; 

	private Scene mScene;
	//protected MenuScene mMenuScene;
	private Scene mMenuScene;
	private Scene gameOverScene;

//	private Snake mSnake;
//	private Frog mFrog;
//	private TargetObject heart;

	public int mScore = 0;
	public int lives=3;
	public int gameLevel=1;
	private boolean gameRunning=false;
	private boolean menuTouched=false;
	
	private ChangeableText mLivesText;
	private ChangeableText mScoreText;
	
	private TimerHandler spawnHandler;
	

	private Sound mGameOverSound;
	//private Sound mMunchSound;
	private Sound mKissSound;
	private Sound mMissedSound;
	
	protected boolean mGameRunning;
	private Text mGameOverText;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

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
		
//		this.mOnScreenControlTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
//		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
//		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		
		
		heartsTextureAtlasArr=new BitmapTextureAtlas[HEART_COLOR_COUNT];
		// TODO: recreate as loop
		heartsTextureAtlasArr[0]= new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		heartsTextureAtlasArr[1]= new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		heartsTextureAtlasArr[2]= new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		heartsTextureAtlasArr[3]= new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
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
		this.menuButtonsTextures[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.menuButtonsTextureAtlas[0], this, "precise_valentine.png", 0, 0);
		this.menuButtonsTextures[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.menuButtonsTextureAtlas[1], this, "love_time.png", 0, 0);
		this.mEngine.getTextureManager().loadTextures(menuButtonsTextureAtlas);

		//TextureRegion menuBack  = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture1, this, "back_blue.png", 0, 0);

		
		this.mBackgroundTexture = new BitmapTextureAtlas(1024, 512, TextureOptions.DEFAULT);
		this.mMenuBackGround= new BitmapTextureAtlas(1024, 512, TextureOptions.DEFAULT);
		this.mMenuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuBackGround, this, "back_blue.png", 0, 0);

		this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTexture, this, "back_red.png", 0, 0);
		this.mEngine.getTextureManager().loadTextures(this.mBackgroundTexture, this.mBitmapTextureAtlas, this.mMenuBackGround);

		/* Load all the sounds this game needs. */
		try {
			SoundFactory.setAssetBasePath("mfx/");
			this.mGameOverSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "game_over.ogg");
			//this.mMunchSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "munch.ogg");
			this.mKissSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "munch.ogg");
			this.mMissedSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "munch.ogg");

		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		// Splash screens vienkaars: http://www.andengine.org/forums/tutorials/very-simple-splash-screen-alternative-t5790.html
		
		
		this.mScene = new Scene();
		for(int i = 0; i < LAYER_COUNT; i++) {
			this.mScene.attachChild(new Entity());
		}

		/* No background color needed as we have a fullscreen background sprite. */
		this.mScene.setBackgroundEnabled(true);
		this.mScene.getChild(LAYER_BACKGROUND).attachChild(new Sprite(0, 0, this.mBackgroundTextureRegion));


		this.mLivesText  = new ChangeableText(2,2, this.mFont, "Lives: 3", "Lives: X".length());
		//this.mScoreText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//this.mScoreText.setAlpha(0.5f);
		this.mScene.getChild(LAYER_SCORE).attachChild(this.mLivesText);
		//this.mLivesText.setText("Lives: "+lives);
		
		this.mScoreText = new ChangeableText(CAMERA_WIDTH-60,2, this.mFont, "Score: 0000", "Score: XXXX".length());
		this.mScoreText.setPosition(CAMERA_WIDTH-this.mScoreText.getBaseWidth()-5 , 2);
		this.mScoreText.setText("Score: "+mScore);

		//this.mScoreText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//this.mScoreText.setAlpha(0.5f);
		this.mScene.getChild(LAYER_SCORE).attachChild(this.mScoreText);
		
/*
		this.mFrog = new Frog(0, 0, this.mFrogTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				mScore++;
				this.setVisible(false);
				return true;
			}
		};
*/		

		
		/*		
		
		TextureRegion balloonReg = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "blue_heart.png", 0, 0);
		this.heart=new TargetObject(50, 50,balloonReg){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				mScore++;
				removeSprite(this);
				//this.setPosition(100, 100);
				//this.setVisible(false);
				return true;
			}
			
		};
		
		this.mScene.attachChild(this.heart);
		this.mScene.registerTouchArea(this.heart);
*/
		
		//this.mScene.getChild(LAYER_FOOD).attachChild(this.heart);
		
		
		//this.mFrog.animate(1000);
		//this.setFrogToRandomCell();
		//this.mScene.getChild(LAYER_FOOD).attachChild(this.mFrog);
		//this.mScene.getChild(LAYER_FOOD).attachChild(this.mFrog);


		//this.mScene.setChildScene(pChildScene, pModalDraw, pModalUpdate, pModalTouch)
		
		this.createMenuScene();
		this.mScene.setChildScene(this.mMenuScene, true, true, true);
		
		/* spawning heart every 2 seconds. */


		//FIXME: commented out for testing
		
		this.spawnHandler=new TimerHandler(2f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				//if(First.this.mGameRunning) {
					First.this.spawnSprite();
				//}
			}
		});
		
/*
		this.mScene.registerUpdateHandler(new TimerHandler(2f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				//if(First.this.mGameRunning) {
					First.this.spawnSprite();
					First.this.spawnSprite();
					
				//}
			}
		}));
*/		
		
		return this.mScene;
		
		
		/* The title-text. */
		/*
		final Text titleText = new Text(0, 0, this.mFont, "Snake\non a Phone!", HorizontalAlign.CENTER);
		titleText.setPosition((CAMERA_WIDTH - titleText.getWidth()) * 0.5f, (CAMERA_HEIGHT - titleText.getHeight()) * 0.5f);
		titleText.setScale(0.0f);
		titleText.registerEntityModifier(new ScaleModifier(2, 0.0f, 1.0f));
		this.mScene.getChild(LAYER_SCORE).attachChild(titleText);
*/
		/* The handler that removes the title-text and starts the game. */
/*		
		this.mScene.registerUpdateHandler(new TimerHandler(3.0f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				First.this.mScene.unregisterUpdateHandler(pTimerHandler);
				First.this.mScene.getChild(LAYER_SCORE).detachChild(titleText);
				First.this.mGameRunning = true;
			}
		}));
*/
		/* The game-over text. */
/*		this.mGameOverText = new Text(0, 0, this.mFont, "Game\nOver", HorizontalAlign.CENTER);
		this.mGameOverText.setPosition((CAMERA_WIDTH - this.mGameOverText.getWidth()) * 0.5f, (CAMERA_HEIGHT - this.mGameOverText.getHeight()) * 0.5f);
		this.mGameOverText.registerEntityModifier(new ScaleModifier(3, 0.1f, 2.0f));
		this.mGameOverText.registerEntityModifier(new RotationModifier(3, 0, 720));
*/
	}

	@Override
	public void onLoadComplete() {

		
	}

	// ===========================================================
	// Methods
	// ===========================================================

	
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
		lives--;
		this.mLivesText.setText("Lives: "+lives);
		mMissedSound.play();

		this.removeSprite(spr);
		//removeSprite(spawnHeart);
		if (lives<1){
			this.onGameOver();
		}
		
		
	}
	
	
	void heartTouched(final TargetObject spr){
		mScore++;
		this.mScoreText.setText("Score: "+mScore);
		this.mKissSound.play();
		this.removeSprite(spr);

	}
	
	void startNewGameClassic(){
		gameRunning=true;
		
		
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
		
		//String heartSpriteName=getSpriteName();
		
		Random rnd2=new Random();
		int rndHeart=rnd2.nextInt(3);
		
		Random randomGenerator = new Random();
	    int randomX = randomGenerator.nextInt(CAMERA_WIDTH-70);
	    int randomY = CAMERA_HEIGHT;//randomGenerator.nextInt(100);
		
		final TargetObject spawnHeart=new TargetObject(randomX, randomY,heartsTextureReagionArr[rndHeart] ){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				this.setVisible(false);
				First.this.mScene.unregisterTouchArea(this);
				//this.unregisterUpdateHandler(pUpdateHandler);
				heartTouched(this);
				//removeSprite(this);
				return true;
			}
		};
		
		//MoveModifier aa=new MoveModifier(pDuration, pFromX, pToX, pFromY, pToY)
		
		Random rnd=new Random();
		spawnHeart.registerEntityModifier(new MoveModifier(3, randomX, rnd.nextInt(CAMERA_WIDTH-70), randomY, 2){
			
			@Override
			protected void onModifierFinished(IEntity pItem) {
				// TODO Auto-generated method stub
				super.onModifierFinished(pItem);
				outOfScreen(spawnHeart);
			}
			
			
		});
		//.registerEntityModifier(new MoveModifier(30, 0, CAMERA_WIDTH - face.getWidth(), 0, CAMERA_HEIGHT - face.getHeight()));
		
		// kur kusteeties http://code.google.com/p/andengine/source/browse/src/org/anddev/andengine/entity/modifier/MoveModifier.java
		this.mScene.attachChild(spawnHeart);
		this.mScene.registerTouchArea(spawnHeart);
		
	}
	
	
	void newGame(){
		this.mScene.clearChildScene();
		this.mScene.registerUpdateHandler(this.spawnHandler);
	}
	
	private void createMenuScene() {
		
		this.mMenuScene = new Scene();
		
		Sprite newGameButton= new Sprite(10, 50, this.menuButtonsTextures[0]){
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// TODO Auto-generated method stub
				//menuTouched=true;
				//First.this.mMenuScene.setVisible(false);
				//First.this.mScene.clearChildScene();
				newGame();
				this.setVisible(false);
				System.out.println("menu touched");
				
				
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		//mMenuBackgroundTextureRegion
		this.mMenuScene.attachChild(new Sprite(0, 0,this.mMenuBackgroundTextureRegion));
		this.mMenuScene.attachChild(newGameButton);
		
		Sprite newTimedGameButton= new Sprite(10, 100, this.menuButtonsTextures[1]);
		this.mMenuScene.attachChild(newTimedGameButton);
		
		
		this.mMenuScene.registerTouchArea(newGameButton);		
		
		this.mMenuScene.setBackgroundEnabled(true);
		
		
/*		
		this.mMenuScene = new MenuScene(this.mCamera);

		final SpriteMenuItem resetMenuItem = new SpriteMenuItem(MENU_RESET, this.mMenuResetTextureRegion);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(resetMenuItem);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.mMenuQuitTextureRegion);
		
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(quitMenuItem);

		this.mMenuScene.buildAnimations();

		this.mMenuScene.setBackgroundEnabled(false);

		//this.mMenuScene.setOnMenuItemClickListener(this);
 */
	}
	

	void createGameOverScene(){
		
		this.gameOverScene= new Scene();
		
		TextureRegion tmp_reg = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuBackGround, this, "back_violet.png", 0, 0);
		Sprite gameOverBack = new Sprite(10, 50, tmp_reg);
		this.gameOverScene.attachChild(gameOverBack);

		
		
		
	}
	
	private void onGameOver() {
		this.mGameRunning = false;
		this.mGameOverSound.play();
		this.mScene.unregisterUpdateHandler(spawnHandler);
		
		this.createGameOverScene();
		this.mScene.setChildScene(this.gameOverScene);
		
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
