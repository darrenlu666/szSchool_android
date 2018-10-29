package com.dt5000.ischool.entity;

import java.util.List;


public class DocumentFlowDetail {
        private String createAt;
        private String createBy;
        private String docContent;
        private String docStatus;
        private String docTitle;
        private String filePath;
        private String teacherName;
        private List<DocumentComment> events;
        private List<String> teachers;
		public String getCreateAt() {
			return createAt;
		}
		public void setCreateAt(String createAt) {
			this.createAt = createAt;
		}
		public String getCreateBy() {
			return createBy;
		}
		public void setCreateBy(String createBy) {
			this.createBy = createBy;
		}
		public String getDocContent() {
			return docContent;
		}
		public void setDocContent(String docContent) {
			this.docContent = docContent;
		}
		public String getDocStatus() {
			return docStatus;
		}
		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}
		public String getDocTitle() {
			return docTitle;
		}
		public void setDocTitle(String docTitle) {
			this.docTitle = docTitle;
		}
		public String getFilePath() {
			return filePath;
		}
		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
		public String getTeacherName() {
			return teacherName;
		}
		public void setTeacherName(String teacherName) {
			this.teacherName = teacherName;
		}
		public List<DocumentComment> getEvents() {
			return events;
		}
		public void setEvents(List<DocumentComment> events) {
			this.events = events;
		}
		public List<String> getTeachers() {
			return teachers;
		}
		public void setTeachers(List<String> teachers) {
			this.teachers = teachers;
		}
        
}
