package cn.breaksky.rounds.publics.listener;


public interface MessageCallBack {
	public enum STATUS {
		SUCCESS, FAILE;
	}

	public void endCall(STATUS status);
}
