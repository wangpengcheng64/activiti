package com.neimeng.workflow.config;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * 监听器，每次流到下一个节点时，会触发，可以设置该节点的办理人
 */
@Component
public class TaskListenerImpl implements TaskListener {

    /**指定个人任务和组任务的办理人*/
    @Override
    public void notify(DelegateTask delegateTask) {
        String assignee = "userA";
        System.out.println("---------------------------执行了--------------------------");
        //指定个人任务
        delegateTask.setAssignee(assignee);
    }

}
