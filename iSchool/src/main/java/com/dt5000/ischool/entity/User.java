package com.dt5000.ischool.entity;

import android.content.Context;
import android.content.SharedPreferences;

import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MCon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息实体类
 *
 * @author 周锋
 * @date 2016年1月11日 下午2:25:54
 * @ClassInfo com.dt5000.ischool.entity.User
 * @Description
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String schoolbaseinfoId;
    private String gradeinfoId;
    private String phone;
    private String classinfoId;
    private String userId;
    private String userName;
    private String realName;
    private String schoolName;
    private String className;
    private String gradeName;
    private boolean teacher;
    private boolean student;
    private Integer vipStatus;
    private String profileUrl;
    private String vipDate1;
    private List<ClassItem> clazzList;

    /**
     * 1小学一年级，2小学二年级...
     */
    private int gradeCode = 0;

    /**
     * -1代表幼儿园，大于0代表普通学校
     */
    private int schoolCode = 0;

    /**
     * 1. 校长 2.教务处主任 3.年级组长 4.班主任 5.普通教师 6.学生 7.家长 9.系统管理员
     */
    private int role = 0;

    /**
     * 标识是否显示班级消息，0显示，1不显示，默认显示
     */
    private int classMessage = 0;

    //新增
    private String classMaster;
    private String cookie;
    private String ecardNumber;
    private int subjectId;
    private String tags;
    private Object vipDate;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }

    public String getSchoolbaseinfoId() {
        return schoolbaseinfoId;
    }

    public void setSchoolbaseinfoId(String schoolbaseinfoId) {
        this.schoolbaseinfoId = schoolbaseinfoId;
    }

    public String getGradeinfoId() {
        return gradeinfoId;
    }

    public void setGradeinfoId(String gradeinfoId) {
        this.gradeinfoId = gradeinfoId;
    }

    public String getClassinfoId() {
        return classinfoId;
    }

    public void setClassinfoId(String classinfoId) {
        this.classinfoId = classinfoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getGradeCode() {
        return gradeCode;
    }

    public void setGradeCode(int gradeCode) {
        this.gradeCode = gradeCode;
    }

    public Integer getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(Integer vipStatus) {
        this.vipStatus = vipStatus;
    }

    public int getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(int schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getVipDate1() {
        return vipDate1;
    }

    public void setVipDate1(String vipDate1) {
        this.vipDate1 = vipDate1;
    }

    public int getClassMessage() {
        return classMessage;
    }

    public void setClassMessage(int classMessage) {
        this.classMessage = classMessage;
    }

    public List<ClassItem> getClazzList() {
        return clazzList;
    }

    public void setClazzList(List<ClassItem> clazzList) {
        this.clazzList = clazzList;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(String classMaster) {
        this.classMaster = classMaster;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getEcardNumber() {
        return ecardNumber;
    }

    public void setEcardNumber(String ecardNumber) {
        this.ecardNumber = ecardNumber;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Object getVipDate() {
        return vipDate;
    }

    public void setVipDate(Object vipDate) {
        this.vipDate = vipDate;
    }

    /**
     * 判断角色是否为学生
     *
     * @param role 角色标识代码
     * @return
     */
    public static boolean isStudentRole(int role) {
        if (role == 6 || role == 7) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断角色是否为教师
     *
     * @param role 角色标识代码
     * @return
     */
    public static boolean isTeacherRole(int role) {
        if (role >= 1 && role <= 5) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断学校是否是幼儿园
     *
     * @param schoolCode 学校标识代码
     * @return
     */
    public static boolean isKindergarten(int schoolCode) {
        if (schoolCode < 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从本地配置文件中读取保存过的用户信息
     *
     * @param context
     * @return
     */
    public static User getUser(Context context) {
        User u = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                MCon.SHARED_PREFERENCES_USER_INFO, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("userJson", "");
        // MLog.i("从配置文件读取用户信息userJson-->" + userJson);
        if (!CheckUtil.stringIsBlank(userJson)) {
            GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = builder.create();
            u = gson.fromJson(userJson, User.class);
        }
        return u;
    }

}
