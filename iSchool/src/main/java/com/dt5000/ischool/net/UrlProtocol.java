package com.dt5000.ischool.net;

/**
 * http接口协议
 *
 * @author 周锋
 * @Date 2014-5-19
 * @ClassInfo com.dt5000.ischool.utils.net-UrlProtocol.java
 * @Description
 */
public interface UrlProtocol {

    /**
     * <b>home项目接口地址，参数如下：</b>
     * <ul>
     * <li>requestData = {operation:{...},public:{...}}</li>
     * <li>session = 3b65450bb50a4439b486a02c5b6811bb</li>
     * </ul>
     * <b>URL参数生成步骤：</b>
     * <ol>
     * <li>拼接逻辑参数部分operation</li>
     * <li>根据operation生成sign签名</li>
     * <li>拼接公共参数部分public，需要用到sign</li>
     * <li>生成httpPost参数requestData</li>
     * <li>根据requestData生成httpPost参数session</li>
     * </ol>
     * <b>sign签名如下：</b> <br>
     * sign = Md5Encrypt.md5(operation + time + SignKey,"utf-8") <br>
     * <br>
     * <b> session签名如下：</b> <br>
     * session = Md5Encrypt.md5(requestData + SessionKey,"utf-8") <br>
     * <br>
     */
    // 苏州学堂
//    String MAIN_HOST = "http://61.155.146.199:8080/mobile/service.do";
    String MAIN_HOST = "http://sz.aroundu.net/mobile/service.do";//最初地址
//    String MAIN_HOST = "http://61.155.146.199/mobile/service.do";
    // 点通学堂
    // String MAIN_HOST = "http://www.aroundu.net/home/service.do";
    // local
    // String MAIN_HOST = "http://192.168.128.123:8080/mobile/service.do";
    // test
//    String MAIN_HOST = "http://192.168.128.90:8080/mobile/service.do";
    // slw
    // String MAIN_HOST = "http://222.92.129.82:60080/mobile/service.do";
    // bjl
    // String MAIN_HOST = "http://192.168.128.126:8088/mobile/service.do";

//     String MAIN_HOST = "http://192.168.128.129:8080/mobile/service.do";

    String WEB = "http://sz.aroundu.net/wxweike/index.action";
    /**
     * homework项目地址
     */
 //   String HOMEWORK_HOST = "http://61.155.146.199:8080/";
    String HOMEWORK_HOST = "http://sz.aroundu.net/";// 苏州学堂 线上地址
    //String HOMEWORK_HOST = "http://222.92.129.82:60080/";// 苏州学堂
    // String HOMEWORK_HOST = "http://www.aroundu.net/";// 点通学堂
    // String HOMEWORK_HOST = "http://192.168.128.90:8080/";// 点通学堂


    /**
     * 视频根地址
     */
    String VIDEO_HOST = HOMEWORK_HOST + "attach/android/";
    /**
     * 图片根地址
     */
    String IMAGE_HOST = HOMEWORK_HOST + "attach/";//线上地址

    /**
     * 附件地址
     */
    //String FILE_ATTACH = "http://61.155.146.199:8080/attchment/homework/";//测试地址
    String FILE_ATTACH = HOMEWORK_HOST + "attchment/homework/";//线上地址
    // String FILE_ATTACH ="http://222.92.129.82:60080/attchment/homework/";

    /**
     * 聊天大图地址
     */
    String LARGE_IMAGE = IMAGE_HOST + "android/";

    /**
     * 聊天小图地址
     */
    String SMALL_IMAGE = IMAGE_HOST + "android/small/";

    /**
     * 聊天中图地址
     */
    String MIDDLE_IMAGE = IMAGE_HOST + "android/middle/";

    /**
     * 软件更新接口地址
     */
    String APP_UPDATE = HOMEWORK_HOST + "upgrade.html";

    /**
     * 同步课堂视频列表主地址
     */
    String COURSE_VIDEOS = "http://218.4.157.2:8086/EPSforAndroid/EPSforAndroid.asmx";
//    String COURSE_VIDEOS = "http://222.92.102.87:8086/EPSforAndroid/EPSforAndroid.asmx";

    /**
     * 同步课堂同步练习题列表主地址
     */
    String COURSE_PRACTISE_LIST = "http://218.4.157.2:8086/studentf/h_stufwListAjax.aspx";

    /**
     * 同步课堂同步练习题单个题目WebView主地址
     */
    String COURSE_PRACTISE_DETAIL = "http://218.4.157.2:8086/studentf/h_stuflashworks.aspx";

