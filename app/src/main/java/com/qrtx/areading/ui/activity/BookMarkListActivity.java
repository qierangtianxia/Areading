package com.qrtx.areading.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.BookMark;
import com.qrtx.areading.ui.view.readView.PageFactory;
import com.qrtx.areading.utils.BookUtil;

import java.util.List;

public class BookMarkListActivity extends AppCompatActivity {
    private ListView mLvMark;
    private ImageView mIvNoMark;
    private List<BookMark> mBookMarkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark_list);
        setTitle(R.string.bookmark);

        mLvMark = (ListView) findViewById(R.id.id_lv_book_mark);
        mIvNoMark = (ImageView) findViewById(R.id.id_iv_no_bookmark);

        mLvMark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_READ_BOOK_MARK, mBookMarkList.get(position).getProgress());
                setResult(Constants.RESULT_CODE_SHOW_BOOKMARK, intent);
                finish();
            }
        });

        String bookID = getIntent().getStringExtra(Constants.KEY_USER_BOOK_ID);
        mBookMarkList = BookUtil.getBookMark(bookID);

        if (mBookMarkList != null && mBookMarkList.size() > 0) {
            mLvMark.setAdapter(new MyAdapter());
        } else {
            mIvNoMark.setVisibility(View.VISIBLE);
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBookMarkList.size();
        }

        @Override
        public BookMark getItem(int position) {
            return mBookMarkList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        LayoutInflater inflater = LayoutInflater.from(BookMarkListActivity.this);

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_lv_book_mark, parent, false);
            }
            TextView chapterName = (TextView) convertView.findViewById(R.id.id_tv_chapter_name);
            TextView time = (TextView) convertView.findViewById(R.id.id_tv_mark_time);
            ImageButton delete = (ImageButton) convertView.findViewById(R.id.id_btn_delete);

            final BookMark bookMark = getItem(position);
            int[] progress = bookMark.getProgress();

            chapterName.setText("第" + progress[0] + "章");
            time.setText(bookMark.getTime() + "   " + bookMark.getPercentStr());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BookUtil.removeBookMark(bookMark) > 0) {
                        mBookMarkList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });

            return convertView;
        }
    }
}
