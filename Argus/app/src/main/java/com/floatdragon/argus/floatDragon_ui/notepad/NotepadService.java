package com.floatdragon.argus.floatDragon_ui.notepad;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.floatdragon.argus.R;
import com.floatdragon.argus.floatDragon_ui.MyService;
import com.floatdragon.argus.floatDragon_ui.StaticData;
import com.floatdragon.argus.storage.*;
import com.floatdragon.argus.floatDragon_ui.notepad.SlideCutListView.RemoveListener;
import com.floatdragon.argus.floatDragon_ui.notepad.SlideCutListView.RemoveDirection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by zx on 2015/9/10.
 */
public class NotepadService extends Service implements RemoveListener {

    WindowManager.LayoutParams wmParams;            //���������������ò��ֲ���Ķ���
    WindowManager mWindowManager;

    RelativeLayout Notepad_Main;
    Button Notepad_Main_Add;
    ImageView Notepad_Main_Delete;
    TextView Notepad_Show_Title;
    RelativeLayout NotepadActivity;
    RelativeLayout Notepad_Head;
    RelativeLayout Notepad_Show_Layout;
    TextView Empty_List_Textview;
    RelativeLayout Notepad_Show_Title_Layout;

    RelativeLayout Notepad_Delete;
    RelativeLayout Notepad_Delete_Show_Layout;
    RelativeLayout Notepad_Delete_Head;
    RelativeLayout NotepadDeleteActivity;
    TextView Notepad_Delete_Show_Title;
    ImageView Notepad_Delete_Delete;
    RelativeLayout Notepad_Delete_SelectAll;
    CheckBox Notepad_Delete_All_Checkbox;
    RelativeLayout Notepad_Delete_Title_Layout;

    private List<Map<String, Object>> listitems;
    private SlideCutListView NotepadListView;
    private ListView NotepadDeleteList;
    private SimpleAdapter adapter;
    private DeleteAdapter adapter2;
    private int count_select_item;

    private View ShowPopupWindowview;
    private View ShowPopupWindowview2;
    private View ShowPopupWindowview3;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;
    private PopupWindow popupWindow3;

    private DatabaseHandler m_dbhandler;

    private boolean lockonclick;
    private boolean CONFIRM;


