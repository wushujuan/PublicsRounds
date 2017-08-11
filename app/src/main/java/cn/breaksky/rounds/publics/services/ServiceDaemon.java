package cn.breaksky.rounds.publics.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceDaemon extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
