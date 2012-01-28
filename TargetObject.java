package com.arvis.andeng;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class TargetObject extends Sprite {

	public TargetObject(float pX, float pY,TextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion);
		
		//TimerHandler spriteTimerHandler;

		
/*		
        float mEffectSpawnDelay=1;
        //tutorial on timer http://www.andengine.org/forums/tutorials/using-timer-s-sprite-spawn-example-t463.html
		this.registerUpdateHandler(new TimerHandler(mEffectSpawnDelay, new ITimerCallback()
        {                      
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler)
            {          
            	moveSprite();
            	
            }
        }));
*/
        
        
        
	}
	
	public void moveSprite(){
		
		this.setPosition(this.getX(), this.getY()+3);
		
	}
	
	
	
}
