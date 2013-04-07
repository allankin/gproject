package txl.common;

import txl.Handlable;
import txl.config.TxlConstants;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class SideBar extends View {  
	
    private char[] l;  
    private SectionIndexer sectionIndexter = null;  
    private ListView list;  
    private final int m_nItemHeight = 26;  
    private Context context;
    private Handlable handlable;
    private boolean hasDone;
    
    public SideBar(Context context) {  
        super(context);  
        init();  
        this.context =context;
    }  
    
    
    public Handlable getHandlable() {
		return handlable;
	}


	public void setHandlable(Handlable handlable) {
		this.handlable = handlable;
	}


	public SideBar(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init();  
    }  
    private void init() {  
        l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',  
                'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };  
        setBackgroundColor(0x44FFFFFF);  
    }  
    public SideBar(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init();  
    }  
    public void setListView(ListView _list) {  
        list = _list;  
        sectionIndexter = (SectionIndexer) _list.getAdapter();  
        
    }  
    public boolean onTouchEvent(MotionEvent event) {  
        super.onTouchEvent(event);  
        int i = (int) event.getY();  
        int idx = i / m_nItemHeight;  
        if (idx >= l.length) {  
            idx = l.length - 1;  
        } else if (idx < 0) {  
            idx = 0;  
        }  
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {  
            if (sectionIndexter == null) {  
                sectionIndexter = (SectionIndexer) list.getAdapter();  
            }  
            int position = sectionIndexter.getPositionForSection(l[idx]);  
            if (position == -1) {  
                return true;  
            } 
            if(!hasDone){
            	Message msg = new Message();
                msg.what = TxlConstants.CONTACT_HANDLER_OVERLAY_VISIBLE;
        		handlable.getHandler().sendMessage(msg);
        		hasDone = true;
            }
            list.setSelection(position);  
        }
        
        
        
        if(event.getAction() == MotionEvent.ACTION_UP){
        	Handlable handlable = this.handlable;
        	if(handlable!=null){
        		Message msg = new Message();
                msg.what = TxlConstants.CONTACT_HANDLER_HIDE_OVERLAY;
        		handlable.getHandler().sendMessage(msg);
        	}
        }
        return true;  
    }  
    protected void onDraw(Canvas canvas) {  
        Paint paint = new Paint();  
        paint.setColor(0xFFA6A9AA);  
        paint.setTextSize(20);  
        paint.setTextAlign(Paint.Align.CENTER);  
        float widthCenter = getMeasuredWidth() / 2;  
        for (int i = 0; i < l.length; i++) {  
            canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);  
        }  
        super.onDraw(canvas);  
    }  
}  