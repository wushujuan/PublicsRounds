package cn.breaksky.rounds.publics.util.request;

import java.io.OutputStream;

public interface RequestLogic {

	public byte[] send(Params param) throws Exception;

	public void send(RequestProgress progress, OutputStream readout, Params param) throws Exception;

	public String getInitSessionID() throws Exception;

	public int getResponseTag();

}
