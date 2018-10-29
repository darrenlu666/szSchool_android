package com.dt5000.ischool.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
/**
 * 题目实体类
 * @author jiaojian
 *
 */
public class PracticeJson implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Expose 
	private Boolean ok;
	@Expose 
	private List<StudyPractice> ds;
	public Boolean getOk() {
		return ok;
	}
	public void setOk(Boolean ok) {
		this.ok = ok;
	}
	public List<StudyPractice> getDs() {
		return ds;
	}
	public void setDs(List<StudyPractice> ds) {
		this.ds = ds;
	}
	
	

}
