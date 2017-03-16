package com.androidjp.traffichelper.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidjp.lib_great_recyclerview.base.BaseViewHolder;
import com.androidjp.lib_great_recyclerview.base.OnItemClickListener;
import com.androidjp.traffichelper.R;
import com.androidjp.traffichelper.data.pojo.Dialogue;
import com.androidjp.traffichelper.home.view.GlideCircleTransform;
import com.bumptech.glide.Glide;

import butterknife.Bind;

/**
 * 提问item布局
 * Created by androidjp on 2016/12/26.
 */

public class QuestionHolder extends BaseViewHolder<Dialogue> {
    @Bind(R.id.iv_user_pic)
    ImageView ivUserPic;
    @Bind(R.id.tv_word_user)
    TextView tvUserWord;

    /**
     * 新的自定义的基类构造方法：
     *
     * @param context ViewHolder所在上下文
     * @param root    依附的RecyclerView
     */
    public QuestionHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.layout_dialogue_question);
    }

    @Override
    protected void bindData(Dialogue itemValue, int position, OnItemClickListener<Dialogue> listener) {
        if (itemValue.getPic() == null)
            ivUserPic.setImageResource(R.drawable.user_blue);
        else
            ivUserPic.setImageBitmap(itemValue.getPic());
        tvUserWord.setText(itemValue.word);
    }
}
