package com.luck.chat;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.luck.chat.service.UserService;
import com.luck.chat.service.imlp.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
@EnableCaching
class ChatApplicationTests {
    private static Lock lock = new ReentrantLock();
    static int index = 0;


    public static void main(String[] args) {
        //多线程交替打印问题
        new Thread(()->{
            while (index<1000) {
                lock.lock();
                if(index%3==0){
                    System.out.println("A");
                    index++;
                }
                lock.unlock();
            }
        }).start();
        new Thread(()->{
            while (index<1000) {
                lock.lock();
                if(index%3==1){
                    System.out.println("B");
                    index++;
                }
                lock.unlock();
            }
        }).start();new Thread(()->{
            while (index<1000) {
                lock.lock();
                if(index%3==2){
                    System.out.println("C");
                    index++;
                }
                lock.unlock();
            }
        }).start();

    }


//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void test(){
        int[] temp=new int[5];
        Arrays.fill(temp,Integer.MAX_VALUE);
    }
}
