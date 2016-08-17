package org.koala.core;
/**
 * ACCEPT 提交了，还未初始化
 * READY  初始化完毕， 准备被执行
 * DOING 正在做
 * DONE 正常完成
 * FAIL 任务失败, 指的是source和sink失败，而不是提交失败，提交失败一开始就知道
 * KILLED 被杀死
 * @author koala
 */
public enum JobStatus {
	ACCEPT, READY, DOING, DONE, FAIL, KILLED
}
