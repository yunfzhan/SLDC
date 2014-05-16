package org.sldc.assist;

/**
 * @version 0.5
 * @author Yunfei
 * Abstract interface to access data of http, file, ftp and database as array
 */
public interface IChunkDataIntf {
	public Object getItem(Object idx);
}
