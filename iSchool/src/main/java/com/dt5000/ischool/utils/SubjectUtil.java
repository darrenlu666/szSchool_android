package com.dt5000.ischool.utils;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.SubjectItem;

/**
 * 年级和科目相关匹配信息辅助类
 * 
 * @author 周锋
 * @date 2016年1月8日 下午3:47:52
 * @ClassInfo com.dt5000.ischool.utils.SubjectUtil
 * @Description
 */
public class SubjectUtil {

	/** 学科对应的名称稀疏整型数组 */
	public static SparseArray<String> getSubjectNames() {
		SparseArray<String> subjects = new SparseArray<String>();
		subjects.put(1, "语文");
		subjects.put(2, "数学");
		subjects.put(3, "英语");
		subjects.put(4, "物理");
		subjects.put(5, "化学");
		subjects.put(6, "生物");
		subjects.put(7, "历史");
		subjects.put(8, "地理");
		subjects.put(9, "政治");
		subjects.put(10, "信息技术");
		subjects.put(11, "音乐");
		subjects.put(12, "体育");
		subjects.put(13, "美术");
		subjects.put(14, "综合实践");
		subjects.put(15, "其他");

		return subjects;
	}

	/**
	 * 学科对应的图片稀疏整型数组<br>
	 * 1 语文 2 数学 3 英语 4 物理 5 化学<br>
	 * 6 生物 7 历史 8 地理 9 政治 10 信息技术<br>
	 * 11 音乐 12 体育 13 美术 14 综合实践 15 其他
	 */
	public static SparseIntArray getSubjectImgs() {
		SparseIntArray subjectImgs = new SparseIntArray();
		subjectImgs.put(1, R.drawable.pj_yuwen);
		subjectImgs.put(2, R.drawable.pj_shuxue);
		subjectImgs.put(3, R.drawable.pj_yingyu);
		subjectImgs.put(4, R.drawable.pj_wuli);
		subjectImgs.put(5, R.drawable.pj_huaxue);
		subjectImgs.put(6, R.drawable.pj_shengwu);
		subjectImgs.put(7, R.drawable.pj_lishi);
		subjectImgs.put(8, R.drawable.pj_dili);
		subjectImgs.put(9, R.drawable.pj_zhengzhi);
		subjectImgs.put(10, R.drawable.pj_xinxi);
		subjectImgs.put(11, R.drawable.pj_yinyue);
		subjectImgs.put(12, R.drawable.pj_tiyu);
		subjectImgs.put(13, R.drawable.pj_meishu);
		subjectImgs.put(14, R.drawable.pj_zhonghe);
		subjectImgs.put(15, R.drawable.pj_qita);

		return subjectImgs;
	}

	/**
	 * 根据年级代码获取年级名称
	 * 
	 * @param gradeCode
	 *            年级代码
	 * @return
	 */
	public static String getGradeName(int gradeCode) {
		String gradeName = "";

		switch (gradeCode) {
		case 1:
			gradeName = "小学一年级";
			break;
		case 2:
			gradeName = "小学二年级";
			break;
		case 3:
			gradeName = "小学三年级";
			break;
		case 4:
			gradeName = "小学四年级";
			break;
		case 5:
			gradeName = "小学五年级";
			break;
		case 6:
			gradeName = "小学六年级";
			break;
		case 7:
			gradeName = "初中一年级";
			break;
		case 8:
			gradeName = "初中二年级";
			break;
		case 9:
			gradeName = "初中三年级";
			break;
		case 10:
			gradeName = "高中一年级";
			break;
		case 11:
			gradeName = "高中二年级";
			break;
		case 12:
			gradeName = "高中三年级";
			break;
		case 101:
			gradeName = "中考专题";
			break;
		case 102:
			gradeName = "高考专题";
			break;
		}

		return gradeName;
	}

