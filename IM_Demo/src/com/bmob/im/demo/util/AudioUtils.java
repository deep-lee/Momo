package com.bmob.im.demo.util;
import com.bmob.im.demo.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class AudioUtils {
	private Context context;
	private SoundPool soundPool;
	public AudioUtils(Context context) {
		super();
		this.context  = context;
		if (soundPool == null) {
			soundPool = new SoundPool(2,AudioManager.STREAM_MUSIC,5);
		}
		soundPool.load(this.context,R.raw.voice_note_start,1);
		soundPool.load(this.context,R.raw.voice_note_error,1);
  }
  public void playSound(int soundId){
	  soundPool.play(soundId,1, 1, 0, 0, 1);	  
  }
}