    private Context mContext = null;
    private static NotepadService notepadService = null;
    public NotepadService(){
        notepadService = this;
    }
    public static NotepadService getItService() {
        return notepadService;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        createNotepadView();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void createNotepadView () {
        lockonclick = true;

        wmParams = new WindowManager.LayoutParams();
        //��ȡ����WindowManagerImpl.CompatModeWrapper
        //mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        mWindowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        //����window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //����ͼƬ��ʽ��Ч��Ϊ����͸��
        wmParams.format = PixelFormat.RGBA_8888;
        //���ø������ڲ��ɾ۽���ʵ�ֲ���������������ɼ�ڵĲ�����
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //��������ʾ��ͣ��λ��Ϊ����ö�
        wmParams.gravity = Gravity.CENTER | Gravity.CENTER;
        // ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ�������gravity
        wmParams.x = 0;
        wmParams.y = 0;
        //������ڳ������
        //wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        wmParams.width=mWindowManager.getDefaultDisplay().getWidth();
        wmParams.height=mWindowManager.getDefaultDisplay().getHeight();



        //��ȡ����������ͼ���ڲ���
/*************************///Notepad_Main
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        Notepad_Main = (RelativeLayout)inflater.inflate(R.layout.notepad_main, null);
        Notepad_Show_Layout = (RelativeLayout)Notepad_Main.findViewById(R.id.notepad_show_layout);
        Notepad_Head = (RelativeLayout)Notepad_Main.findViewById(R.id.notepad_head);
        NotepadActivity = (RelativeLayout)Notepad_Main.findViewById(R.id.notepad_activity);
        NotepadActivity.getBackground().setAlpha(220);
        Notepad_Head.getBackground().setAlpha(220);
        NotepadListView = (SlideCutListView) Notepad_Main.findViewById(R.id.lv_friend_list);
        Notepad_Main_Add = (Button)Notepad_Main.findViewById(R.id.notepad_main_add);
        Notepad_Main_Delete = (ImageView)Notepad_Main.findViewById(R.id.notepad_main_delete);
        Notepad_Show_Title = (TextView)Notepad_Main.findViewById(R.id.notepad_show_title);
        Empty_List_Textview = (TextView)Notepad_Main.findViewById(R.id.empty_list_textview);
        Notepad_Show_Title_Layout = (RelativeLayout)Notepad_Main.findViewById(R.id.notepad_show_title_layout);

        m_dbhandler = new DatabaseHandler(getApplicationContext());
        listitems = m_dbhandler.selectAll();
        adapter = new SimpleAdapter(this, listitems, R.layout.array_items,
                new String[]{"date", "string"}, new int[]{R.id.dates, R.id.strings});
        if (adapter.getCount() == 0) {
            Empty_List_Textview.setVisibility(View.VISIBLE);
        }
        else {
            Empty_List_Textview.setVisibility(View.GONE);
        }
        NotepadListView.setRemoveListener(this);
        NotepadListView.setAdapter(adapter);
/****************************/
/****************************///Notepad_Delete
        LayoutInflater inflater2 = LayoutInflater.from(getApplication());
        Notepad_Delete = (RelativeLayout)inflater2.inflate(R.layout.notepad_delete, null);
        Notepad_Delete_Show_Layout = (RelativeLayout) Notepad_Delete.findViewById(R.id.notepad_delete_layout);
        Notepad_Delete_Head = (RelativeLayout)Notepad_Delete.findViewById(R.id.notepad_head);
        Notepad_Delete_Delete = (ImageView)Notepad_Delete.findViewById(R.id.Notepad_Delete_Delete);
        NotepadDeleteActivity = (RelativeLayout)Notepad_Delete.findViewById(R.id.notepad_activity);
        NotepadDeleteActivity.getBackground().setAlpha(220);
        Notepad_Delete_Head.getBackground().setAlpha(220);
        NotepadDeleteList = (ListView) Notepad_Delete.findViewById(R.id.notepad_delete_list);
        Notepad_Delete_Show_Title = (TextView)Notepad_Delete.findViewById(R.id.notepad_show_title);
        Notepad_Delete_SelectAll = (RelativeLayout)Notepad_Delete.findViewById(R.id.notepad_delete_all);
        Notepad_Delete_All_Checkbox = (CheckBox)Notepad_Delete.findViewById(R.id.checkbox_all);
        Notepad_Delete_Title_Layout = (RelativeLayout)Notepad_Delete.findViewById(R.id.notepad_delete_title_layout);
/****************************/

/****************************/
        int Width, Height;
        Height = StaticData.screenHeight;
        Width = StaticData.screenWidth;
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)Notepad_Show_Layout.getLayoutParams();
        relativeParams.setMargins(Width / 7, Height / 8, Width / 7, Height / 8);
        Notepad_Show_Layout.setLayoutParams(relativeParams);
        Notepad_Delete_Show_Layout.setLayoutParams(relativeParams);




/****************************/

        mWindowManager.addView(Notepad_Main, wmParams);
        Notepad_Delete.setVisibility(View.GONE);
        mWindowManager.addView(Notepad_Delete, wmParams);

        Notepad_Main.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Notepad_Delete.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
/*****************************///Notepad_Main_*.setListener()
        Notepad_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyService.getMyService().isShow3(false);
                StaticData.isShow3 = 0;
            }
        });                 ////////////////////finish
        Notepad_Head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        Notepad_Show_Title_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });                 ////////////////////finish

        NotepadActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopupWindow(-1, "");
            }
        });                 ////////////////////finish

        NotepadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView lv = (ListView) adapterView;
                HashMap<String, Object> hash_map = (HashMap<String, Object>) lv.getItemAtPosition(i);
                ShowPopupWindow(i, hash_map.get("string").toString());
            }
        });                 ////////////////////finish

        Notepad_Main_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopupWindow(-1, "");
            }
        });                 ////////////////////finish

        Notepad_Main_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean [] checked = new boolean[listitems.size()];
                for (int i = 0; i < checked.length; i ++) {
                    checked[i] = false;
                }
                adapter2 = new DeleteAdapter(getBaseContext(), listitems, R.layout.delete_items,
                        new String[]{"date", "string",},
                        new int[]{R.id.dates, R.id.strings}, checked);
                NotepadDeleteList.setAdapter(adapter2);
                count_select_item = 0;
                Notepad_Delete.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Notepad_Main.setVisibility(View.GONE);
                    }
                }, 50);
            }
        });                 ////////////////////finish
