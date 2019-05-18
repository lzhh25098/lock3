package cn.taoleduoshop.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 基于前面的思路，我们来实现一套优雅的锁思路基本差不多
 * 多了两个变量
 * @author lzhh
 * create by 2019/5/18  17:00
 */
public class ZookeeperDistrbuteLock2 extends ZookeeprAbstractLock {
    private final static Logger logger = LoggerFactory.getLogger(ZookeeperDistrbuteLock2.class);
    private CountDownLatch countDownLatch = null;
    private String beforepath ;//当前节点的后一个节点
    private String currentpath;//记录当前节点
    //需要一个构造方法创建一个持久节点，持久节点下创建临时顺序节点
    public  ZookeeperDistrbuteLock2(){
        if (this.zkClient.exists(PATH2)){
            this.zkClient.createPersistent(PATH2);
        }
    }
    @Override
    public boolean tryLock() {

        //如果当前为空则尝试第一次枷锁，第一次赋值currentpath
        if (currentpath == null || currentpath.length() <= 0){
            //创建一个临时顺序节点
            currentpath = this.zkClient.
                    createEphemeralSequential(PATH2 ,"lock");
            return true;
        }
        //获取临时节点并排序
        List<String> childNode = this.zkClient.getChildren(PATH2);
        Collections.sort(childNode);
        //获取排序后第0个节点
        if (currentpath.equals(PATH2 + '/' + childNode.get(0))){
            return true;
        }else {
            int wz = Collections.binarySearch(childNode,currentpath.substring(7));
            beforepath = PATH2 + '/' + childNode.get(wz - 1);
        }
        return false;
    }
//等待锁思路基本一致
    @Override
    public void waitLock() {
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                if (countDownLatch != null){
                    //countDownLatch不等于空进行一次扣减
                    countDownLatch.countDown();
                }
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        };
        //给排名前面的节点增加删除数据的watch
        this.zkClient.subscribeDataChanges(beforepath,listener);
        if (this.zkClient.exists(beforepath)){
            //一直阻塞等到收到通知为止
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            }catch (Exception e){
                logger.error("异常信息:"+ e.getMessage(),e);
                e.printStackTrace();}
        }
        //删除节点
        this.zkClient.unsubscribeDataChanges(beforepath,listener);

    }

    @Override
    public void unLock() {
        //释放锁删除当前所有节点
        zkClient.delete(currentpath);
        zkClient.close();
        logger.info("#######释放资源#######");

    }
}
