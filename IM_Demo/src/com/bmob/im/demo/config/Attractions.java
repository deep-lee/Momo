package com.bmob.im.demo.config;

import java.util.ArrayList;

import com.baidu.platform.comapi.map.v;

import cn.bmob.v3.datatype.BmobGeoPoint;

public class Attractions {
	
	public static ArrayList<BmobGeoPoint> attarctionsSet = new ArrayList<BmobGeoPoint>();
	
	public static ArrayList<String> attractionsName = new ArrayList<String>();

	public Attractions() {
		super();
		// TODO Auto-generated constructor stub
		initData();
	}
	
	public void initData() {
		attarctionsSet.add(new BmobGeoPoint(103.928015,33.271447));  // 0 九寨沟
		attarctionsSet.add(new BmobGeoPoint(104.083176,30.660824));  // 1 春熙路
		attarctionsSet.add(new BmobGeoPoint(104.055,30.650696));  // 2 武侯祠
		attarctionsSet.add(new BmobGeoPoint(103.620669,31.007682));  // 3 都江堰
		attarctionsSet.add(new BmobGeoPoint(104.003307,30.55768));  // 4 川大江安校区
		attarctionsSet.add(new BmobGeoPoint(116.942693,31.446724));  // 5 舒城中学
		attarctionsSet.add(new BmobGeoPoint(109.291934,34.39444));  // 7 西安兵马俑
		attarctionsSet.add(new BmobGeoPoint(118.194253,30.082226));  // 8 安徽黄山
		attarctionsSet.add(new BmobGeoPoint(118.073672,24.452335));  // 9 厦门鼓浪屿
		attarctionsSet.add(new BmobGeoPoint(118.130118,24.431489));  // 10 曾厝安
		attarctionsSet.add(new BmobGeoPoint(126.633755,45.776193));  // 11 哈尔滨圣索菲亚大教堂
		attarctionsSet.add(new BmobGeoPoint(117.947016,40.990196));  // 12 承德避暑山庄
		attarctionsSet.add(new BmobGeoPoint(109.654556,18.236153));  // 13 海南三亚亚龙湾
		attarctionsSet.add(new BmobGeoPoint(118.058037,29.373708));  // 14 江西婺源
		attarctionsSet.add(new BmobGeoPoint(115.955115,29.563251));  // 15 江西庐山
		attarctionsSet.add(new BmobGeoPoint(116.021415,40.363466));  // 16 北京八达岭长城
		attarctionsSet.add(new BmobGeoPoint(120.139058,30.251772));  // 17 杭州西湖
		attarctionsSet.add(new BmobGeoPoint(91.124882,29.661235 ));  // 18 西藏布达拉宫
		attarctionsSet.add(new BmobGeoPoint(100.167776,25.699029));  // 19 云南大理古城
		attarctionsSet.add(new BmobGeoPoint(119.019873,29.598407));  // 20 浙江千岛湖
		attarctionsSet.add(new BmobGeoPoint(117.995474,30.010385));  // 21 西递宏村
		attarctionsSet.add(new BmobGeoPoint(116.27487,39.998473));  // 22 颐和园
		attarctionsSet.add(new BmobGeoPoint(116.404015,39.912733 ));  // 23 天安门广场
		attarctionsSet.add(new BmobGeoPoint(116.314607,40.01629));  // 24 圆明园
		attarctionsSet.add(new BmobGeoPoint(121.496713,31.241765));  // 25 上海外滩
		attarctionsSet.add(new BmobGeoPoint(117.122599,36.251515 ));  // 26 泰山
		attarctionsSet.add(new BmobGeoPoint(111.018502,32.405693));  // 27 武当山
		attarctionsSet.add(new BmobGeoPoint(94.815601,40.048746));  // 28 敦煌莫高窟
		attarctionsSet.add(new BmobGeoPoint(114.052791,22.315576));  // 29 香港迪斯尼乐园度假区
		attarctionsSet.add(new BmobGeoPoint(114.309053,30.550232));  // 30 湖北黄鹤楼
		attarctionsSet.add(new BmobGeoPoint(100.240547,26.881159));  // 31 桂林丽江 
		attarctionsSet.add(new BmobGeoPoint(116.403426,39.924077));  // 32 故宫
		attarctionsSet.add(new BmobGeoPoint(116.392861,39.941954));  // 33 恭王府景区
		attarctionsSet.add(new BmobGeoPoint(116.399127,40.001209));  // 34 北京奥林匹克公园
		attarctionsSet.add(new BmobGeoPoint(121.50626,31.245369));  // 35 东方明珠广播电视塔
		attarctionsSet.add(new BmobGeoPoint(121.50626,31.245369));  // 36 南京夫子庙
		attarctionsSet.add(new BmobGeoPoint(120.074037,30.274644));  // 37 杭州西溪湿地旅游区
		attarctionsSet.add(new BmobGeoPoint(115.787575,31.136299));  // 38 六安市金寨县天堂寨旅游景区
		attarctionsSet.add(new BmobGeoPoint(115.787575,31.136299));  // 39 黄山市古徽州文化旅游区
		attarctionsSet.add(new BmobGeoPoint(117.067539,24.589321));  // 40 福建土楼（永定·南靖）旅游景区
		attarctionsSet.add(new BmobGeoPoint(118.608168,24.949825));  // 41 泉州市清源山风景名胜区
		attarctionsSet.add(new BmobGeoPoint(100.284377,36.805245));  // 42 青海湖风景区
		
		attractionsName.add("九寨沟");
		attractionsName.add("春熙路");
		attractionsName.add("武侯祠");
		attractionsName.add("都江堰");
		attractionsName.add("川大江安校区");
		attractionsName.add("舒城中学");
		attractionsName.add("西安兵马俑");
		attractionsName.add("安徽黄山");
		attractionsName.add("厦门鼓浪屿");
		attractionsName.add("曾厝安");
		attractionsName.add("哈尔滨圣索菲亚大教堂");
		attractionsName.add("承德避暑山庄");
		attractionsName.add("海南三亚亚龙湾");
		attractionsName.add("江西婺源");
		attractionsName.add("江西庐山");
		attractionsName.add("北京八达岭长城");
		attractionsName.add("杭州西湖");
		attractionsName.add("西藏布达拉宫");
		attractionsName.add("云南大理古城");
		attractionsName.add("浙江千岛湖");
		attractionsName.add("西递宏村");
		attractionsName.add("颐和园");
		attractionsName.add("天安门广场");
		attractionsName.add("圆明园");
		attractionsName.add("上海外滩");
		attractionsName.add("泰山");
		attractionsName.add("武当山");
		attractionsName.add("敦煌莫高窟");
		attractionsName.add("香港迪斯尼乐园度假区");
		attractionsName.add("湖北黄鹤楼");
		attractionsName.add("桂林丽江 ");
		attractionsName.add("故宫");
		attractionsName.add("恭王府景区");
		attractionsName.add("北京奥林匹克公园");
		attractionsName.add("东方明珠广播电视塔");
		attractionsName.add("南京夫子庙");
		attractionsName.add("杭州西溪湿地旅游区");
		attractionsName.add("六安市金寨县天堂寨旅游景区");
		attractionsName.add("黄山市古徽州文化旅游区");
		attractionsName.add("福建土楼（永定·南靖）旅游景区");
		attractionsName.add("泉州市清源山风景名胜区");
		attractionsName.add("青海湖风景区");
		
		
		
	}
	
	public static BmobGeoPoint getRandomAttractions() {
		int random = (int)(Math.random() * 42);
		return attarctionsSet.get(random);
	}
	
	public static int getRandom() {
		return (int)(Math.random() * 42);
	}
	
	
	
}
