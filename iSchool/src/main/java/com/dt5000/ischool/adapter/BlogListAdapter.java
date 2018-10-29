package com.dt5000.ischool.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.BlogPicPagerActivity;
import com.dt5000.ischool.entity.Blog;
import com.dt5000.ischool.entity.BlogComment;
import com.dt5000.ischool.entity.BlogLike;
import com.dt5000.ischool.entity.BlogPic;
import com.dt5000.ischool.entity.User;
import com.dt5000.ischool.net.UrlBulider;
import com.dt5000.ischool.net.UrlProtocol;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.DialogAlert;
import com.dt5000.ischool.utils.ImageLoaderUtil;
import com.dt5000.ischool.utils.MLog;
import com.dt5000.ischool.utils.TimeUtil;
import com.dt5000.ischool.utils.afinal.FinalHttp;
import com.dt5000.ischool.utils.afinal.http.AjaxCallBack;
import com.dt5000.ischool.utils.afinal.http.AjaxParams;
import com.dt5000.ischool.widget.NestedListView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 博客列表适配器
 * 
 * @author 周锋
 * @date 2016年2月2日 下午6:39:48
 * @ClassInfo com.dt5000.ischool.adapter.BlogListAdapter
 * @Description
 */
public class BlogListAdapter extends BaseAdapter {

	private Context context;
	private List<Blog> list;
	private LayoutInflater inflater;
	private FinalHttp finalHttp;
	private User user;
	private boolean canCommentBlog;
	private ImageLoader imageLoader;
	private OnClickShareListener clickShareListener;

	public BlogListAdapter(Context ctx, List<Blog> data, User user, boolean canCommentBlog) {
		this.context = ctx;
		this.list = data;
		this.user = user;
		this.canCommentBlog = canCommentBlog;
		inflater = LayoutInflater.from(context);
		finalHttp = new FinalHttp();
		imageLoader = ImageLoaderUtil.createSimple(context);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.view_list_item_blog, null);
			holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
			holder.txt_content = (TextView) convertView.findViewById(R.id.txt_content);
			holder.txt_upstr = (TextView) convertView.findViewById(R.id.txt_upstr);
			holder.lLayout_praise = (LinearLayout) convertView.findViewById(R.id.lLayout_praise);
			holder.lLayout_share = (LinearLayout) convertView.findViewById(R.id.lLayout_share);
			holder.lLayout_comment = (LinearLayout) convertView.findViewById(R.id.lLayout_comment);
			holder.img_head = (ImageView) convertView.findViewById(R.id.img_head);
			holder.img_praise = (ImageView) convertView.findViewById(R.id.img_praise);
			holder.img_comment = (ImageView) convertView.findViewById(R.id.img_comment);
			holder.txt_time = (TextView) convertView.findViewById(R.id.txt_time);
			holder.lLayout_upstr = (LinearLayout) convertView.findViewById(R.id.lLayout_upstr);
			holder.listview_comment = (NestedListView) convertView.findViewById(R.id.listview_comment);
			holder.lLayout_pic = (LinearLayout) convertView.findViewById(R.id.lLayout_pic);
			holder.lLayout_delete = (LinearLayout) convertView.findViewById(R.id.lLayout_delete);
			holder.lLayout_pic_single = (LinearLayout) convertView.findViewById(R.id.lLayout_pic_single);
			holder.img_pic_single = (ImageView) convertView.findViewById(R.id.img_pic_single);
			holder.lLayout_pic_more = (LinearLayout) convertView.findViewById(R.id.lLayout_pic_more);
			holder.lLayout_pic1 = (LinearLayout) convertView.findViewById(R.id.lLayout_pic1);
			holder.lLayout_pic2 = (LinearLayout) convertView.findViewById(R.id.lLayout_pic2);
			holder.lLayout_pic3 = (LinearLayout) convertView.findViewById(R.id.lLayout_pic3);
			holder.img1 = (ImageView) convertView.findViewById(R.id.img1);
			holder.img2 = (ImageView) convertView.findViewById(R.id.img2);
			holder.img3 = (ImageView) convertView.findViewById(R.id.img3);
			holder.img4 = (ImageView) convertView.findViewById(R.id.img4);
			holder.img5 = (ImageView) convertView.findViewById(R.id.img5);
			holder.img6 = (ImageView) convertView.findViewById(R.id.img6);
			holder.img7 = (ImageView) convertView.findViewById(R.id.img7);
			holder.img8 = (ImageView) convertView.findViewById(R.id.img8);
			holder.img9 = (ImageView) convertView.findViewById(R.id.img9);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Blog blog = list.get(position);

