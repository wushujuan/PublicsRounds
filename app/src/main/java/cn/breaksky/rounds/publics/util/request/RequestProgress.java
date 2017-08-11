package cn.breaksky.rounds.publics.util.request;

public interface RequestProgress {
	public void sendProgress(long length, long count);

	public void sendComplete(long length);

	public void readProgress(long length, long count);

	public void readComplete(long length);

	public boolean haveCancel();
}
