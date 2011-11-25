package hu.vmiklos.pacman;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class Storage {
	public static void setScore(int score) {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore("score", true);
			if (score <= readScore(rs))
				return;
			byte bytes[] = String.valueOf(score).getBytes();
			if (rs.getNumRecords() == 0) {
				rs.addRecord(bytes, 0, bytes.length);
			} else {
				rs.setRecord(1, bytes, 0, bytes.length);
			}
		} catch (RecordStoreFullException e) {
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			e.printStackTrace();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.closeRecordStore();
				} catch (RecordStoreNotOpenException e) {
				} catch (RecordStoreException e) {
				}
		}
	}
	
	public static int getScore() {
		RecordStore rs = null;
		int ret = -1;
		try {
			rs = RecordStore.openRecordStore("score", false);
			ret = readScore(rs);
		} catch (RecordStoreFullException e) {
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			return ret;
		} catch (RecordStoreException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.closeRecordStore();
				} catch (RecordStoreNotOpenException e) {
				} catch (RecordStoreException e) {
				}
		}
		return ret;
	}
	
	private static int readScore(RecordStore rs) {
		int ret = -1;
		try {
			byte bytes[] = rs.getRecord(1);
			ret = Integer.valueOf(new String(bytes)).intValue();
		} catch (RecordStoreNotOpenException e) {
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			e.printStackTrace();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