    /**
     * 同步课堂同步练习题单个题目解析WebView主地址
     */
    String COURSE_PRACTISE_EXPLAIN = "http://218.4.157.2:8086/studentf/h_stuflashworksjx.aspx";

    /**
     * sign签名密钥
     */
    String SignKey = "78d2204c48baa1c6e30fb3dc7ab61d1e2b414b6ec6f3fc3406566e90657453f6f4d5ea7f7a06a2d2a231f1bbf330445959dd6a0be8963ed5d8176f57992768be";

    /**
     * session签名密钥
     */
    String SessionKey = "32cfd07149b91cad149b189db024eb110258af8691f752fa842e42d3b57e43d5712115ec41ee4d0090fb47796bec5b70ba085f6a1723263151f571f6ae2c62ad";

    /**
     * 登录，operationType=login
     */
    String OPERATION_TYPE_LOGIN = "login";

    /**
     * 更换手机，operationType=UserEditPhone
     */
    String OPERATION_TYPE_UserEditPhone = "UserEditPhone";

    /**
     * 通知已读，operationType=updateMsgStatus
     */
    String OPERATION_TYPE_NotityReader = "updateMsgStatus";

    /**
     * 订单详情，operationType=orders
     */
    String OPERATION_TYPE_Orders = "orders";

    /**
     * 作业列表，operationType=homework
     */
    String OPERATION_TYPE_HOMEWORK_LIST = "homework";
    String OPERATION_TYPE_GROUP_HOMEWORK_LIST = "groupHomework";
    /**
     * 作业详情：学生端，operationType=homeworkDetail
     */
    String OPERATION_TYPE_HOMEWORK_DETAIL = "homeworkDetail";

    /**
     * 作业评论列表：教师端，operationType=homeworkTeaDetailCom
     */
    String OPERATION_TYPE_HOMEWORK_COMMENT = "homeworkTeaDetailCom";

    /**
     * 发表作业评论，operationType=commentHomework
     */
    String OPERATION_TYPE_HOMEWORK_DETAIL_COMMENT = "commentHomework";

    /**
     * 单科作业列表，operationType=homeworkSingle
     */
    String OPERATION_TYPE_HOMEWORK_SUBJECT_LIST = "homeworkSingle";

    /**
     * 联系人列表，operationType=contact
     */
    String OPERATION_TYPE_CONTACT_LIST = "contact";

    /**
     * 自测评估试卷列表，operationType=selfTest
     */
    String OPERATION_TYPE_SELFTEST_LIST = "selfTest";

    /**
     * 自测评估试题列表，operationType=selfTestPaper
     */
    String OPERATION_TYPE_SELFTEST_PAGER = "selfTestPaper";

    /**
     * 自测评估单个试题，operationType=question
     */
    String OPERATION_TYPE_SELFTEST_SINGLE_ITEM = "question";

    /**
     * 自测评估难错套题收藏，operationType=saveQuestion
     */
    String OPERATION_TYPE_SELFTEST_SAVE_QUE = "saveQuestion";

    /**
     * 自测评估保存测试，operationType=saveTest
     */
    String OPERATION_TYPE_SELFTEST_SAVE_TEST = "saveTest";

    /**
     * 自测评估试题答案解析，operationType=analysis
     */
    String OPERATION_TYPE_SELFTEST_ANALYSIS = "analysis";

    /**
     * 退出登录，operationType=logoff
     */
    String OPERATION_TYPE_LOGIN_OFF = "logoff";

    /**
     * 意见反馈，operationType=feedBack
     */
    String OPERATION_TYPE_FEEDBACK = "feedBack";

    /**
     * 难题错题收藏列表，operationType=questionCollection
     */
    String OPERATION_TYPE_QUESTION_COLLECTION = "questionCollection";

    /**
     * 套题收藏列表，operationType=setsCollection
     */
    String OPERATION_TYPE_SETS_COLLECTION = "setsCollection";

    /**
     * 难题错题套题收藏-删除，operationType=collectionDelete
     */
    String OPERATION_TYPE_DELETE_COLLECTION = "collectionDelete";

    /**
     * 做题记录列表，operationType=record
     */
    String OPERATION_TYPE_TEST_RECORD = "record";

    /**
     * 做题记录删除，operationType=deleteRecord
     */
    String OPERATION_TYPE_DEL_RECORD = "deleteRecord";

    /**
     * 做题记录解析列表，operationType=analysisRecord
     */
    String OPERATION_TYPE_ANALYSIS_RECORD = "analysisRecord";

    /**
     * 我的成绩列表，operationType=myScore
     */
    String OPERATION_TYPE_MY_SCORE = "myScore";