		// 设置名字
		holder.txt_name.setText(blog.getCreateName());

		// 设置头像
		imageLoader.displayImage(blog.getProfileUrl(), holder.img_head);

		// 设置内容
		String content = blog.getArticleDetail().getContent();
		if (CheckUtil.stringIsBlank(content)) {
			holder.txt_content.setVisibility(View.GONE);
		} else {
			holder.txt_content.setVisibility(View.VISIBLE);
			holder.txt_content.setText(content);
		}

		// 设置点赞人和点赞图片状态
		boolean userLike = false;// 标识用户是否点过赞
		List<BlogLike> likeList = blog.getLikeList();
		if (likeList != null && likeList.size() > 0) {
			holder.lLayout_upstr.setVisibility(View.VISIBLE);

			String likeName = "";
			for (BlogLike like : likeList) {
				likeName += like.getCreateName() + ",";

				if (user.getRealName().equals(like.getCreateName())) {
					userLike = true;// 如果点赞人列表中有用户姓名，则标识用户点过赞
				}
			}
			likeName = likeName.substring(0, likeName.lastIndexOf(","));

			holder.txt_upstr.setText(likeName);
		} else {
			holder.lLayout_upstr.setVisibility(View.GONE);
		}

		// 根据点赞状态设置点赞图片
		if (userLike) {
			holder.img_praise.setBackgroundResource(R.drawable.a_praise_checked);
		} else {
			holder.img_praise.setBackgroundResource(R.drawable.a_praise_pressed);
		}

		// 设置分享功能
		holder.lLayout_share.setOnClickListener(new ShareOnClickListener(blog, position));

		// 设置点赞功能
		holder.lLayout_praise.setOnClickListener(new PraiseOnClickListener(
				blog, holder.img_praise, holder.lLayout_upstr,
				holder.txt_upstr, likeList));

		// 判断是否能删除博客
		if (blog.isShowDelete()) {
			holder.lLayout_delete.setVisibility(View.VISIBLE);
		} else {
			holder.lLayout_delete.setVisibility(View.GONE);
		}

		// 设置删除功能
		holder.lLayout_delete.setOnClickListener(new DeleteOnClickListener(blog));

		// 设置时间
		Date date = TimeUtil.parseFullTime(blog.getCreateAt());
		holder.txt_time.setText(TimeUtil.messageFormat(date));

		// 设置评论
		BlogCommentListAdapter commentListAdapter = new BlogCommentListAdapter(context, blog.getCommentList());
		holder.listview_comment.setAdapter(commentListAdapter);

		// 设置评论功能
		holder.lLayout_comment.setOnClickListener(new CommentOnClickListener(
				blog, blog.getCommentList(), commentListAdapter));

		// 设置删除评论功能
		holder.listview_comment
				.setOnItemClickListener(new CommentItemClickListener(blog
						.getCommentList(), commentListAdapter));

