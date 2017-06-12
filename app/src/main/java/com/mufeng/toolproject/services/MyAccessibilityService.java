package com.mufeng.toolproject.services;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.mufeng.toolproject.Activitys.FileStorageActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能安装功能的实现类
 * Created by mufeng on 2017/3/24.
 */
public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";
    private Map<Integer, Boolean> mHandleMap = new HashMap<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "AccessibilityEvent is: " + event);
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null && FileStorageActivity.mIsSmartInstall) {
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                    eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (mHandleMap.get(event.getWindowId()) == null) {
                    boolean handled = iterateNodesAndHandle(nodeInfo);
                    if (handled) {
                        mHandleMap.put(event.getWindowId(), true);
                    }
                }
            }
        }

    }

    //遍历节点，模拟点击安装按钮
    private boolean iterateNodesAndHandle(AccessibilityNodeInfo nodeInfo) {
        Log.d(TAG, "AccessibilityNodeInfo is: " + nodeInfo);
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();
            if ("android.widget.Button".equals(nodeInfo.getClassName())) {//遇到Button的时候模拟点击一下
                String nodeContent = nodeInfo.getText().toString();
                Log.d(TAG, "content is: " + nodeContent);
                if ("安装".equals(nodeContent)
                        || "完成".equals(nodeContent)
                        || "确定".equals(nodeContent)) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            } else if ("android.widget.ScrollView".equals(nodeInfo.getClassName())) {//遇到ScrollView的时候模拟滑动一下
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (iterateNodesAndHandle(childNodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }
}