	/**
	 * 根据年级代码获取对应科目集合
	 * 
	 * @param gradeCode
	 * @return
	 */
	public static List<SubjectItem> getSubjectList(int gradeCode) {
		List<SubjectItem> list = new ArrayList<SubjectItem>();

		list.add(new SubjectItem("1", "语文", R.drawable.pj_yuwen));
		list.add(new SubjectItem("2", "数学", R.drawable.pj_shuxue));
		list.add(new SubjectItem("3", "英语", R.drawable.pj_yingyu));

		if (gradeCode >= 7 && gradeCode < 10) {
			list.add(new SubjectItem("7", "历史", R.drawable.pj_lishi));
			list.add(new SubjectItem("9", "政治", R.drawable.pj_zhengzhi));

			if (gradeCode > 7) {
				list.add(new SubjectItem("4", "物理", R.drawable.pj_wuli));
			}

			if (gradeCode > 8) {
				list.add(new SubjectItem("5", "化学", R.drawable.pj_huaxue));
			}

			if (gradeCode < 9) {
				list.add(new SubjectItem("6", "生物", R.drawable.pj_shengwu));
				list.add(new SubjectItem("8", "地理", R.drawable.pj_dili));
			}
		} else if (gradeCode >= 10 && gradeCode <= 12) {
			list.add(new SubjectItem("4", "物理", R.drawable.pj_wuli));
			list.add(new SubjectItem("5", "化学", R.drawable.pj_huaxue));
			list.add(new SubjectItem("6", "生物", R.drawable.pj_shengwu));
			list.add(new SubjectItem("7", "历史", R.drawable.pj_lishi));
			list.add(new SubjectItem("8", "地理", R.drawable.pj_dili));
			list.add(new SubjectItem("9", "政治", R.drawable.pj_zhengzhi));
		} else if (gradeCode == 101) {
			list.add(new SubjectItem("4", "物理", R.drawable.pj_wuli));
			list.add(new SubjectItem("5", "化学", R.drawable.pj_huaxue));
			list.add(new SubjectItem("7", "历史", R.drawable.pj_lishi));
			list.add(new SubjectItem("9", "政治", R.drawable.pj_zhengzhi));
		} else if (gradeCode == 102) {
			list.add(new SubjectItem("4", "物理", R.drawable.pj_wuli));
			list.add(new SubjectItem("5", "化学", R.drawable.pj_huaxue));
			list.add(new SubjectItem("6", "生物", R.drawable.pj_shengwu));
			list.add(new SubjectItem("7", "历史", R.drawable.pj_lishi));
			list.add(new SubjectItem("8", "地理", R.drawable.pj_dili));
			list.add(new SubjectItem("9", "政治", R.drawable.pj_zhengzhi));
		}

		return list;
	}

	/**
	 * 根据年级代码获取年级标识
	 * 
	 * @param gradeCode
	 * @return
	 */
	public static String getGradeGuid(int gradeCode) {
		String gradeGuid = "";

		switch (gradeCode) {
		case 1:
			gradeGuid = "8841c68e-aa89-436c-956c-36065730dcc5";
			break;
		case 2:
			gradeGuid = "58dfeae0-ac53-48eb-b5fa-399e596cd4f7";
			break;
		case 3:
			gradeGuid = "288d0dfc-109d-40b8-b6aa-fc5f65f107be";
			break;
		case 4:
			gradeGuid = "bdb460be-3907-444b-992c-7be88326a23f";
			break;
		case 5:
			gradeGuid = "dc162c92-75a6-4869-8b97-1ddd4ded1b15";
			break;
		case 6:
			gradeGuid = "7cd290dd-9f18-4304-916b-8d3c9f1f1969";
			break;
		case 7:
			gradeGuid = "10d8dacb-f165-4648-9ffd-59340e4e3d61";
			break;
		case 8:
			gradeGuid = "6e0c8d84-597a-49c7-ab1c-2de6464f7e34";
			break;
		case 9:
			gradeGuid = "5baecd2b-fed3-45b3-b1ed-f00dda74d2bb";
			break;
		case 10:
			gradeGuid = "caf34206-e466-4f69-812a-d86c3d1407ee";
			break;
		case 11:
			gradeGuid = "2f060e6d-2ff2-45ed-a434-126e13582683";
			break;
		case 12:
			gradeGuid = "a7542852-1678-46bd-90ed-9870915c93d7";
			break;
		}

		return gradeGuid;
	}

	/**
	 * 根据科目名称获取科目标识
	 * 
	 * @param subjectName
	 * @return
	 */
	public static String getSubjectGuid(String subjectName) {
		String subjectGuid = "";

		if ("数学".equals(subjectName)) {
			subjectGuid = "d7f81496-f285-4d87-a8a6-3a7e3293003a";
		} else if ("语文".equals(subjectName)) {
			subjectGuid = "3209b63f-52a4-4aec-8a2b-07fab2117d3f";
		} else if ("英语".equals(subjectName)) {
			subjectGuid = "fb302434-b5a2-4095-ba11-8d9bac695264";
		} else if ("物理".equals(subjectName)) {
			subjectGuid = "2474da78-0be6-47a7-943f-92b6b1adbc2a";
		} else if ("化学".equals(subjectName)) {
			subjectGuid = "32f2ded7-af2b-4438-a76f-66103ed3bf4e";
		} else if ("生物".equals(subjectName)) {
			subjectGuid = "5c79d436-6964-4bee-afca-b591fab9a276";
		} else if ("地理".equals(subjectName)) {
			subjectGuid = "1b54508a-d90f-4f47-b241-3819791820c4";
		} else if ("历史".equals(subjectName)) {
			subjectGuid = "cefba238-9bfb-4afa-b780-c26895096d19";
		} else if ("政治".equals(subjectName)) {
			subjectGuid = "da439a20-44fa-41fe-96c3-9467ebdeeac7";
		}

		return subjectGuid;
	}

	/**
	 * 根据类型标识获取自主学习试题的类型
	 * 
	 * @param type
	 * @return
	 */
	public static String getStudyTestType(int type) {
		switch (type) {
		case 1:
			return "单选";
		case 2:
			return "多选";
		case 3:
			return "不定项选择";
		case 4:
			return "填空";
		case 5:
			return "简答";
		case 6:
			return "阅读理解";
		case 7:
			return "作文";
		case 8:
			return "判断题";
		case 9:
			return "论述题";
		case 10:
			return "综合运用题";
		case 11:
			return "完形填空";
		}

		return "";
	}

}
