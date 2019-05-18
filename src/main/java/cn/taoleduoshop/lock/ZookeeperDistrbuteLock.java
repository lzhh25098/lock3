package cn.taoleduoshop.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 具体锁的实现，基于异常的,需要继承zookeeperAbstactLock这个类并实现所有方法
 * @author lzhh
 * create by 2019/5/18  15:36
 */
public class ZookeeperDistrbuteLock extends ZookeeprAbstractLock {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperDistrbuteLock.class);
    private CountDownLatch countDownLatch = null;
    public boolean tryLock() {
        //尝试获取锁方法应进来是就创建一个临时节点，
        // 让线程去抢这个节点抢到则上锁成功没抢到则等待
        try {
            zkClient.createEphemeral(PATH);
            return true;

        }catch (Exception e){
            logger.error("异常信息："+ e.getMessage(),e);
            e.printStackTrace();
            return false;
        }

    }
//等待锁，如果节点存在则资源被上锁，
// 节点不存在则不阻塞我们应该使用Countdownlatch实现阻塞
    public void waitLock() {
        //我们使用watch机制去监听节点是否有变化
        IZkDataListener listener = new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {
                //这里监听节点是否变化
                if (countDownLatch != null){
                    //扣减一次
                    countDownLatch.countDown();
                }
            }

            public void handleDataDeleted(String s) throws Exception {

            }
        };
        //注册事件
        zkClient.subscribeDataChanges(PATH, listener);
        //如果临时节点存在
        if (zkClient.exists(PATH)){
            //进行阻塞等待,给countdownlatch赋值阻塞
            countDownLatch = new CountDownLatch(1);
            //调用countdownlatch.await方法阻塞此方法会抛出
            // InterruptedException所以应该用try包起来
            try {
                countDownLatch.await();

            }catch (Exception e){
                logger.error("异常信息："+ e.getMessage(),e);
                e.printStackTrace();
            }

        }
        //删除监听
        zkClient.unsubscribeDataChanges(PATH,listener);

    }
//释放锁
    public void unLock() {
        if(zkClient != null){
            zkClient.delete(PATH);
            zkClient.close();
            logger.info("########释放资源########");
        }
    }
}
