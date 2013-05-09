package txl.common;
import txl.activity.R;
import txl.config.Config;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class TxlAlertDialog
{
   public static android.app.AlertDialog alert = null;
   
   public static final int FIRST_BTN_INDEX = -1;
   public static final int SENCOND_BTN_INDEX = -2;
   public static final int THIRD_BTN_INDEX = -3;
   
   static String title = Config.getInstance().getTiptitle();
   public static abstract class DialogInvoker {
      public abstract void  doInvoke(DialogInterface dialog,int btndex);
   }
   /**
    * alert框显示
    * @param context
    * @param _title
    * @param message
    * @param buttons
    * @param invoker
    */
   public static void show(Context context,String _title,String message,String buttons,final DialogInvoker invoker){
      show(context, title, message,null, buttons,invoker); 
   }
   
   private static void show(Context context,String _title,String message,View view,String buttons,final DialogInvoker invoker){
       String[] btnArray = buttons.split(",");
       android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
       builder.setTitle(_title);
       if(view != null){
           builder.setView(view); 
       }
       if(message !=null){
           builder.setMessage(message).setCancelable(false);
       }
       int len = btnArray.length;
       if(len >= 1){
           builder.setPositiveButton(btnArray[0], new DialogInterface.OnClickListener()
           {
               public void onClick(DialogInterface dialog, int id)
               {
                   invoker.doInvoke(dialog,id);
                   alert.dismiss();
                   
               }
           });  
       }
       if(len >=2){
           builder.setNegativeButton(btnArray[1], new DialogInterface.OnClickListener()
           {
               public void onClick(DialogInterface dialog, int id)
               {
                   invoker.doInvoke(dialog,id);
                   alert.dismiss();
                   
               }
           });   
       }
       if(len >=3){
           builder.setNeutralButton(btnArray[2], new DialogInterface.OnClickListener()
           {
               public void onClick(DialogInterface dialog, int id)
               {
                   invoker.doInvoke(dialog,id);
                   alert.dismiss();
                   
               }
           });   
       }
         
       alert = builder.create();
       alert.show();
   }
   
   
   public static void show(Context context,String message,String buttons,DialogInvoker invoker){
       show(context, title, message, buttons,invoker); 
   }
   public static void show(Context context,View view,String buttons, DialogInvoker invoker){
       show(context, title, null,view, buttons,invoker);   
   }
   
   public static void show(Activity activity,int layout){
	   Dialog dialog = new Dialog(activity, R.style.dialog);  
       dialog.setContentView(layout);  
       dialog.show();  
   }
   /**
    
    AlertDialog.show(this, "将为您切入受理单管理系统？", "确定,取消", new AlertDialog.DialogInvoker(){

            @Override
            public void doInvoke(DialogInterface dialog, int btndex)
            {
                if(btndex == AlertDialog.FIRST_BTN_INDEX){
                   Intent intent = new Intent(ContactActivity.this,MawActivity.class);
                   ContactActivity.this.startActivity(intent); 
                    
                }else if(btndex == AlertDialog.SENCOND_BTN_INDEX){
                    
                }
                Log.d(TAG, " btndex : "+ btndex);
            }}
        );
   **/ 
}
