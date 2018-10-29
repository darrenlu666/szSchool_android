package com.dt5000.ischool.utils;

import java.util.ArrayList;
import java.util.List;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.EmojiMatch;

/**
 * 聊天页面表情辅助类
 * 
 * @author 周锋
 * @date 2016年1月20日 上午9:53:01
 * @ClassInfo com.dt5000.ischool.utils.EmojiUtil
 * @Description
 */
public class EmojiUtil {

	private static List<EmojiMatch> emojiMatchs;

	static {
		emojiMatchs = new ArrayList<EmojiMatch>();
		emojiMatchs.add(new EmojiMatch("[ok]", R.drawable.em_ok));
		emojiMatchs.add(new EmojiMatch("[yeah]", R.drawable.em_yeah));
		emojiMatchs.add(new EmojiMatch("[爱你]", R.drawable.em_aini));
		emojiMatchs.add(new EmojiMatch("[八字眉]", R.drawable.em_bazimei));
		emojiMatchs.add(new EmojiMatch("[粑粑]", R.drawable.em_papa));
		emojiMatchs.add(new EmojiMatch("[白骨精]", R.drawable.em_baigujing));
		emojiMatchs.add(new EmojiMatch("[保重]", R.drawable.em_baozhong));
		emojiMatchs.add(new EmojiMatch("[抱抱]", R.drawable.em_baobao));
		emojiMatchs.add(new EmojiMatch("[匕首]", R.drawable.em_bishou));
		emojiMatchs.add(new EmojiMatch("[鄙视]", R.drawable.em_bishi));
		emojiMatchs.add(new EmojiMatch("[闭嘴]", R.drawable.em_bizui));
		emojiMatchs.add(new EmojiMatch("[不理你]", R.drawable.em_bulini));
		emojiMatchs.add(new EmojiMatch("[擦汗]", R.drawable.em_cahan));
		emojiMatchs.add(new EmojiMatch("[菜刀]", R.drawable.em_caidao));
		emojiMatchs.add(new EmojiMatch("[吃饭]", R.drawable.em_chifan));
		emojiMatchs.add(new EmojiMatch("[呲牙]", R.drawable.em_ziya));
		emojiMatchs.add(new EmojiMatch("[打哈欠]", R.drawable.em_dahaqian));
		emojiMatchs.add(new EmojiMatch("[打击]", R.drawable.em_daji));
		emojiMatchs.add(new EmojiMatch("[大骂]", R.drawable.em_dama));
		emojiMatchs.add(new EmojiMatch("[蛋糕]", R.drawable.em_dangao));
		emojiMatchs.add(new EmojiMatch("[凋零]", R.drawable.em_diaoling));
		emojiMatchs.add(new EmojiMatch("[逗你玩]", R.drawable.em_douniwan));
		emojiMatchs.add(new EmojiMatch("[发狂]", R.drawable.em_fakuang));
		emojiMatchs.add(new EmojiMatch("[伐开心]", R.drawable.em_bukaixin));
		emojiMatchs.add(new EmojiMatch("[翻白眼]", R.drawable.em_baiyan));
		emojiMatchs.add(new EmojiMatch("[犯困]", R.drawable.em_fankun));
		emojiMatchs.add(new EmojiMatch("[奋斗]", R.drawable.em_fendou));
		emojiMatchs.add(new EmojiMatch("[尴尬]", R.drawable.em_ganga));
		emojiMatchs.add(new EmojiMatch("[勾引]", R.drawable.em_gouyin));
		emojiMatchs.add(new EmojiMatch("[鼓掌]", R.drawable.em_guzhang));
		emojiMatchs.add(new EmojiMatch("[害羞]", R.drawable.em_haixiu));
		emojiMatchs.add(new EmojiMatch("[好饿]", R.drawable.em_haoe));
		emojiMatchs.add(new EmojiMatch("[哼]", R.drawable.em_heng));
		emojiMatchs.add(new EmojiMatch("[坏笑]", R.drawable.em_huaixiao));
		emojiMatchs.add(new EmojiMatch("[奸笑]", R.drawable.em_jianxiao));
		emojiMatchs.add(new EmojiMatch("[惊呆]", R.drawable.em_jingdai));
		emojiMatchs.add(new EmojiMatch("[惊恐]", R.drawable.em_jingkong));
		emojiMatchs.add(new EmojiMatch("[惊讶]", R.drawable.em_jingya));
		emojiMatchs.add(new EmojiMatch("[咖啡]", R.drawable.em_kafei));
		emojiMatchs.add(new EmojiMatch("[可爱]", R.drawable.em_keai));
		emojiMatchs.add(new EmojiMatch("[可怜]", R.drawable.em_kelian));
		emojiMatchs.add(new EmojiMatch("[哭]", R.drawable.em_ku));
		emojiMatchs.add(new EmojiMatch("[酷]", R.drawable.em_banku));
		emojiMatchs.add(new EmojiMatch("[快哭了]", R.drawable.em_kuaikule));
		emojiMatchs.add(new EmojiMatch("[狂笑]", R.drawable.em_kuangxiao));
		emojiMatchs.add(new EmojiMatch("[困]", R.drawable.em_shuijiao));
		emojiMatchs.add(new EmojiMatch("[篮球]", R.drawable.em_lanqiu));
		emojiMatchs.add(new EmojiMatch("[冷汗]", R.drawable.em_lenghan));
		emojiMatchs.add(new EmojiMatch("[礼物]", R.drawable.em_liwu));
		emojiMatchs.add(new EmojiMatch("[流鼻涕]", R.drawable.em_liubiti));
		emojiMatchs.add(new EmojiMatch("[流汗]", R.drawable.em_liuhan));
		emojiMatchs.add(new EmojiMatch("[流泪]", R.drawable.em_liulei));
		emojiMatchs.add(new EmojiMatch("[么么哒]", R.drawable.em_memeda));
		emojiMatchs.add(new EmojiMatch("[玫瑰]", R.drawable.em_meigui));
		emojiMatchs.add(new EmojiMatch("[呕吐]", R.drawable.em_outu));
		emojiMatchs.add(new EmojiMatch("[炮兵]", R.drawable.em_paobing));
		emojiMatchs.add(new EmojiMatch("[啤酒]", R.drawable.em_pijiu));
		emojiMatchs.add(new EmojiMatch("[瓢虫]", R.drawable.em_piaochong));
		emojiMatchs.add(new EmojiMatch("[乒乓]", R.drawable.em_pingpang));
		emojiMatchs.add(new EmojiMatch("[强]", R.drawable.em_qiang));
		emojiMatchs.add(new EmojiMatch("[糗大了]", R.drawable.em_qiudale));
		emojiMatchs.add(new EmojiMatch("[拳头]", R.drawable.em_quantou));
		emojiMatchs.add(new EmojiMatch("[如花]", R.drawable.em_ruhua));
		emojiMatchs.add(new EmojiMatch("[弱]", R.drawable.em_ruo));
		emojiMatchs.add(new EmojiMatch("[色]", R.drawable.em_se));
		emojiMatchs.add(new EmojiMatch("[闪电]", R.drawable.em_shandian));
		emojiMatchs.add(new EmojiMatch("[衰衰]", R.drawable.em_shuai));
		emojiMatchs.add(new EmojiMatch("[偷笑]", R.drawable.em_touxiao));
		emojiMatchs.add(new EmojiMatch("[头痛]", R.drawable.em_toutong));
		emojiMatchs.add(new EmojiMatch("[晚安]", R.drawable.em_wanan));
		emojiMatchs.add(new EmojiMatch("[微笑]", R.drawable.em_weixiao));
		emojiMatchs.add(new EmojiMatch("[委屈]", R.drawable.em_weiqu));
		emojiMatchs.add(new EmojiMatch("[吻]", R.drawable.em_wen));
		emojiMatchs.add(new EmojiMatch("[握手]", R.drawable.em_wushou));
		emojiMatchs.add(new EmojiMatch("[西瓜]", R.drawable.em_xigua));
		emojiMatchs.add(new EmojiMatch("[小骂]", R.drawable.em_xiaoma));
		emojiMatchs.add(new EmojiMatch("[小拇指]", R.drawable.em_xiaomuzhi));
		emojiMatchs.add(new EmojiMatch("[小声点]", R.drawable.em_xiaoshengdian));
		emojiMatchs.add(new EmojiMatch("[炫金牙]", R.drawable.em_xuanjinya));
		emojiMatchs.add(new EmojiMatch("[一]", R.drawable.em_yi));
		emojiMatchs.add(new EmojiMatch("[疑惑]", R.drawable.em_yihuo));
		emojiMatchs.add(new EmojiMatch("[晕乎乎]", R.drawable.em_yunhuhu));
		emojiMatchs.add(new EmojiMatch("[再见]", R.drawable.em_zaijian));
		emojiMatchs.add(new EmojiMatch("[早安]", R.drawable.em_zaoan));
		emojiMatchs.add(new EmojiMatch("[炸弹]", R.drawable.em_zhadan));
		emojiMatchs.add(new EmojiMatch("[猪猪]", R.drawable.em_zhuzhu));
		emojiMatchs.add(new EmojiMatch("[足球]", R.drawable.em_zhuqiu));
	}

	public static int getEmojiResId(String str) {
		for (int i = 0; i < emojiMatchs.size(); i++) {
			EmojiMatch match = emojiMatchs.get(i);
			if (match.getName().equals(str)) {
				return match.getResId();
			}
		}
		return -1;
	}

}
