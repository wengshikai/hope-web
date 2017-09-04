package models.util;

import models.dbtable.TaskTables;
import models.excel.BuyerTask;
import models.excel.ShopkeeperTask;
import models.excel.ShopkeeperTaskBook;
import models.excel.ShopkeeperTaskList;
import models.excel.BuyerTaskList;
import util.FileTool;
import util.LocalStoreTool;
import util.ZIPTool;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by weng on 15-12-26.
 */
public class TaskHelper {
    public static Map<String,ShopkeeperTaskBook>  getShopkeeperTaskBook(List<TaskTables> all){
        Map<String,ShopkeeperTaskList> stbmap = new HashMap<String,ShopkeeperTaskList>();
        for(TaskTables task:all){
            String uuid = task.getTaskbookUuid()+task.getSubTaskbookId();
            ShopkeeperTask stk = new ShopkeeperTask();
            stk.initByTables(task);
            if(!stbmap.containsKey(uuid)){
                ShopkeeperTaskList stl = new ShopkeeperTaskList();
                stl.initByTask(stk);
                stbmap.put(uuid,stl);
            }
            ShopkeeperTaskList stl = stbmap.get(uuid);
            stl.addShopkeeperTask(stk);
        }

        Map<String,ShopkeeperTaskBook> bookMap = new HashMap<String,ShopkeeperTaskBook>();
        for(Map.Entry<String,ShopkeeperTaskList> entry:stbmap.entrySet()){
            String uuid = entry.getValue().getTasklist().get(0).getTaskbookName();
            if(!bookMap.containsKey(uuid)){
                ShopkeeperTaskBook stb = new ShopkeeperTaskBook();
                stb.initByTask(entry.getValue());
                bookMap.put(uuid,stb);
            }
            ShopkeeperTaskBook stb = bookMap.get(uuid);
            stb.addShopkeeperTaskList(entry.getValue());
        }
        return bookMap;
    }

    public static Map<String,BuyerTaskList>  getBuyerTaskBook(List<TaskTables> all){
        Map<Integer,String> idtobuyer = new HashMap<Integer,String>();
        Map<Integer,BuyerTaskList> stlmap = new HashMap<Integer,BuyerTaskList>();
        for(TaskTables task:all){
            idtobuyer.put(task.getBuyerTaskBookId(),task.getBuyerWangwang());
            Integer sbid = task.getBuyerTaskBookId();
            if(!stlmap.containsKey(sbid)){
                BuyerTaskList stl = new BuyerTaskList();
                stl.setFilepath("exceltmp/"+sbid+".xls");
                stl.setTaskbookid(sbid);
                stlmap.put(sbid,stl);
            }
            BuyerTaskList stl = stlmap.get(sbid);
            BuyerTask sst = new BuyerTask();
            sst.setShopname(task.getShopName());
            sst.setGoodnum(task.getGoodsNumber());
            sst.setUnit_price(task.getUnitPrice());
            sst.setAllPrice(task.getAllPrice());
            sst.setKeyword(task.getKeyword());
            String pic1 = task.getPic1();
            String pic2 = task.getPic2();
            String pic3 = task.getPic3();
            sst.setRequirement(task.getTaskRequirement());
            sst.setPhoneCost(task.getPhoneCost());
            stl.addShuashouTask(sst);

        }

        Map<String,BuyerTaskList> taskMap  = new HashMap<String,BuyerTaskList>();
        for (Map.Entry<Integer,BuyerTaskList> entry: stlmap.entrySet()) {
            BuyerTaskList sstl = (BuyerTaskList)entry.getValue();
            BigDecimal   b1   =   new BigDecimal(sstl.getZongBenJinNum());
            double   f1bj   =   b1.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

            double benjin = +sstl.getZongBenJinNum() + sstl.getZongYongjinNum();
            BigDecimal   b2   =   new BigDecimal(benjin);
            double   f2benjin   =   b2.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

            String real_n =  entry.getKey()+"号-共"+sstl.getZongDanShuNum()+"单-"+f1bj+
                    "+"+sstl.getZongYongjinNum()+"="+f2benjin+"元-"+idtobuyer.get(entry.getKey())+".xls";
            taskMap.put(real_n,sstl);
        }

        return taskMap;


    }
}
