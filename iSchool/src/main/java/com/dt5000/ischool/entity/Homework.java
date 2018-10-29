package com.dt5000.ischool.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作业实体类
 * 
 * @author 周锋
 * @date 2016年1月12日 下午2:31:17
 * @ClassInfo com.dt5000.ischool.entity.Homework
 * @Description
 */
public class Homework implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer homeworkId;// 作业id
	private String name;// 作业名称
	private Integer subjectId;// 所属科目id
	private Integer subjectPicId;// 所属科目对应的图片id
	private String subjectName;// 所属科目名称
	private String content;// 作业内容
	private String subContent;// 内容
	private String attach;// （修改过的附件文件名）地址
	private Long createTime;// 发布日期
	private String createBy;// 发布人
	private String attachOldName;// 附件原始名称
	private List<HomeworkComment> homeworkCommentItems;// 作业相关的评论
	private List<HomeworkAttach> homeworkAttachs;// 作业附件列表

	public Homework() {
	}

	public Integer getHomeworkId() {
		return homeworkId;
	}

	public void setHomeworkId(Integer homeworkId) {
		this.homeworkId = homeworkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getAttachOldName() {
		return attachOldName;
	}

	public void setAttachOldName(String attachOldName) {
		this.attachOldName = attachOldName;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getSubjectPicId() {
		return subjectPicId;
	}

	public void setSubjectPicId(Integer subjectPicId) {
		this.subjectPicId = subjectPicId;
	}

	public String getSubContent() {
		return subContent;
	}

	public void setSubContent(String subContent) {
		this.subContent = subContent;
	}

	public List<HomeworkComment> getHcomments() {
		return homeworkCommentItems;
	}

	public void setHcomments(List<HomeworkComment> hcomments) {
		this.homeworkCommentItems = hcomments;
	}

	public List<HomeworkAttach> getHomeworkAttachs() {
		return homeworkAttachs;
	}

	public void setHomeworkAttachs(List<HomeworkAttach> homeworkAttachs) {
		this.homeworkAttachs = homeworkAttachs;
	}

}
