package org.huihoo.ofbiz.smart.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.huihoo.ofbiz.smart.base.util.CommUtil;

public class TransactionScanning {

	/**
	 * 开启事务串
	 */
	private static String transactionSacnning;

	public static void setTransactionSacnning(String transactionSacnning) {
		TransactionScanning.transactionSacnning = transactionSacnning;
	}

	/**
	 * 是否开启事务
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isTransaction(String methodName) {
		boolean flg = false;
		if (CommUtil.isNotEmpty(transactionSacnning)) {
			String[] methods = transactionSacnning.split(",");
			for (String method : methods) {
				method = method.replace("*", ".*");
				Pattern p = Pattern.compile(method);
				Matcher m = p.matcher(methodName);
				flg = m.matches();
				if (flg) {
					break;
				}
			}
		}
		return flg;
	}

}
