package com.abcs.huaqiaobang.yiyuanyungou.yyg.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.abcs.huaqiaobang.yiyuanyungou.R;


/**
 * Created by Administrator on 2016/6/3 0003.
 */
public class YYGBalacneDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
    public TextView t_income;
    public TextView t_expend;
    public TextView t_add_time;
    public TextView t_desc;
    public YYGBalacneDetailViewHolder(View itemView) {
        super(itemView);
        t_add_time = (TextView) itemView.findViewById(R.id.t_add_time);
        t_desc = (TextView) itemView.findViewById(R.id.t_desc);
        t_expend = (TextView) itemView.findViewById(R.id.t_expend);
        t_income = (TextView) itemView.findViewById(R.id.t_income);
    }

    @Override
    public void onClick(View v) {

    }
}
