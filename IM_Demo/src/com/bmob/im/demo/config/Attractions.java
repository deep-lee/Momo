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
		attarctionsSet.add(new BmobGeoPoint(103.928015,33.271447));  // 0 ��կ��
		attarctionsSet.add(new BmobGeoPoint(104.083176,30.660824));  // 1 ����·
		attarctionsSet.add(new BmobGeoPoint(104.055,30.650696));  // 2 �����
		attarctionsSet.add(new BmobGeoPoint(103.620669,31.007682));  // 3 ������
		attarctionsSet.add(new BmobGeoPoint(104.003307,30.55768));  // 4 ���󽭰�У��
		attarctionsSet.add(new BmobGeoPoint(116.942693,31.446724));  // 5 �����ѧ
		attarctionsSet.add(new BmobGeoPoint(109.291934,34.39444));  // 7 ��������ٸ
		attarctionsSet.add(new BmobGeoPoint(118.194253,30.082226));  // 8 ���ջ�ɽ
		attarctionsSet.add(new BmobGeoPoint(118.073672,24.452335));  // 9 ���Ź�����
		attarctionsSet.add(new BmobGeoPoint(118.130118,24.431489));  // 10 ���Ȱ�
		attarctionsSet.add(new BmobGeoPoint(126.633755,45.776193));  // 11 ������ʥ�����Ǵ����
		attarctionsSet.add(new BmobGeoPoint(117.947016,40.990196));  // 12 �е±���ɽׯ
		attarctionsSet.add(new BmobGeoPoint(109.654556,18.236153));  // 13 ��������������
		attarctionsSet.add(new BmobGeoPoint(118.058037,29.373708));  // 14 ������Դ
		attarctionsSet.add(new BmobGeoPoint(115.955115,29.563251));  // 15 ����®ɽ
		attarctionsSet.add(new BmobGeoPoint(116.021415,40.363466));  // 16 �����˴��볤��
		attarctionsSet.add(new BmobGeoPoint(120.139058,30.251772));  // 17 ��������
		attarctionsSet.add(new BmobGeoPoint(91.124882,29.661235 ));  // 18 ���ز�������
		attarctionsSet.add(new BmobGeoPoint(100.167776,25.699029));  // 19 ���ϴ���ų�
		attarctionsSet.add(new BmobGeoPoint(119.019873,29.598407));  // 20 �㽭ǧ����
		attarctionsSet.add(new BmobGeoPoint(117.995474,30.010385));  // 21 ���ݺ��
		attarctionsSet.add(new BmobGeoPoint(116.27487,39.998473));  // 22 �ú�԰
		attarctionsSet.add(new BmobGeoPoint(116.404015,39.912733 ));  // 23 �찲�Ź㳡
		attarctionsSet.add(new BmobGeoPoint(116.314607,40.01629));  // 24 Բ��԰
		attarctionsSet.add(new BmobGeoPoint(121.496713,31.241765));  // 25 �Ϻ���̲
		attarctionsSet.add(new BmobGeoPoint(117.122599,36.251515 ));  // 26 ̩ɽ
		attarctionsSet.add(new BmobGeoPoint(111.018502,32.405693));  // 27 �䵱ɽ
		attarctionsSet.add(new BmobGeoPoint(94.815601,40.048746));  // 28 �ػ�Ī�߿�
		attarctionsSet.add(new BmobGeoPoint(114.052791,22.315576));  // 29 ��۵�˹����԰�ȼ���
		attarctionsSet.add(new BmobGeoPoint(114.309053,30.550232));  // 30 �����ƺ�¥
		attarctionsSet.add(new BmobGeoPoint(100.240547,26.881159));  // 31 �������� 
		attarctionsSet.add(new BmobGeoPoint(116.403426,39.924077));  // 32 �ʹ�
		attarctionsSet.add(new BmobGeoPoint(116.392861,39.941954));  // 33 ����������
		attarctionsSet.add(new BmobGeoPoint(116.399127,40.001209));  // 34 ��������ƥ�˹�԰
		attarctionsSet.add(new BmobGeoPoint(121.50626,31.245369));  // 35 ��������㲥������
		attarctionsSet.add(new BmobGeoPoint(121.50626,31.245369));  // 36 �Ͼ�������
		attarctionsSet.add(new BmobGeoPoint(120.074037,30.274644));  // 37 ������Ϫʪ��������
		attarctionsSet.add(new BmobGeoPoint(115.787575,31.136299));  // 38 �����н�կ������կ���ξ���
		attarctionsSet.add(new BmobGeoPoint(115.787575,31.136299));  // 39 ��ɽ�йŻ����Ļ�������
		attarctionsSet.add(new BmobGeoPoint(117.067539,24.589321));  // 40 ������¥���������Ͼ������ξ���
		attarctionsSet.add(new BmobGeoPoint(118.608168,24.949825));  // 41 Ȫ������Դɽ�羰��ʤ��
		attarctionsSet.add(new BmobGeoPoint(100.284377,36.805245));  // 42 �ຣ���羰��
		
		attractionsName.add("��կ��");
		attractionsName.add("����·");
		attractionsName.add("�����");
		attractionsName.add("������");
		attractionsName.add("���󽭰�У��");
		attractionsName.add("�����ѧ");
		attractionsName.add("��������ٸ");
		attractionsName.add("���ջ�ɽ");
		attractionsName.add("���Ź�����");
		attractionsName.add("���Ȱ�");
		attractionsName.add("������ʥ�����Ǵ����");
		attractionsName.add("�е±���ɽׯ");
		attractionsName.add("��������������");
		attractionsName.add("������Դ");
		attractionsName.add("����®ɽ");
		attractionsName.add("�����˴��볤��");
		attractionsName.add("��������");
		attractionsName.add("���ز�������");
		attractionsName.add("���ϴ���ų�");
		attractionsName.add("�㽭ǧ����");
		attractionsName.add("���ݺ��");
		attractionsName.add("�ú�԰");
		attractionsName.add("�찲�Ź㳡");
		attractionsName.add("Բ��԰");
		attractionsName.add("�Ϻ���̲");
		attractionsName.add("̩ɽ");
		attractionsName.add("�䵱ɽ");
		attractionsName.add("�ػ�Ī�߿�");
		attractionsName.add("��۵�˹����԰�ȼ���");
		attractionsName.add("�����ƺ�¥");
		attractionsName.add("�������� ");
		attractionsName.add("�ʹ�");
		attractionsName.add("����������");
		attractionsName.add("��������ƥ�˹�԰");
		attractionsName.add("��������㲥������");
		attractionsName.add("�Ͼ�������");
		attractionsName.add("������Ϫʪ��������");
		attractionsName.add("�����н�կ������կ���ξ���");
		attractionsName.add("��ɽ�йŻ����Ļ�������");
		attractionsName.add("������¥���������Ͼ������ξ���");
		attractionsName.add("Ȫ������Դɽ�羰��ʤ��");
		attractionsName.add("�ຣ���羰��");
		
		
		
	}
	
	public static BmobGeoPoint getRandomAttractions() {
		int random = (int)(Math.random() * 42);
		return attarctionsSet.get(random);
	}
	
	public static int getRandom() {
		return (int)(Math.random() * 42);
	}
	
	
	
}
