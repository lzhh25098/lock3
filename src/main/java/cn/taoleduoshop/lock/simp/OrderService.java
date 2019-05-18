package cn.taoleduoshop.lock.simp;

import cn.taoleduoshop.lock.Lock;
import cn.taoleduoshop.lock.ZookeeperDistrbuteLock;
import cn.taoleduoshop.lock.ZookeeperDistrbuteLock2;

/**
 * @author lzhh
 */
public class OrderService implements Runnable{
    private OrderNumGenerator orderNumGenerator =new OrderNumGenerator();
    private Lock lock = new ZookeeperDistrbuteLock2();
   @Override
    public void run() {
       getNumber();

    }

    private void getNumber() {
      try{
          lock.getLock();
          String number = orderNumGenerator.getNumber();
          System.out.println(Thread.currentThread().getName() +
                  "生成订单id:" + number);
      }catch (Exception e){
          e.printStackTrace();
      }finally {
          lock.unLock();
      }
   }

    public static void main(String[] args)  {
        for (int i =0;i < 150;i++){
           new Thread(new OrderService()).start();
        }
    }


}
