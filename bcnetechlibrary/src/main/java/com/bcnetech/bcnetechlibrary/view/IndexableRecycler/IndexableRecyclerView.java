package com.bcnetech.bcnetechlibrary.view.IndexableRecycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.util.PinyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Author: seewhy
 * Date: 2016/1/14
 */
public class IndexableRecyclerView extends RelativeLayout {
    public static final int DEFAULT_COLUMN_NUMBER = 3;
    private RecyclerView mRecyclerView;
    private TextView mTipText;
    private LetterBar mLetterBar;
    private Context mContext;
    public int mColumnNumber = DEFAULT_COLUMN_NUMBER;
    private SectionedRecyclerAdapter mRecyclerAdapter;
    private List<String> mlist;

    public IndexableRecyclerView(Context context) {
        this(context, null);
    }

    public IndexableRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexableRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews(context);
        init(attrs, defStyleAttr);
    }

    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_indexable, this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLetterBar = (LetterBar) view.findViewById(R.id.letter_bar);
        mTipText = (TextView) view.findViewById(R.id.tip_text);
    }

    public void setAdapter(RecyclerView.Adapter adapter, List<String> mlist) {
        mRecyclerAdapter = new SectionedRecyclerAdapter(mContext, R.layout.title_item, R.id.tvName, adapter);
        mRecyclerView.setAdapter(adapter);

        if(mlist!=null&&mlist.size()!=0){
            List<String> newlist = new ArrayList<>();
            for (int i=0;i<mlist.size();i++){
                //String news = PinyUtil.getSpells(mlist.get(i));
                String  news = com.bcnetech.bcnetechlibrary.util.hanyupinyin.HanziToPinyin.getFirstPinYinChar(mlist.get(i));
               String index =  news.charAt(0)+"";
                index = index.toUpperCase();
                if (isNumeric(index)){
                    newlist.add("#");
                }else{
                    newlist.add(index);
                }
            }
          // this.mlist = mlist;
            mLetterBar.textlist(newlist);
        }

    }

    public  boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.IndexableRecyclerView, defStyleAttr, 0);
        mColumnNumber = typedArray.getInteger(R.styleable.IndexableRecyclerView_recyclerColumns, DEFAULT_COLUMN_NUMBER);
        typedArray.recycle();

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, mColumnNumber);
        mRecyclerView.setLayoutManager(gridLayoutManager);
       // setLetter(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mRecyclerAdapter.isSectionHeaderPosition(position) ? mColumnNumber : 1;
            }
        });
        mLetterBar.setOnLetterSelectListener(new LetterBar.OnLetterSelectListener() {
            @Override
            public void onLetterSelect(int position, String letter, boolean confirmed) {
                if (confirmed) {
                    mTipText.setVisibility(View.GONE);
                } else {
                    mTipText.setVisibility(View.VISIBLE);
                    mTipText.setText(letter);
                }
                Integer sectionPosition = mRecyclerAdapter.getSectionPosition(position);
                if (sectionPosition != null)
                    gridLayoutManager.scrollToPositionWithOffset(sectionPosition, 0);
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //setLetter(gridLayoutManager);
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void setLetter(GridLayoutManager gridLayoutManager){
        int firstVisibleItems;
        firstVisibleItems = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (mlist!=null&&mlist.size()!=0) {
            if (mlist.size() == 1){
                String name = mlist.get(0);
            }else {
                for (int i = 0; i < mlist.size(); i++) {
                    if ((firstVisibleItems / 2 + 1) < mlist.size() && mlist.get(firstVisibleItems / 2 + 1) != null) {
                        String name = mlist.get(firstVisibleItems / 2 + 1);
                        String f = PinyUtil.getSpells(name);
                        String s = f.substring(0,1);
                        mLetterBar.onText(s);
                    }
                }
            }
        }
    }
}