    /**
     * 所带班级列表：教师端，operationType=homeworkTeaClass
     */
    String OPERATION_TYPE_TEACHER_CLASS = "homeworkTeaClass";

    String OPERATION_TYPE_TEACHER_GROUP = "homeworkTeaGroup";

    /**
     * 所在群组，operationType=userGroupList
     */
    String OPERATION_TYPE_GROUP = "userGroupList";

    /**
     * 同步个人消息，operationType=syncPersonal
     */
    String OPERATION_TYPE_SYNC_PERSION_MSG = "syncPersonal";

    /**
     * 同步班级消息，operationType=syncClassStu
     */
    String OPERATION_TYPE_SYNC_CLASS_MSG = "syncClassStu";

    /**
     * 同步群组消息，operationType=syncGroupStu
     */
    String OPERATION_TYPE_SYNC_GROUP_MSG = "syncGroupStu";

    /**
     * 发送个人消息，operationType=sendMessage
     */
    String OPERATION_TYPE_SEND_PERSON_MSG = "sendMessage";

    /**
     * 发送班级消息，operationType=sendGroupMessage
     */
    String OPERATION_TYPE_SEND_CLASS_MSG = "sendGroupMessage";

    /**
     * 发送群组消息，operationType=sendGroupsMessage
     */
    String OPERATION_TYPE_SEND_GROUP_MSG = "sendUserGroupMessage";

    /**
     * 群发消息：教师端，operationType=sendMessageByStu
     */
    String OPERATION_TYPE_SEND_MASS_MSG = "sendMessageByStu";

    /**
     * 上传头像，operationType=uploadUserIcon
     */
    String OPERATION_TYPE_UPLOAD_USER_ICON = "uploadUserIcon";

    /**
     * 班级相册列表：教师端，operationType=album
     */
    String OPERATION_TYPE_ALBUMS_TEACHER = "album";

    /**
     * 幼儿园相册列表：学生端，operationType=albumStudent
     */
    String OPERATION_TYPE_ALBUMS_STDUENT = "albumStudent";

    /**
     * 新建班级相册，operationType=albumAdd
     */
    String OPERATION_TYPE_ADD_ALBUMS = "albumAdd";

    /**
     * 新建班级相册前需要的年级班级数据，operationType=clazzInfo
     */
    String OPERATION_TYPE_ALBUMS_GRADE = "clazzInfo";

    /**
     * 班级相册上传图片，operationType=albumImageAdd
     */
    String OPERATION_TYPE_ALBUMS_UPLOAD_PIC = "albumImageAdd";

    /**
     * 班级相册中的图片列表，operationType=albumImage
     */
    String OPERATION_TYPE_ALBUMS_IMAGE = "albumImage";

    /**
     * VIP套餐，operationType=vipCombo
     */
    String OPERATION_TYPE_VIP_PAY_CPMBO = "vipCombo";

    /**
     * 判断VIP套餐是否已经购买，operationType=checkCombo
     */
    String OPERATION_TYPE_VIP_CHECK_COMBO = "checkCombo";

    /**
     * 生成支付订单，operationType=vipOrder
     */
    String OPERATION_TYPE_VIP_PAY_ORDER = "vipOrder";

    /**
     * 通知后台使订单生效，operationType=updateOrder
     */
    String OPERATION_TYPE_VIP_UPDATE_ORDER = "updateOrder";

    /**
     * VIP状态信息，operationType=vipStatus
     */
    String OPERATION_TYPE_VIP_STATUS = "vipStatus";

    /**
     * 学生成绩列表：教师端，operationType=score
     */
    String OPERATION_TYPE_STUDENT_SCORE_LIST = "score";

    /**
     * 科目列表，operationType=subject
     */
    String OPERATION_TYPE_SUBJECT = "subject";

    /**
     * 发布作业，operationType=addHomework
     */
    String OPERATION_TYPE_ADD_HOMEWORK = "addHomework";
    String OPERATION_TYPE_ADD_GROUP_HOMEWORK = "addGroupHomework";
    String OPERATION_TYPE_ADD_HOMEWORK_MULTIFILE = "addHomeworkWithMultifile";
    /**
     * 公文列表，operationType = listDocument
     */
    String OPERATION_TYPE_LISTDOCUMENT = "listDocument";

    /**
     * 公文详情，operationType=documentDetail
     */
    String OPERATION_TYPE_DOCUMENTDETAIL = "documentDetail";

    /**
     * 发布公文评论，operationType=documentReply
     */
    String OPERATION_TYPE_DOCUMENTREPLY = "documentReply";