		// 设置图片
		List<BlogPic> picList = blog.getAttachList();
		int picSize = picList.size();
		if (picSize <= 0) {// 没有图片
			holder.lLayout_pic.setVisibility(View.GONE);
		} else if (picSize == 1) {// 只有一张图片
			holder.lLayout_pic.setVisibility(View.VISIBLE);
			holder.lLayout_pic_single.setVisibility(View.VISIBLE);
			holder.lLayout_pic_more.setVisibility(View.GONE);
			imageLoader.displayImage(picList.get(0).getSmallImage(),
					holder.img_pic_single);
		} else {// 有多张图片
			holder.lLayout_pic.setVisibility(View.VISIBLE);
			holder.lLayout_pic_single.setVisibility(View.GONE);
			holder.lLayout_pic_more.setVisibility(View.VISIBLE);
			if (picSize > 1 && picSize <= 3) {// 显示一排
				holder.lLayout_pic1.setVisibility(View.VISIBLE);
				holder.lLayout_pic2.setVisibility(View.GONE);
				holder.lLayout_pic3.setVisibility(View.GONE);
				switch (picSize) {
				case 2:
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.GONE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					break;
				case 3:
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.VISIBLE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					imageLoader.displayImage(picList.get(2).getSmallImage(),
							holder.img3);
					break;
				}
			} else if (picSize > 3 && picSize <= 6) {// 显示两排
				holder.lLayout_pic1.setVisibility(View.VISIBLE);
				holder.lLayout_pic2.setVisibility(View.VISIBLE);
				holder.lLayout_pic3.setVisibility(View.GONE);
				switch (picSize) {
				case 4:
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.VISIBLE);
					holder.img4.setVisibility(View.VISIBLE);
					holder.img5.setVisibility(View.GONE);
					holder.img6.setVisibility(View.GONE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					imageLoader.displayImage(picList.get(2).getSmallImage(),
							holder.img3);
					imageLoader.displayImage(picList.get(3).getSmallImage(),
							holder.img4);
					break;
				case 5:
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.VISIBLE);
					holder.img4.setVisibility(View.VISIBLE);
					holder.img5.setVisibility(View.VISIBLE);
					holder.img6.setVisibility(View.GONE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					imageLoader.displayImage(picList.get(2).getSmallImage(),
							holder.img3);
					imageLoader.displayImage(picList.get(3).getSmallImage(),
							holder.img4);
					imageLoader.displayImage(picList.get(4).getSmallImage(),
							holder.img5);
					break;
				case 6:
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.VISIBLE);
					holder.img4.setVisibility(View.VISIBLE);
					holder.img5.setVisibility(View.VISIBLE);
					holder.img6.setVisibility(View.VISIBLE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					imageLoader.displayImage(picList.get(2).getSmallImage(),
							holder.img3);
					imageLoader.displayImage(picList.get(3).getSmallImage(),
							holder.img4);
					imageLoader.displayImage(picList.get(4).getSmallImage(),
							holder.img5);
					imageLoader.displayImage(picList.get(5).getSmallImage(),
							holder.img6);
					break;
				}
			} else if (picSize > 6) {// 显示三排
				holder.lLayout_pic1.setVisibility(View.VISIBLE);
				holder.lLayout_pic2.setVisibility(View.VISIBLE);
				holder.lLayout_pic3.setVisibility(View.VISIBLE);
				switch (picSize) {
				case 7:
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.VISIBLE);
					holder.img4.setVisibility(View.VISIBLE);
					holder.img5.setVisibility(View.VISIBLE);
					holder.img6.setVisibility(View.VISIBLE);
					holder.img7.setVisibility(View.VISIBLE);
					holder.img8.setVisibility(View.GONE);
					holder.img9.setVisibility(View.GONE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					imageLoader.displayImage(picList.get(2).getSmallImage(),
							holder.img3);
					imageLoader.displayImage(picList.get(3).getSmallImage(),
							holder.img4);
					imageLoader.displayImage(picList.get(4).getSmallImage(),
							holder.img5);
					imageLoader.displayImage(picList.get(5).getSmallImage(),
							holder.img6);
					imageLoader.displayImage(picList.get(6).getSmallImage(),
							holder.img7);
					break;
				case 8:
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.VISIBLE);
					holder.img4.setVisibility(View.VISIBLE);
					holder.img5.setVisibility(View.VISIBLE);
					holder.img6.setVisibility(View.VISIBLE);
					holder.img7.setVisibility(View.VISIBLE);
					holder.img8.setVisibility(View.VISIBLE);
					holder.img9.setVisibility(View.GONE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					imageLoader.displayImage(picList.get(2).getSmallImage(),
							holder.img3);
					imageLoader.displayImage(picList.get(3).getSmallImage(),
							holder.img4);
					imageLoader.displayImage(picList.get(4).getSmallImage(),
							holder.img5);
					imageLoader.displayImage(picList.get(5).getSmallImage(),
							holder.img6);
					imageLoader.displayImage(picList.get(6).getSmallImage(),
							holder.img7);
					imageLoader.displayImage(picList.get(7).getSmallImage(),
							holder.img8);
					break;
				case 9:
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.VISIBLE);
					holder.img4.setVisibility(View.VISIBLE);
					holder.img5.setVisibility(View.VISIBLE);
					holder.img6.setVisibility(View.VISIBLE);
					holder.img7.setVisibility(View.VISIBLE);
					holder.img8.setVisibility(View.VISIBLE);
					holder.img9.setVisibility(View.VISIBLE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					imageLoader.displayImage(picList.get(2).getSmallImage(),
							holder.img3);
					imageLoader.displayImage(picList.get(3).getSmallImage(),
							holder.img4);
					imageLoader.displayImage(picList.get(4).getSmallImage(),
							holder.img5);
					imageLoader.displayImage(picList.get(5).getSmallImage(),
							holder.img6);
					imageLoader.displayImage(picList.get(6).getSmallImage(),
							holder.img7);
					imageLoader.displayImage(picList.get(7).getSmallImage(),
							holder.img8);
					imageLoader.displayImage(picList.get(8).getSmallImage(),
							holder.img9);
					break;
				default:// 超过9张只显示9张
					holder.img1.setVisibility(View.VISIBLE);
					holder.img2.setVisibility(View.VISIBLE);
					holder.img3.setVisibility(View.VISIBLE);
					holder.img4.setVisibility(View.VISIBLE);
					holder.img5.setVisibility(View.VISIBLE);
					holder.img6.setVisibility(View.VISIBLE);
					holder.img7.setVisibility(View.VISIBLE);
					holder.img8.setVisibility(View.VISIBLE);
					holder.img9.setVisibility(View.VISIBLE);

					imageLoader.displayImage(picList.get(0).getSmallImage(),
							holder.img1);
					imageLoader.displayImage(picList.get(1).getSmallImage(),
							holder.img2);
					imageLoader.displayImage(picList.get(2).getSmallImage(),
							holder.img3);
					imageLoader.displayImage(picList.get(3).getSmallImage(),
							holder.img4);
					imageLoader.displayImage(picList.get(4).getSmallImage(),
							holder.img5);
					imageLoader.displayImage(picList.get(5).getSmallImage(),
							holder.img6);
					imageLoader.displayImage(picList.get(6).getSmallImage(),
							holder.img7);
					imageLoader.displayImage(picList.get(7).getSmallImage(),
							holder.img8);
					imageLoader.displayImage(picList.get(8).getSmallImage(),
							holder.img9);
					break;
				}
			}
		}

