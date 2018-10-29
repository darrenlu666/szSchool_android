package com.dt5000.ischool.entity;

import java.io.Serializable;

/**
 * 学生成绩实体类
 * 
 * @author 周锋
 * @date 2016年1月15日 上午10:55:40
 * @ClassInfo com.dt5000.ischool.entity.Score
 * @Description
 */
public class Score implements Serializable {

	private static final long serialVersionUID = 1L;
	private String examName;
	private Integer examId;
	private String[] subjects;
	private double[] scores;
	private double total;
	private String date;
	private Integer count;
	private String subscore;

	public Score() {
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public Integer getExamId() {
		return examId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public String[] getSubjects() {
		return subjects;
	}

	public void setSubjects(String[] subjects) {
		this.subjects = subjects;
	}

	public double[] getScores() {
		return scores;
	}

	public void setScores(double[] scores) {
		this.scores = scores;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSubscore() {
		return subscore;
	}

	public void setSubscore(String subscore) {
		this.subscore = subscore;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
