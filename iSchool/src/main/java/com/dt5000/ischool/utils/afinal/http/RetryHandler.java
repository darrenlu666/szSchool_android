package com.dt5000.ischool.utils.afinal.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

public class RetryHandler implements HttpRequestRetryHandler {
	private static final int RETRY_SLEEP_TIME_MILLIS = 1000;

	// �����쳣������
	private static HashSet<Class<?>> exceptionWhitelist = new HashSet<Class<?>>();

	// �û��쳣���������磬�û��ж��̣߳�
	private static HashSet<Class<?>> exceptionBlacklist = new HashSet<Class<?>>();

	static {
		exceptionWhitelist.add(NoHttpResponseException.class);
		exceptionWhitelist.add(UnknownHostException.class);
		exceptionWhitelist.add(SocketException.class);

		exceptionBlacklist.add(InterruptedIOException.class);
		exceptionBlacklist.add(SSLHandshakeException.class);
	}

	private final int maxRetries;

	public RetryHandler(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	@Override
	public boolean retryRequest(IOException exception, int executionCount,
			HttpContext context) {
		boolean retry = true;

		Boolean b = (Boolean) context
				.getAttribute(ExecutionContext.HTTP_REQ_SENT);
		boolean sent = (b != null && b.booleanValue());

		if (executionCount > maxRetries) {
			// ���Դ�����û�����Ĳ��ԣ�Ĭ��5��
			retry = false;
		} else if (exceptionBlacklist.contains(exception.getClass())) {
			// �̱߳��û��жϣ��򲻼�����
			retry = false;
		} else if (exceptionWhitelist.contains(exception.getClass())) {
			retry = true;
		} else if (!sent) {
			retry = true;
		}

		if (retry) {
			HttpUriRequest currentReq = (HttpUriRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			retry = currentReq != null
					&& !"POST".equals(currentReq.getMethod());
		}

		if (retry) {
			// ����1���Ӻ��ټ�����
			SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
		} else {
			exception.printStackTrace();
		}

		return retry;
	}

}