/******************************/

/*****************************///Notepad_Delete_*.setListener()
        Notepad_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter = new SimpleAdapter(getApplicationContext(), listitems, R.layout.array_items,
                        new String[]{"date", "string"}, new int[]{R.id.dates, R.id.strings});
                NotepadListView.setAdapter(adapter);
                Notepad_Main.setVisibility(View.VISIBLE);
                if (adapter.getCount() == 0) {
                    Empty_List_Textview.setVisibility(View.VISIBLE);
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Notepad_Delete.setVisibility(View.GONE);
                    }
                }, 50);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Notepad_Delete_All_Checkbox.setChecked(false);
                        Notepad_Delete_SelectAll.setBackgroundColor(0);
                    }
                }, 50);

            }
        });

        Notepad_Delete_Head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Notepad_Delete_Title_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Notepad_Delete_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count_select_item > 0) {
                    showpopupwindowconfirm();
                }
            }
        });

        Notepad_Delete_SelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Notepad_Delete_All_Checkbox.isChecked()) {
                    adapter2.setcheckedall(false);
                    adapter2.notifyDataSetChanged();
                    Notepad_Delete_All_Checkbox.setChecked(false);
                    Notepad_Delete_SelectAll.setBackgroundColor(0);
                    count_select_item = 0;
                }
                else {
                    adapter2.setcheckedall(true);
                    adapter2.notifyDataSetChanged();
                    Notepad_Delete_All_Checkbox.setChecked(true);
                    Notepad_Delete_SelectAll.setBackgroundResource(R.drawable.selectedbackground);
                    Notepad_Delete_SelectAll.getBackground().setAlpha(100);
                    count_select_item = adapter2.getCount();
                }
            }
        });

        NotepadDeleteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                lockonclick = false;
                ListView lv = (ListView) adapterView;
                HashMap<String, Object> hash_map = (HashMap<String, Object>) lv.getItemAtPosition(i);
                ShowPopupWindowinDelete(hash_map.get("string").toString());
                return false;
            }
        });
        NotepadDeleteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (lockonclick == false) {
                    lockonclick = true;
                    return;
                }
                boolean checked = adapter2.getcheckedat(i);
                adapter2.setcheckedat(!checked, i);
                adapter2.notifyDataSetChanged();
                if (checked) {
                    count_select_item --;
                    Notepad_Delete_All_Checkbox.setChecked(false);
                    Notepad_Delete_SelectAll.setBackgroundColor(0);
                }
                else {
                    count_select_item ++;
                    if (count_select_item == adapter2.getCount()) {
                        Notepad_Delete_All_Checkbox.setChecked(true);
                        Notepad_Delete_SelectAll.setBackgroundResource(R.drawable.selectedbackground);
                        Notepad_Delete_SelectAll.getBackground().setAlpha(100);
                    }
                }
            }
        });
