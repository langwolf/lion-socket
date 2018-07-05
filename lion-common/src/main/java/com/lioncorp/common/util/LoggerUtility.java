package com.lioncorp.common.util;

public class LoggerUtility {
	 public static ThreadLocal<StringBuilder> thread_local = new ThreadLocal<>();
	    public static void beforeInvoke() {
	        StringBuilder sb = thread_local.get();
	        if (sb == null) {
	            sb = new StringBuilder();
	            thread_local.set(sb);
	        }
	        sb.delete(0, sb.length());
	    }
	    public static void returnInvoke(Class c) {
	        StringBuilder sb = thread_local.get();
	        if (sb != null) {
	            System.out.println(  sb.toString());
	        }
	    }
	    public static void throwableInvoke(Class c, String fmt, Object... args) {
	        StringBuilder sb = thread_local.get();
	        if (sb != null) {
	            System.out.println(  sb.toString() + " " + String.format(fmt, args));
	        }
	    }
	    public static void noticeLog(String fmt, Object... args) {
	        StringBuilder sb = thread_local.get();
	        if (sb != null) {
	            sb.append(String.format(fmt, args));
	        }
	    }
	}
