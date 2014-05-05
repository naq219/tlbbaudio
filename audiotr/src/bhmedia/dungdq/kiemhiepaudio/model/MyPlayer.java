package bhmedia.dungdq.kiemhiepaudio.model;

import android.media.MediaPlayer;

public class MyPlayer {
	public static MediaPlayer mp = null;
	public static MediaPlayer getInstance() {
      if(mp == null) {
         mp = new MediaPlayer();
      }
      return mp;
   }
}