		// 设置图片点击事件
		holder.img_pic_single.setOnClickListener(new PicOnClickListener(0,
				picList));
		holder.img1.setOnClickListener(new PicOnClickListener(0, picList));
		holder.img2.setOnClickListener(new PicOnClickListener(1, picList));
		holder.img3.setOnClickListener(new PicOnClickListener(2, picList));
		holder.img4.setOnClickListener(new PicOnClickListener(3, picList));
		holder.img5.setOnClickListener(new PicOnClickListener(4, picList));
		holder.img6.setOnClickListener(new PicOnClickListener(5, picList));
		holder.img7.setOnClickListener(new PicOnClickListener(6, picList));
		holder.img8.setOnClickListener(new PicOnClickListener(7, picList));
		holder.img9.setOnClickListener(new PicOnClickListener(8, picList));

		return convertView;
	}

	/**
	 * 图片点击监听
	 */
	class PicOnClickListener implements OnClickListener {
		private int position;
		private List<BlogPic> picList;

		public PicOnClickListener(int position, List<BlogPic> picList) {
			this.position = position;
			this.picList = picList;
		}

		@Override
		public void onClick(View v) {
			try {
				if (position < picList.size()) {
					List<String> imgList = new ArrayList<String>();
					for (BlogPic pic : picList) {
						imgList.add(pic.getMediumImage());
					}

					Bundle bundle = new Bundle();
					bundle.putInt("index", position);
					bundle.putSerializable("albumImageList",
							(Serializable) imgList);
					Intent intent = new Intent(context,
							BlogPicPagerActivity.class);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class ShareOnClickListener implements OnClickListener {
		private Blog blog;
		private int position;

		public ShareOnClickListener(Blog blog, int position) {
			this.blog = blog;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (clickShareListener != null) {
				clickShareListener.toShare(blog, position);
			}
		}
	}

	/**
	 * 点赞监听
	 */
	class PraiseOnClickListener implements OnClickListener {
		private Blog blog;
		private ImageView img_praise;
		private LinearLayout lLayout_upstr;
		private TextView txt_upstr;
		private List<BlogLike> likeList;

		public PraiseOnClickListener() {
		}

		public PraiseOnClickListener(Blog blog, ImageView img_praise,
				LinearLayout lLayout_upstr, TextView txt_upstr,
				List<BlogLike> likeList) {
			this.blog = blog;
			this.img_praise = img_praise;
			this.lLayout_upstr = lLayout_upstr;
			this.txt_upstr = txt_upstr;
			this.likeList = likeList;
		}

		@Override
		public void onClick(View v) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("operationType", UrlProtocol.OPERATION_TYPE_BLOG_PRAISE);
			map.put("userId", user.getUserId());
			map.put("blogId", blog.getId());
			map.put("role", String.valueOf(user.getRole()));
			AjaxParams ajaxParams = UrlBulider.getAjaxParams(map, context,
					user.getUserId());
			finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
					new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String t) {
							MLog.i("点赞返回结果：" + t);
							try {
								JSONObject jsonObject = new JSONObject(t);
								String resultStatus = jsonObject
										.optString("resultStatus");

								if ("200".equals(resultStatus)) {
									String message = jsonObject
											.optString("message");

									if ("like".equals(message)) {// 点赞
										lLayout_upstr
												.setVisibility(View.VISIBLE);

										if (likeList == null) {
											likeList = new ArrayList<BlogLike>();
										}

										// 将点赞人姓名添加到第一个
										likeList.add(
												0,
												new BlogLike(user.getRealName()));
										String likeName = "";
										for (BlogLike like : likeList) {
											likeName += like.getCreateName()
													+ ",";
										}
										likeName = likeName.substring(0,
												likeName.lastIndexOf(","));
										txt_upstr.setText(likeName);

										// 设置点赞图片
										img_praise
												.setBackgroundResource(R.drawable.a_praise_checked);
									} else if ("dislike".equals(message)) {// 取消点赞
										// 将点赞人姓名移除
										int removeIndex = -1;
										for (int i = 0; i < likeList.size(); i++) {
											if (user.getRealName().equals(
													likeList.get(i)
															.getCreateName())) {
												removeIndex = i;
											}
										}
										if (removeIndex != -1) {
											likeList.remove(removeIndex);
										}

										if (likeList.size() > 0) {// 取消点赞后如果还有其余人的点赞则保留
											String likeName = "";
											for (BlogLike like : likeList) {
												likeName += like
														.getCreateName() + ",";
											}
											likeName = likeName.substring(0,
													likeName.lastIndexOf(","));
											txt_upstr.setText(likeName);
										} else {// 取消点赞后如果没有其它人的点赞，则把点赞布局隐藏
											lLayout_upstr
													.setVisibility(View.GONE);
										}

										// 设置点赞图片
										img_praise
												.setBackgroundResource(R.drawable.a_praise_pressed);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

	/**
	 * 删除监听
	 */
	class DeleteOnClickListener implements OnClickListener {
		private Blog blog;

		public DeleteOnClickListener() {
		}

		public DeleteOnClickListener(Blog blog) {
			this.blog = blog;
		}

		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(context)
					.setMessage("是否删除本条博客？")
					.setPositiveButton("删除",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Map<String, String> map = new HashMap<String, String>();
									map.put("operationType",
											UrlProtocol.OPERATION_TYPE_BLOG_DELETE);
									map.put("blogId", blog.getId());
									AjaxParams ajaxParams = UrlBulider
											.getAjaxParams(map, context,
													user.getUserId());
									finalHttp.post(UrlProtocol.MAIN_HOST,
											ajaxParams,
											new AjaxCallBack<String>() {
												@Override
												public void onSuccess(String t) {
													MLog.i("删除博客返回结果：" + t);
													try {
														JSONObject jsonObject = new JSONObject(
																t);
														String resultStatus = jsonObject
																.optString("resultStatus");
														if ("200"
																.equals(resultStatus)) {
															list.remove(blog);
															notifyData();
														}
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											});
								}
							}).setNegativeButton("取消", null).show();
		}
	}

	/**
	 * 点击评论监听
	 */
	class CommentOnClickListener implements OnClickListener {
		private Blog blog;
		private List<BlogComment> commentList;
		private BlogCommentListAdapter blogCommentListAdapterStudent;

		public CommentOnClickListener() {
		}

		public CommentOnClickListener(Blog blog, List<BlogComment> list,
				BlogCommentListAdapter adapter) {
			this.blog = blog;
			this.commentList = list;
			this.blogCommentListAdapterStudent = adapter;
		}

		@Override
		public void onClick(View v) {
			if (canCommentBlog) {
				showCommentDialog(blog, commentList,
						blogCommentListAdapterStudent);
			} else {
				DialogAlert.show(context, "评论已关闭");
			}
		}
	}

	class CommentItemClickListener implements OnItemClickListener {
		private List<BlogComment> commentList;
		private BlogCommentListAdapter blogCommentListAdapter;

		public CommentItemClickListener() {
		}

		public CommentItemClickListener(List<BlogComment> list,
				BlogCommentListAdapter adapter) {
			this.commentList = list;
			this.blogCommentListAdapter = adapter;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			deleteComment(position, commentList, blogCommentListAdapter);
		}
	}

	/**
	 * 显示评论输入框
	 * 
	 * @param blog
	 * @param list
	 * @param adapter
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	private void showCommentDialog(final Blog blog,
			final List<BlogComment> list, final BlogCommentListAdapter adapter) {
		View view = inflater.inflate(R.layout.view_blog_comment_dialog, null);
		final EditText edit_content = (EditText) view
				.findViewById(R.id.edit_content);
		Button btn_public = (Button) view.findViewById(R.id.btn_public);
		final Dialog dialog = new Dialog(context, R.style.CommonDialogStyle);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.BOTTOM);
		WindowManager windowManager = ((Activity) context).getWindowManager();
		Display defaultDisplay = windowManager.getDefaultDisplay();
		lp.width = defaultDisplay.getWidth();
		dialogWindow.setAttributes(lp);

		btn_public.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String content = edit_content.getText().toString().trim();
				if (!CheckUtil.stringIsBlank(content)) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("operationType",
							UrlProtocol.OPERATION_TYPE_BLOG_COMMENT);
					map.put("userId", user.getUserId());
					map.put("blogId", blog.getId());
					map.put("content", content);
					map.put("role", String.valueOf(user.getRole()));
					AjaxParams ajaxParams = UrlBulider.getAjaxParams(map,
							context, user.getUserId());
					finalHttp.post(UrlProtocol.MAIN_HOST, ajaxParams,
							new AjaxCallBack<String>() {
								@Override
								public void onSuccess(String t) {
									MLog.i("评论返回结果：" + t);
									try {
										JSONObject jsonObject = new JSONObject(
												t);
										String resultStatus = jsonObject
												.optString("resultStatus");
										if ("200".equals(resultStatus)) {
											String blogCommentId = jsonObject
													.optString("blogCommentId");
											BlogComment comment = new BlogComment();
											comment.setId(blogCommentId);
											comment.setContent(content);
											comment.setCreateName(user
													.getRealName());
											comment.setCreateBy(user
													.getUserId());
											list.add(comment);
											adapter.notifyDataSetChanged();
											notifyData();
										}
										dialog.dismiss();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
				}
			}
		});
		dialog.show();
	}

	private void deleteComment(final int position,
			final List<BlogComment> list,
			final BlogCommentListAdapter blogCommentListAdapter) {
		final BlogComment deleteComment = list.get(position);
		if (!user.getUserId().equals(deleteComment.getCreateBy())) {
			MLog.i("不是自己的评论，无法操作");
			return;
		}

		new AlertDialog.Builder(context)
				.setMessage("请选择操作")
				.setPositiveButton("删除评论",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("operationType",
										UrlProtocol.OPERATION_TYPE_BLOG_COMMENT_DELETE);
								map.put("commentId", deleteComment.getId());
								map.put("userId", user.getUserId());
								AjaxParams ajaxParams = UrlBulider
										.getAjaxParams(map, context,
												user.getUserId());
								String httpURL = UrlBulider.getHttpURL(map,
										context, user.getUserId());
								MLog.i("删除博客评论地址：" + httpURL);

								finalHttp.post(UrlProtocol.MAIN_HOST,
										ajaxParams, new AjaxCallBack<String>() {
											@Override
											public void onSuccess(String t) {
												MLog.i("删除博客评论返回结果：" + t);
												try {
													JSONObject jsonObject = new JSONObject(
															t);
													String resultStatus = jsonObject
															.optString("resultStatus");
													if ("200"
															.equals(resultStatus)) {
														list.remove(position);
														blogCommentListAdapter
																.notifyDataSetChanged();
														notifyData();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										});
							}
						}).setNegativeButton("取消", null).show();
	}

	private void notifyData() {
		this.notifyDataSetChanged();
	}

	static class ViewHolder {
		TextView txt_name;
		TextView txt_time;
		TextView txt_content;
		LinearLayout lLayout_upstr;
		TextView txt_upstr;
		LinearLayout lLayout_delete;
		LinearLayout lLayout_praise;
		LinearLayout lLayout_share;
		LinearLayout lLayout_comment;
		ImageView img_head;
		ImageView img_praise;
		ImageView img_comment;
		NestedListView listview_comment;
		LinearLayout lLayout_pic;
		ImageView img_pic_single;
		LinearLayout lLayout_pic_single;
		LinearLayout lLayout_pic_more;
		LinearLayout lLayout_pic1;
		LinearLayout lLayout_pic2;
		LinearLayout lLayout_pic3;
		ImageView img1;
		ImageView img2;
		ImageView img3;
		ImageView img4;
		ImageView img5;
		ImageView img6;
		ImageView img7;
		ImageView img8;
		ImageView img9;
	}

	public interface OnClickShareListener {
		public void toShare(Blog blog, int position);
	}

	public void setOnClickShareListener(OnClickShareListener listener) {
		this.clickShareListener = listener;
	}

}
