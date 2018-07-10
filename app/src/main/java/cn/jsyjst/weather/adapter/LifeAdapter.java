package cn.jsyjst.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.entity.Life;

/**
 * Created by 残渊 on 2018/5/18.
 */


/**
 * 生活指数RecyclerView适配器
 */
public class LifeAdapter extends RecyclerView.Adapter<LifeAdapter.ViewHolder> {

    private List<Life> mLifeList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView lifeIv;
        TextView briefTv;
        TextView lifeNameTv;

        public ViewHolder(View view){
            super(view);
            lifeIv=view.findViewById(R.id.iv_life);
            briefTv=view.findViewById(R.id.tv_life_brief);
            lifeNameTv=view.findViewById(R.id.tv_life_name);
        }
    }

    public LifeAdapter(List<Life> lifeList){
        mLifeList=lifeList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.life_recyclerview_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,int position){
        Life life=mLifeList.get(position);
        viewHolder.lifeIv.setImageResource(life.getImageId());
        viewHolder.briefTv.setText(life.getBrief());
        viewHolder.lifeNameTv.setText(life.getLifeName());
    }
    @Override
    public int getItemCount(){
        return mLifeList.size();
    }

}
