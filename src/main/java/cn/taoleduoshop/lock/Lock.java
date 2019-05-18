package cn.taoleduoshop.lock;

/**
 * 锁接口
 * @author lzhh
 * create by 2019/5/18  15:24
 */
public interface Lock {
    //获取锁
    public void getLock();
    //解锁
    public void unLock();

}
