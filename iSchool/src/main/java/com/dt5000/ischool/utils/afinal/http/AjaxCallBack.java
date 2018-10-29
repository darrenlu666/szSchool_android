package com.dt5000.ischool.utils.afinal.http;

/**
 * 
 * @author michael
 * 
 * @param <T>
 *            Ŀǰ����֧�� String,File, �Ժ���չ��JSONObject,Bitmap,byte[],XmlDom
 */
public abstract class AjaxCallBack<T> {

	private boolean progress = true;
	private int rate = 1000 * 1;// ÿ��

	// private Class<T> type;
	//
	// public AjaxCallBack(Class<T> clazz) {
	// this.type = clazz;
	// }

	public boolean isProgress() {
		return progress;
	}

	public int getRate() {
		return rate;
	}

	/**
	 * ���ý��,����ֻ��������������Ժ�onLoading������Ч��
	 * 
	 * @param progress
	 *            �Ƿ����ý����ʾ
	 * @param rate
	 *            ��ȸ���Ƶ��
	 */
	public AjaxCallBack<T> progress(boolean progress, int rate) {
		this.progress = progress;
		this.rate = rate;
		return this;
	}

	public void onStart() {
	};

	/**
	 * onLoading������Чprogress
	 * 
	 * @param count
	 * @param current
	 */
	public void onLoading(long count, long current) {
	};

	public void onSuccess(T t) {
	};

	public void onFailure(Throwable t, int errorNo, String strMsg) {
	};
}
