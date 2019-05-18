package cn.taoleduoshop.lock.simp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lzhh
 */
public class OrderNumGenerator {
    //全局订单id
    public  static int count = 0;
    //public Object object = new Object();


    //生成订单ID
    public   String getNumber() {
      //  synchronized (object) {
            SimpleDateFormat simpt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            return simpt.format(new Date()) + "-" + ++count;
        }
    }



//}
