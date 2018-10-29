package com.dt5000.ischool.adapter.teacher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.entity.ClassItem;
import com.dt5000.ischool.utils.MLog;

import java.util.List;

/**
 * 发布作业选择班级列表适配器：教师端
 *
 * @author 周锋
 * @date 2016年1月14日 下午1:50:39
 * @ClassInfo com.dt5000.ischool.adapter.teacher.HomeworkAddChooseClassListAdapter
 * @Description
 */
public class HomeworkAddChooseClassListAdapter extends BaseAdapter {

    private Context context;
    private List<ClassItem> list;
    private LayoutInflater inflater;

    public HomeworkAddChooseClassListAdapter(Context ctx, List<ClassItem> data) {
        this.context = ctx;
        this.list = data;
        inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.view_list_item_homework_add_choose_class, null);
            holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.rLayout_set_class = (RelativeLayout) convertView.findViewById(R.id.rLayout_set_class);
            holder.checkBox_permission = (CheckBox) convertView.findViewById(R.id.checkBox_permission);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ClassItem classItem = list.get(position);
        holder.txt_name.setText(classItem.getClassName());
        holder.checkBox_permission.setChecked(classItem.isChoose());

        holder.rLayout_set_class.setOnClickListener(new ClassItemClickListener(holder.checkBox_permission));

        holder.checkBox_permission.setOnCheckedChangeListener(new ClassItemCheckListener(position));

        return convertView;
    }

    class ClassItemClickListener implements OnClickListener {
        private CheckBox checkBox;

        public ClassItemClickListener() {

        }

        public ClassItemClickListener(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View v) {
            checkBox.performClick();
        }
    }

    class ClassItemCheckListener implements OnCheckedChangeListener {
        private int position;

        public ClassItemCheckListener() {
        }

        public ClassItemCheckListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            list.get(position).setChoose(isChecked);

            for (int i = 0; i < list.size(); i++) {
                ClassItem classItem = list.get(i);
                MLog.i(classItem.getClassName() + "---选中状态---" + classItem.isChoose());
            }
        }
    }

    public List<ClassItem> getClassList() {
        return list;
    }

    static class ViewHolder {
        RelativeLayout rLayout_set_class;
        TextView txt_name;
        CheckBox checkBox_permission;
    }

}