/*****************************/
    }

    private void ShowPopupWindow(final int index, final String string) {

        int width = Notepad_Show_Layout.getWidth();
        int height = Notepad_Show_Layout.getHeight();
        if (popupWindow == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
            ShowPopupWindowview = layoutInflater.inflate(R.layout.popupwindow, null);
            popupWindow = new PopupWindow(ShowPopupWindowview, width, height);
        }
        final ImageView tmpimageview = (ImageView)ShowPopupWindowview.findViewById(R.id.PopupWindow_New_Delete);
        final EditText tmpedittext = (EditText)ShowPopupWindowview.findViewById(R.id.PopupWindow_New_Edittext);
        TextView notepadshowtitle = (TextView)ShowPopupWindowview.findViewById(R.id.notepad_show_title);
        tmpedittext.setText(string);
        if (index == -1) {
            notepadshowtitle.setText("NEW-NOTE");
        }
        else {
            notepadshowtitle.setText("BROWSE-NOTE");
        }
        popupWindow.setFocusable(true);
        // ����������������ʧ
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        final int selector;
        if (index == -1) {
            popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
            selector = 250;
        }
        else {
            popupWindow.setAnimationStyle(R.style.browse_notepad_popwindow);
            selector = 500;
        }
        popupWindow.showAtLocation(Notepad_Show_Layout, Gravity.CENTER, 0, 0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Notepad_Head.setVisibility(View.GONE);
                NotepadActivity.setVisibility(View.GONE);
            }
        }, selector);
        if (index == -1) {
            openKeyboard(new Handler(), 50);
        }
        tmpimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tmpedittext.setText("");
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String tmpstring = tmpedittext.getText().toString();
                if (index == -1) {
                    String date = GetDate();
                    if (!tmpstring.equals("")) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("date", date);
                        map.put("string", tmpstring);
                        listitems.add(0, map);
                        adapter.notifyDataSetChanged();
                        m_dbhandler.insertMessage(date, tmpstring);
                    }
                }
                else {
                    if (!tmpstring.equals(string)) {
                        if (tmpstring.equals("")) {
                            listitems.remove(index);
                            adapter.notifyDataSetChanged();
                            m_dbhandler.deleteContact(index);
                        }
                        else {
                            Map<String, Object> map = new HashMap<String, Object>();
                            String date = GetDate();
                            map.put("date", date);
                            map.put("string", tmpstring);
                            listitems.remove(index);
                            listitems.add(0, map);
                            adapter.notifyDataSetChanged();
                            m_dbhandler.deleteContact(index);
                            m_dbhandler.insertMessage(date, tmpstring);
                        }
                    }
                }
                if (adapter.getCount() > 0) {
                    Empty_List_Textview.setVisibility(View.GONE);
                }
                else {
                    Empty_List_Textview.setVisibility(View.VISIBLE);
                }
                int tmpselector;
                if (index == -1) {
                    tmpselector = 500;
                }
                else {
                    tmpselector = 0;
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Notepad_Head.setVisibility(View.VISIBLE);
                        NotepadActivity.setVisibility(View.VISIBLE);
                    }
                }, tmpselector);

            }
        });
    }

    private void ShowPopupWindowinDelete (String string) {
        int width = Notepad_Delete_Show_Layout.getWidth();
        int height = Notepad_Delete_Show_Layout.getHeight();
        if (popupWindow2 == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
            ShowPopupWindowview2 = layoutInflater.inflate(R.layout.delete_browse_popupwindow, null);
            popupWindow2 = new PopupWindow(ShowPopupWindowview2, width, height);
        }
        final TextView textView = (TextView)ShowPopupWindowview2.findViewById(R.id.delete_browse_textview);
        textView.setText(string);
        popupWindow2.setFocusable(true);
        // ����������������ʧ
        popupWindow2.setOutsideTouchable(true);
        popupWindow2.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow2.setAnimationStyle(R.style.delete_notepad_popwindow);
        popupWindow2.showAtLocation(Notepad_Delete_Show_Layout, Gravity.CENTER, 0, 0);
        ShowPopupWindowview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.dismiss();
            }
        });
    }

    private void showpopupwindowconfirm() {
        TextView textView;
        Button buttoncancel;
        Button buttonconfirm;
        CONFIRM = false;
        int width = Notepad_Delete_Show_Layout.getHeight() / 2;
        int height = Notepad_Delete_Show_Layout.getWidth() / 2;
        if (popupWindow3 == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
            ShowPopupWindowview3 = layoutInflater.inflate(R.layout.delete_confirm_popupwindow, null);
            popupWindow3 = new PopupWindow(ShowPopupWindowview3, width, height);
        }
        textView = (TextView)ShowPopupWindowview3.findViewById(R.id.delete_confirm_textview);
        buttoncancel = (Button)ShowPopupWindowview3.findViewById(R.id.delete_confirm_cancel);
        buttonconfirm = (Button)ShowPopupWindowview3.findViewById(R.id.delete_confirm_confirm);
        popupWindow3.setFocusable(true);
        // ����������������ʧ
        popupWindow3.setOutsideTouchable(true);
        popupWindow3.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow3.setAnimationStyle(R.style.delete_notepad_popwindow);
        popupWindow3.showAtLocation(Notepad_Delete_Show_Layout, Gravity.CENTER, 0, 0);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow3.dismiss();
            }
        });
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CONFIRM = true;
                popupWindow3.dismiss();
            }
        });
        ShowPopupWindowview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow3.dismiss();
            }
        });
        popupWindow3.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (CONFIRM) {
                    Vector vector = new Vector();
                    for (int i = 0; i < adapter2.getCount(); i++) {
                        if (adapter2.getcheckedat(i)) {
                            vector.addElement(i);
                        }
                    }
                    m_dbhandler.deleteselect(vector);
                    listitems.clear();
                    listitems = m_dbhandler.selectAll();
                    boolean[] checked = new boolean[listitems.size()];
                    for (int i = 0; i < checked.length; i++) {
                        checked[i] = false;
                    }
                    adapter2 = new DeleteAdapter(getBaseContext(), listitems, R.layout.delete_items,
                            new String[]{"date", "string",},
                            new int[]{R.id.dates, R.id.strings}, checked);
                    NotepadDeleteList.setAdapter(adapter2);
                    Notepad_Delete_All_Checkbox.setChecked(false);
                    Notepad_Delete_SelectAll.setBackgroundColor(0);
                    count_select_item = 0;
                }
            }
        });
    }

    private void openKeyboard(Handler mHandler, int s) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, s);
    }//�Զ��������뷨

    private String GetDate() {
        Calendar calendar = Calendar.getInstance();
        String date = (((calendar.get(Calendar.DAY_OF_MONTH) < 10)?"0":""))
                + calendar.get(Calendar.DAY_OF_MONTH) + "/"
                + (((calendar.get(Calendar.MONTH) + 1) < 10)?"0":"")
                + (calendar.get(Calendar.MONTH) + 1) + " "
                + ((calendar.get(Calendar.HOUR_OF_DAY) < 10)?"0":"")
                + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + ((calendar.get(Calendar.MINUTE) < 10)?"0":"")
                + calendar.get(Calendar.MINUTE);
        return  date;
    }

    @Override
    public void removeItem(RemoveDirection direction, int position) {
        m_dbhandler.deleteContact(position);
        listitems.clear();
        listitems = m_dbhandler.selectAll();
        adapter = new SimpleAdapter(this, listitems, R.layout.array_items,
                new String[]{"date", "string"}, new int[]{R.id.dates, R.id.strings});
        if (adapter.getCount() == 0) {
            Empty_List_Textview.setVisibility(View.VISIBLE);
        }
        NotepadListView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(Notepad_Main != null)
        {
            //�Ƴ����
            mWindowManager.removeView(Notepad_Main);
        }
    }
}
