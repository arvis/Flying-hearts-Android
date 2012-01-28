package com.arvis.andeng;


import java.io.IOException;

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
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
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

//	private static final int CAMERA_WIDTH = CELLS_HORIZONTAL * CELL_WIDTH; // 640
//	private static final int CAMERA_HEIGHT = CELLS_VERTICAL * CELL_HEIGHT; // 480

	private static final int CAMERA_WIDTH = 400;
	private static final int CAMERA_HEIGHT = 800;
	
	
	private static final int LAYER_COUNT = 4;

	private static final int LAYER_BACKGROUND = 0;
	private static final int LAYER_FOOD = LAYER_BACKGROUND + 1;
	private static final int LAYER_SNAKE = LAYER_FOOD + 1;
	private static final int LAYER_SCORE = LAYER_SNAKE + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;

	private DigitalOnScreenControl mDigitalOnScreenControl;

	private BitmapTextureAtlas mFontTexture;
	private Font mFont;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TextureRegion mTailPartTextureRegion;
	private TiledTextureRegion mHeadTextureRegion;
	private TiledTextureRegion mFrogTextureRegion;

	private BitmapTextureAtlas mBackgroundTexture;
	private TextureRegion mBackgroundTextureRegion;

	private BitmapTextureAtlas mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;

	private Scene mScene;

	private Snake mSnake;
	private Frog mFrog;
	private TargetObject heart;

	private int mScore = 0;
	private ChangeableText mScoreText;
	private boolean moving=false;

	private Sound mGameOverSound;
	private Sound mMunchSound;
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
		FontFactory.setAssetBasePath("font/");
		this.mFontTexture = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.mFont = FontFactory.createFromAsset(this.mFontTexture, this, "Plok.ttf", 32, true, Color.WHITE);

		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		//this.getFontManager().loadFont(this.mFont);

		/* Load all the textures this game needs. */
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		
		this.mHeadTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "snake_head.png", 0, 0, 3, 1);
		this.mTailPartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "snake_tailpart.png", 96, 0);
		this.mFrogTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "frog.png", 0, 64, 3, 1);

		this.mBackgroundTexture = new BitmapTextureAtlas(1024, 512, TextureOptions.DEFAULT);
		this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTexture, this, "snake_background.png", 0, 0);

		this.mOnScreenControlTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);

		this.mEngine.getTextureManager().loadTextures(this.mBackgroundTexture, this.mBitmapTextureAtlas, this.mOnScreenControlTexture);

		/* Load all the sounds this game needs. */
		try {
			SoundFactory.setAssetBasePath("mfx/");
			this.mGameOverSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "game_over.ogg");
			this.mMunchSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "munch.ogg");
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

		/* No background color needed as we have a fullscreen background sprite. */
		this.mScene.setBackgroundEnabled(false);
		this.mScene.getChild(LAYER_BACKGROUND).attachChild(new Sprite(0, 0, this.mBackgroundTextureRegion));

		/* The ScoreText showing how many points the pEntity scored. */
		//this.mScoreText = new ChangeableText(5, 5, this.mFont, "Score: 0", "Score: XXXX".length());
//		this.mScoreText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		this.mScoreText.setAlpha(0.5f);
//		this.mScene.getChild(LAYER_SCORE).attachChild(this.mScoreText);
		
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
		
		// kur kusteeties http://code.google.com/p/andengine/source/browse/src/org/anddev/andengine/entity/modifier/MoveModifier.java
		
		
		this.mScene.attachChild(this.heart);
		this.mScene.registerTouchArea(this.heart);

		
		//this.mScene.getChild(LAYER_FOOD).attachChild(this.heart);
		
		
		//this.mFrog.animate(1000);
		//this.setFrogToRandomCell();
		//this.mScene.getChild(LAYER_FOOD).attachChild(this.mFrog);
		//this.mScene.getChild(LAYER_FOOD).attachChild(this.mFrog);

		
		//moving a frog
		this.mScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(final float pSecondsElapsed) {
				if (moving) moveSnake();
			}

			@Override
			public void reset() {}
		});
		
		
		/* Make the Snake move every 0.5 seconds. */
/*		
		this.mScene.registerUpdateHandler(new TimerHandler(0.5f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				//if(First.this.mGameRunning) {
					try {
						First.this.mSnake.move();
					} catch (final SnakeSuicideException e) {
						First.this.onGameOver();
					}

					First.this.handleNewSnakePosition();
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

	private void moveSnake(){
		try {
			First.this.mSnake.move();
		} catch (final SnakeSuicideException e) {
			First.this.onGameOver();
		}
		
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
	
	
	private void setFrogToRandomCell() {
		this.mFrog.setCell(MathUtils.random(1, CELLS_HORIZONTAL - 2), MathUtils.random(1, CELLS_VERTICAL - 2));
	}

	private void handleNewSnakePosition() {
		final SnakeHead snakeHead = this.mSnake.getHead();

		if(snakeHead.getCellX() < 0 || snakeHead.getCellX() >= CELLS_HORIZONTAL || snakeHead.getCellY() < 0 || snakeHead.getCellY() >= CELLS_VERTICAL) {
			this.onGameOver();
		} else if(snakeHead.isInSameCell(this.mFrog)) {
			this.mScore += 50;
			this.mScoreText.setText("Score: " + this.mScore);
			this.mSnake.grow();
			this.mMunchSound.play();
			this.setFrogToRandomCell();
		}
	}

	private void onGameOver() {
		this.mGameOverSound.play();
		this.mScene.getChild(LAYER_SCORE).attachChild(this.mGameOverText);
		this.mGameRunning = false;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
