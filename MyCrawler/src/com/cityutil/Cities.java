package com.cityutil;

import java.util.ArrayList;
import java.util.List;

public class Cities {
	private List<String> cities;
	public Cities(){
		cities=new ArrayList<>();
		cities.add("上海");
		cities.add("北京");
		cities.add("重庆");
		cities.add("天津");
		cities.add("河北");
		cities.add("山西");
		cities.add("辽宁");
		cities.add("吉林");
		cities.add("黑龙江");
		cities.add("江苏");
		cities.add("浙江");
		cities.add("安徽");
		cities.add("福建");
		cities.add("江西");
		cities.add("山东");
		cities.add("河南");
		cities.add("湖北");
		cities.add("湖南");
		cities.add("广东");
		cities.add("海南");
		cities.add("四川");
		cities.add("贵州");
		cities.add("云南");
		cities.add("陕西");
		cities.add("甘肃");
		cities.add("青海");
		cities.add("台湾");
		cities.add("内蒙古");
		cities.add("广西");
		cities.add("西藏");
		cities.add("宁夏");
		cities.add("新疆");
		cities.add("香港");
		cities.add("澳门");
	}
	public List<String> getCities(){
		return cities;
	}
}
