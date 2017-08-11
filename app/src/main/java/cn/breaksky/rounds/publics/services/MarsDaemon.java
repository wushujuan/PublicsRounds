package cn.breaksky.rounds.publics.services;

import android.content.Context;

import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;

public class MarsDaemon {

	public DaemonClient createDaemonClient(Context context) {
		DaemonClient client = new DaemonClient(createDaemonConfigurations());
		client.onAttachBaseContext(context);
		return client;
	}

	private DaemonConfigurations createDaemonConfigurations() {
		DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration("cn.breaksky.rounds.publics:listener",
				ListenerService.class.getCanonicalName(), ListenerServiceReciver.class.getCanonicalName());
		DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration("cn.breaksky.rounds.publics:daemon",
				ServiceDaemon.class.getCanonicalName(), ReciverDaemon.class.getCanonicalName());
		DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
		return new DaemonConfigurations(configuration1, configuration2, listener);
	}

	class MyDaemonListener implements DaemonConfigurations.DaemonListener {

		@Override
		public void onPersistentStart(Context context) {

		}

		@Override
		public void onDaemonAssistantStart(Context context) {

		}

		@Override
		public void onWatchDaemonDaed() {

		}

	}
}
