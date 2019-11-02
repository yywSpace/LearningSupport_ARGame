package com.example.learningsupport_argame.tempararyfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.task.data.TaskInfo;

import java.util.List;

public class AbbTaskAdapter extends BaseAdapter {
    private List<TaskInfo> list;
    private Context con;
    private LayoutInflater mLayoutInflater;

    public AbbTaskAdapter(List<TaskInfo> list, Context con) {
        this.list = list;
        this.con = con;
        this.mLayoutInflater = LayoutInflater.from(con);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        TaskInfo.FormatTask ftask= list.get(i).new FormatTask();
        return ftask.getTid();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AbbTaskAdapter.ViewHolder viewHolder = null;

        if(view == null){

            view= mLayoutInflater.inflate(R.layout.task_item,viewGroup,false);

            viewHolder = new AbbTaskAdapter.ViewHolder();


            viewHolder.name = view.findViewById(R.id.task_redo_name);

            viewHolder.time=view.findViewById(R.id.task_redo_time);
            viewHolder.location=view.findViewById(R.id.task_redo_location);




            view.setTag(viewHolder);

        }else{

            viewHolder = (AbbTaskAdapter.ViewHolder)view.getTag();

        }

        TaskInfo task = list.get(i);

        viewHolder.name.setText(task.new FormatTask().getTaskName());
        viewHolder.time.setText(task.new FormatTask().getTaskTime());
        viewHolder.location.setText(task.new FormatTask().getTaskLocation());
        //这里就直接用msglist通过position获得msg然后填充就好了

        return view;

    }

    private static class ViewHolder{

       TextView name;
       TextView time;
       TextView location;

    }
}
