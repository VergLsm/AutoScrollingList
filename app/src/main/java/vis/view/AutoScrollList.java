package vis.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import org.vision.autoscrollinglist.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import vis.widget.AutoScrollAdapter;

/**
 * Created by Vision on 15/6/5.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AutoScrollList extends ListView {
    private Context context;
    private AutoScrollAdapter adapter;
    private int[] imageRid;
    private String[] titleName;
    private int itemHight;

    private final Handler myHandler = new MyHandler(this);
    private int position;
    private int displayLength;
    private OnClickListener onClickListener;

    private static class MyHandler extends Handler {
        private final WeakReference<AutoScrollList> mAutoScrollList;

        public MyHandler(AutoScrollList asl) {
            mAutoScrollList = new WeakReference<AutoScrollList>(asl);
        }

        @Override
        public void handleMessage(Message msg) {
            AutoScrollList asl = mAutoScrollList.get();
            asl.jumpNext();
        }
    }

    public AutoScrollList(Context context) {
        super(context);
        this.context = context;
    }

    public AutoScrollList(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public AutoScrollList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (0 == itemHight) {
            View listItem = getAdapter().getView(0, null, this);
            listItem.measure(0, 0);
            itemHight = listItem.getMeasuredHeight();
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthSize, (itemHight + getDividerHeight()) * displayLength - getDividerHeight());
    }

    public void initView(int[] imageRid, String[] titleName, int displayLength, int period) {
        this.imageRid = imageRid;
        this.titleName = titleName;
        this.displayLength = displayLength;
        adapter = new AutoScrollAdapter(this.context, getData(), R.layout.vlist,
                new String[]{"title", "img"},
                new int[]{R.id.title, R.id.img});
        this.setAdapter(adapter);
        this.setEnabled(false);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(0);
            }
        }, period, period);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map;
        for (int i = 0; i < titleName.length + displayLength; i++) {
            map = new HashMap<>();
            map.put("title", titleName[i % titleName.length]);
//            map.put("img", R.mipmap.personnal_center_friends_relationship_page_icon_1 + (i % 3));
            map.put("img", imageRid[i % imageRid.length]);
            list.add(map);
        }
        return list;
    }

    public void jumpNext() {
        if (0 == position) {
            //很无奈，准确定位要加这句
            adapter.notifyDataSetChanged();
            this.setSelectionFromTop(0, 0);
        }
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            this.smoothScrollToPositionFromTop(position + 1, 0, 500);
        } else if (android.os.Build.VERSION.SDK_INT >= 8) {
            Log.d("jumpNext()", String.valueOf(position));
            this.smoothScrollBy(itemHight + this.getDividerHeight(), 500);
        }
        position = (position + 1) % titleName.length;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        return super.onTouchEvent(ev);
        if (MotionEvent.ACTION_UP == ev.getAction()) {
            this.onClickListener.onClick(((((int) ev.getY()) / (getHeight() / 3)) + position) % titleName.length);
        }
        return true;
    }

    public void setOnClickListener(AutoScrollList.OnClickListener listener) {
        this.onClickListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param selectedItem The number that was clicked.
         */
        void onClick(int selectedItem);
    }


}