    /**
     * 发布公文老师列表，operationType=teachers
     */
    String OPERATION_TYPE_PUBLISH_DOCUMENT_TEACHERS = "teachers";

    /**
     * 发布公文，operationType=addDocument
     */
    String OPERATION_TYPE_ADDDOCUMENT = "addDocument";

    /**
     * 转发公文，operationType=documentResend
     */
    String OPERATION_TYPE_DOCUMENTRESEND = "documentResend";

    /**
     * 关闭公文，operationType=documentClose
     */
    String OPERATION_TYPE_DOCUMENTCLOSE = "documentClose";

    /**
     * 删除关闭公文，operationType=documentDelete
     */
    String OPERATION_TYPE_DOCUMENTDELETE = "documentDelete";

    /**
     * 课程表，operationType=courseTable
     */
    String OPERATION_TYPE_COURSE_TABLE = "courseTable";

    /**
     * 上传图片前获取的token，operationType=token
     */
    String OPERATION_TYPE_UPLOAD_PIC_TOKEN = "token";

    /**
     * 数据列表：1日记、2校园公告、3教育资讯、4育儿经、5食谱
     */
    String OPERATION_TYPE_PUBLICATION = "publication";

    /**
     * 数据收藏：1作业、2育儿经
     */
    String OPERATION_TYPE_PUBLICATION_COLLECT = "addFavourite";

    /**
     * 数据收藏列表：1作业、2育儿经
     */
    String OPERATION_TYPE_PUBLICATION_COLLECT_LIST = "favourite";

    /**
     * 日记、校园公告、教育资讯、育儿经、食谱等页面的网页地址
     */
    String PUBLICATION_DETAIL = HOMEWORK_HOST + "phoneview_init.jhtml";
    ;

    /**
     * 点点GO网址
     */
    String OPERATION_TYPE_DIAN_DIAN_GO = "diandiango";

    /**
     * 班级博客列表
     */
    String OPERATION_TYPE_BLOG = "blogsClazz";

    /**
     * 班级博客点赞
     */
    String OPERATION_TYPE_BLOG_PRAISE = "blogsLike";

    /**
     * 班级博客评论
     */
    String OPERATION_TYPE_BLOG_COMMENT = "blogsComment";

    /**
     * 新增博客
     */
    String OPERATION_TYPE_BLOG_ADD = "blogsAdd";

    /**
     * 删除博客
     */
    String OPERATION_TYPE_BLOG_DELETE = "blogsDelete";

    /**
     * 删除博客评论
     */
    String OPERATION_TYPE_BLOG_COMMENT_DELETE = "blogsCommentDelete";

    /**
     * 新增博客上传图片的token
     */
    String OPERATION_TYPE_BLOG_ADD_TOKEN = "tokenAttach";

    /**
     * 日记列表：学生端
     */
    String OPERATION_TYPE_DIARY_LIST = "diaryStu";

    /**
     * 日记列表：教师端
     */
    String OPERATION_TYPE_DIARY_LIST_TEACHER = "diaryClazz";

    /**
     * 日记评价
     */
    String OPERATION_TYPE_DIARY_APPRAISE = "diaryAppraise";

    /**
     * 日记分享
     */
    String OPERATION_TYPE_DIARY_SHARE = "diaryBlogs";

    /**
     * 首页轮播
     */
    String OPERATION_TYPE_BANNER = "banner";

    /**
     * 修改密码
     */
    String OPERATION_TYPE_MOD_PASSWORD = "changePassword";

    /**
     * 动态更新
     */
    String OPERATION_TYPE_DYNAMICS = "dynamics";

    /**
     * 请假单列表
     */
    String OPERATION_TYPE_VACATE_LIST = "listLeaveRecord";

    /**
     * 请假单新增修改确认撤销
     */
    String OPERATION_TYPE_VACATE_SAVE = "saveLeaveRecord";

    /**
     * 请假单删除
     */
    String OPERATION_TYPE_VACATE_DELETE = "deleteLeaveRecord";

    /**
     * 考勤班级列表
     */
    String OPERATION_TYPE_ATTENDANCE_CLASS_LIST = "kqMessageTeacherClazzList";

    /**
     * 考勤列表：学生端
     */
    String OPERATION_TYPE_ATTENDANCE_LIST_STUDENT = "kqMessageStudentList";

    /**
     * 考勤列表：教师端
     */
    String OPERATION_TYPE_ATTENDANCE_LIST_TEACHER = "kqMessageTeacherList";

    /**
     * 考勤图片列表
     */
    String OPERATION_TYPE_ATTENDANCE_PICTURE_LIST = "kqMessageTeacherPhoto";
}
