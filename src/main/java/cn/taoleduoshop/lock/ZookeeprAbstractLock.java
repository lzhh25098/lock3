package cn.taoleduoshop.lock;

import org.I0Itec.zkclient.ZkClient;

/**
 * 把重复代码写入子类，例如节点连接的信息
 * @author lzhh
 * create by 2019/5/18  15:31
 */
public abstract class ZookeeprAbstractLock extends AbstractLock {
    //zk连接地址
    private static final String CONNECT = "47.105.61.220:2181";
    //zk客户端
    protected ZkClient zkClient = new ZkClient(CONNECT);
    //zk锁节点
    protected final static String PATH = "/lock";
    //zk锁节点2
    protected final static String PATH2 ="/lock2";
}
