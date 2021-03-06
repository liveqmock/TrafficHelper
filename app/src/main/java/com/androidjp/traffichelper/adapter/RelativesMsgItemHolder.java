package com.androidjp.traffichelper.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidjp.lib_custom_view.edittext.ClearEditText;
import com.androidjp.lib_custom_view.selector.JPSelectView;
import com.androidjp.lib_great_recyclerview.base.BaseViewHolder;
import com.androidjp.lib_great_recyclerview.base.OnItemClickListener;
import com.androidjp.traffichelper.R;
import com.androidjp.traffichelper.THApplication;
import com.androidjp.traffichelper.data.pojo.RelativeItemMsg;

import butterknife.Bind;

/**
 * 亲属和年龄选择界面 item
 * Created by androidjp on 2017/1/17.
 */

public class RelativesMsgItemHolder extends BaseViewHolder<RelativeItemMsg>{
    @Bind(R.id.iv_sub)
    ImageView ivSub;
    @Bind(R.id.jsv_relatives_msg)
    JPSelectView relationSelector;
    @Bind(R.id.cet_age)
    ClearEditText cetAge;

    /**
     * 新的自定义的基类构造方法：
     *
     * @param context   ViewHolder所在上下文
     * @param root      依附的RecyclerView
     */
    public RelativesMsgItemHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.layout_relatives_msg_item);
    }

    @Override
    protected void bindData(final RelativeItemMsg itemValue, final int position, final OnItemClickListener<RelativeItemMsg> listener) {
        ivSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onItemClick(itemValue,R.id.iv_sub,position);
                }
            }
        });
        relationSelector.setOnChangeStringListener(new JPSelectView.OnChangeStringListener() {
            @Override
            public void onFinishChangeString(String value) {
                itemValue.setRelation(relationSelector.getCurrentPos());
                if (listener!=null)
                    listener.onItemClick(itemValue,R.id.jsv_relatives_msg,position);
            }
        });
        cetAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int age;
                if (s==null || s.length()==0)
                    age = 0;
                else
                    age  = Integer.valueOf(s.toString());
                itemValue.setAge(age);
                if (listener!=null)
                    listener.onItemClick(itemValue,R.id.cet_age,position);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        relationSelector.setCurrentType(JPSelectView.TYPE_STRING).setStringList(THApplication.getContext().getResources().getStringArray(R.array.relation_array));
    }
}
