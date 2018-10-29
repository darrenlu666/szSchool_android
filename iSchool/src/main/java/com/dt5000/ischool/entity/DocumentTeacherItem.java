package com.dt5000.ischool.entity;

import java.util.List;

public class DocumentTeacherItem {
        private String  subjectName;
        private List<DocumentTeacher> teachers;
		public String getSubjectName() {
			return subjectName;
		}
		public void setSubjectName(String subjectName) {
			this.subjectName = subjectName;
		}
		public List<DocumentTeacher> getTeachers() {
			return teachers;
		}
		public void setTeachers(List<DocumentTeacher> teachers) {
			this.teachers = teachers;
		}
}
