package com.androidjp.traffichelper.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidjp.lib_common_util.ui.SnackUtil;
import com.androidjp.lib_great_recyclerview.base.BaseRecAdapter;
import com.androidjp.lib_great_recyclerview.base.BaseViewHolder;
import com.androidjp.traffichelper.R;
import com.androidjp.traffichelper.adapter.RecordHolder;
import com.androidjp.traffichelper.data.pojo.Record;
import com.androidjp.traffichelper.data.pojo.RecordRes;
import com.androidjp.traffichelper.result.ResultActivity;

import java.util.List;

/**
 * 带HeaderView的分页加载LinearLayout RecyclerView
 * Created by androidjp on 2017/3/22.
 */

public class RecordListFragment extends SuperRecListFragment<Record> implements HistoryContract.View {

    private HistoryContract.Presenter mPresenter;

    protected RecyclerView.LayoutManager onSetLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected BaseRecAdapter<Record> onSetAdapter() {
        return new BaseRecAdapter<Record>() {
            @Override
            protected int onGetItemViewType(Record item) {
                return 0;
            }

            @Override
            protected BaseViewHolder createViewHolder(Context context, ViewGroup parent, int type) {
                return new RecordHolder(context, parent);
            }
        };
    }

    @Override
    protected View onSetEmptyView() {
//        return LayoutInflater.from(getActivity()).inflate(R.layout.empty_view,mSwipeRefreshLayout,false);
        return LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, (ViewGroup) getView(), false);
    }

    @Override
    protected boolean initData() {
        setEnableSwipeRefresh(true);
        setLoadlListener(new LoadListener() {
            @Override
            public void refreshDoing() {
                Toast.makeText(getActivity(), "下拉刷新。。。", Toast.LENGTH_SHORT).show();
                mPresenter.refresh();
            }

            @Override
            public void askForMoreDoing() {
                Toast.makeText(getActivity(), "加载更多。。。", Toast.LENGTH_SHORT).show();
                showFooter();
                mPresenter.loadMore();
            }
        });

        return false;
    }

    @Override
    protected RecyclerView.ItemDecoration[] onAddItemDecoration() {
        return null;
    }

    @Override
    protected void destroyViewDoing() {

    }


    @Override
    public void setPresenter(HistoryContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showHeader();
        this.mPresenter.start();
    }

    @Override
    public void onItemClick(Record itemValue, int viewID, int position) {
//        Toast.makeText(getActivity(),"点击事件",Toast.LENGTH_SHORT).show();
        //TODO:加载详情
        if (viewID == 123321)
            this.mPresenter.loadDetail(position, itemValue);
    }

    @Override
    public List<Record> getRecDataList() {
        return getAdapter().getDataList();
    }

    @Override
    public void refreshRecordList(int page, List<Record> recordList) {
        if (page == 0) {
            if (recordList == null || recordList.size() == 0) {
                hideHeader();
                refreshEmptyView();
                SnackUtil.show(getView(), "您没有新的理赔记录了。。");
                return;
            }
            ///有新的数据，加载进去
            getAdapter().addList(0, recordList);
            hideHeader();
            refreshEmptyView();
            //TODO：此时，如果没有更多的数据，应该显示footView，并点击footView可加载更多
            if (isSlideToBottom(mRecView)) {
                showFooter();
                mFootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.loadMore();
                    }
                });
            }
        } else {
            getAdapter().addList(recordList);
            hideFooter();
            refreshEmptyView();
        }
    }

    @Override
    public void finishLoadDetail(int pos, RecordRes recordRes) {
        if (this.getRecDataList().get(pos).getResult() == null) {
            if (recordRes != null)
                this.getRecDataList().get(pos).setResult(recordRes);
        }
        Intent intent = new Intent(getActivity(), ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("recordRes", this.getRecDataList().get(pos).getResult());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void loadFail(String msg) {
        SnackUtil.show(mSwipeRefreshLayout, msg);
    }
}
