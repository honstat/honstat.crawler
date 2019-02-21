package com.honstat.house.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;

import com.honstat.crawler.service.utils.CountUtils;
import com.honstat.house.manager.MySqlChangeEntry;
import com.honstat.house.utils.HttpResponse;
import com.honstat.house.utils.HttpResponseBuild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.controller
 * @Description: TODO
 * @date 2018/12/6 11:26
 */
@RestController
@RequestMapping("/canal")
public class CanalClientController {
    @Autowired
    KafkaTemplate kafkaTemplate;
    @RequestMapping(value = "/run", method = RequestMethod.GET)
    public HttpResponse run() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                listener();
            }
        };
        new Thread(runnable).start();

        return new HttpResponseBuild().setData("Hello !").build();
    }
    @RequestMapping(value = "/testCanalConnection", method = RequestMethod.GET)
    public HttpResponse testCanalConnection() {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.131.3",
                11111), "example", "", "");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe("test\\.two_house_trade_info,test\\.two_house_sale_info,test\\.user_employee_1");
            connector.rollback();
            int totalEmptyCount = 120;
            while (emptyCount < totalEmptyCount) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
                } else {
                    emptyCount = 0;
                    System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getEntries());


                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }

            System.out.println("empty too many times, exit");
        }catch (Exception e){
            e.printStackTrace();
        }

        return new HttpResponseBuild().setData("fail !").build();
    }
    private void listener() {
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("172.21.250.66",
                11111), "example", "", "");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe("test\\.two_house_trade_info,test\\.two_house_sale_info,test\\.user_employee_1");
            connector.rollback();
            int totalEmptyCount = 120;
            while (emptyCount < totalEmptyCount) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
                } else {
                    emptyCount = 0;
                    System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getEntries());


                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }

            System.out.println("empty too many times, exit");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            connector.disconnect();
        }
    }


    private void printEntry(List<CanalEntry.Entry> entrys) {

        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            CanalEntry.EventType eventType = rowChage.getEventType();

            int keyIndex = 0;
            String mqTopicSub = "house_info";
            String key = "example";
            String mqTopic = "";
            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (keyIndex > 10) {
                    keyIndex = 0;
                }
                keyIndex++;
                mqTopic = mqTopicSub + "_" + keyIndex;
                MySqlChangeEntry changeEntry = new MySqlChangeEntry();
                changeEntry.setTableName(entry.getHeader().getTableName());
                changeEntry.setEventType(eventType);
                Map<String, String> beforeMap = new HashMap<>();
                if (eventType == CanalEntry.EventType.DELETE) {
                    beforeMap = rowData.getBeforeColumnsList().stream().collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
                    changeEntry.setChangeBefore(beforeMap);
                    //    printColumn(rowData.getBeforeColumnsList());

                } else if (eventType == CanalEntry.EventType.INSERT) {

                    beforeMap = rowData.getAfterColumnsList().stream().collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
                    changeEntry.setChangeBefore(beforeMap);

                } else {

                    //普通队列


                    Map<String, String> afterMap = new HashMap<>();
                    beforeMap = rowData.getBeforeColumnsList().stream().collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

                    changeEntry.setChangeBefore(beforeMap);
                    afterMap = rowData.getAfterColumnsList().stream().filter(x -> x.getUpdated()).collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
                    changeEntry.setChangeAfter(afterMap);


                }

                kafkaTemplate.send(mqTopic, key, JSON.toJSONString(changeEntry));
                CountUtils.addCount("mq_current_key");
                CountUtils.addCount("mq_total_key");
            }
        }
    }

    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}
