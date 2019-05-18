package cn.taoleduoshop.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 锁的抽象，继承锁接口
 * @author lzhh
 * create by 2019/5/18  15:25
 */
public abstract class AbstractLock implements Lock {
    private Logger logger = LoggerFactory.getLogger(AbstractLock.class);
    public void getLock(){
        if ( tryLock()){
           logger.info("##########获取锁资源成功###########");
        }//如果不成功则递归获取
        else {
            waitLock();
            getLock();
        }

    }
    //尝试获取锁
    public abstract boolean tryLock();
    //等待锁
    public abstract  void waitLock();
}
