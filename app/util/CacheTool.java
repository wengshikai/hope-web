package util;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class obj_null{

}
public class CacheTool {
    private static LoadingCache<String, Object> cache;
    static{
        cache = CacheBuilder.newBuilder().maximumSize(20)
                .expireAfterWrite(24*60*60, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) throws Exception {
                        return new obj_null();
                    }
                });
    }
    public static void set(String key,Object object){
        cache.put(key, object);
    }

    public static void del(String key){
        cache.invalidate(key);
    }

    public static Object get(String key){
        try {
            Object obj = cache.get(key);
            if(obj instanceof obj_null){
                return null;
            }else{
                return obj;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer inc(String key){
        Object obj= get(key);
        if(obj == null){
            AtomicInteger i = new AtomicInteger();
            i.set(1);
            set(key, i);
            return i.get();
        }
        if(obj instanceof AtomicInteger){
            return ((AtomicInteger)obj).getAndIncrement()+1;
        }else if(obj instanceof Integer){
            AtomicInteger i = new AtomicInteger();
            i.set((Integer)obj);
            Integer ret = i.getAndIncrement()+1;
            set(key, i);
            return ret;
        }

        return null;
    }

    public static Integer dec(String key){
        Object obj= get(key);
        if(obj == null){
            AtomicInteger i = new AtomicInteger();
            i.set(-1);
            set(key,i);
            return i.get();
        }
        if(obj instanceof AtomicInteger){
            return ((AtomicInteger)obj).getAndDecrement()-1;
        }else if(obj instanceof Integer){
            AtomicInteger i = new AtomicInteger();
            i.set((Integer)obj);
            Integer ret = i.getAndIncrement()+1;
            set(key, i);
            return ret;
        }
        return null;
    }

    public static void main(String[] args){
        System.out.println(inc("hehe"));
        System.out.println(inc("hehe"));
        System.out.println(inc("hehe"));
        System.out.println(dec("hehe"));
        System.out.println(dec("hehe"));
        System.out.println(dec("hehe"));
        System.out.println(dec("hehe"));
        System.out.println(dec("hehe2"));
        System.out.println(dec("hehe2"));
        System.out.println(dec("hehe2"));
        System.out.println(dec("hehe2"));
        System.out.println(inc("hehe2"));
        System.out.println(inc("hehe2"));
    }

}


