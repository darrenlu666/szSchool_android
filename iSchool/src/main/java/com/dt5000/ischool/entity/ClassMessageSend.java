package com.dt5000.ischool.entity;

/**
 * 发送班级消息时用到的实体类
 *
 * @author 周锋
 * @date 2016年1月20日 下午4:30:00
 * @ClassInfo com.dt5000.ischool.entity.ClassMessageSend
 * @Description
 */
public class ClassMessageSend implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private Integer classMessageId;
    private String classinfoId;
    private String teacherId;
    private String studentId;
    private String content;
    private Long sendDate;
    private Integer reaStatus;
    private String teacherName;
    private String studentName;
    private String messageType;

    public ClassMessageSend() {
    }

    public Integer getClassMessageId() {
        return classMessageId;
    }

    public void setClassMessageId(Integer classMessageId) {
        this.classMessageId = classMessageId;
    }

    public String getClassinfoId() {
        return classinfoId;
    }

    public void setClassinfoId(String classinfoId) {
        this.classinfoId = classinfoId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSendDate() {
        return sendDate;
    }

    public void setSendDate(Long sendDate) {
        this.sendDate = sendDate;
    }

    public Integer getReaStatus() {
        return reaStatus;
    }

    public void setReaStatus(Integer reaStatus) {
        this.reaStatus = reaStatus;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}