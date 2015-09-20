package com.kmgh.utils;

public class RemoveNonZh {
	public static String removeNonZh(String str){
		return str.replaceAll("[^\u4E00-\u9FA5]{3,40}", "");
	}
}
