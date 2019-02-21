package com.honstat.house.manager;

import com.alibaba.otter.canal.protocol.CanalEntry;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.model.common
 * @Description: TODO
 * @date 2018/12/11 18:19
 */
@Data
public class MySqlChangeEntry implements Serializable {
    private Map changeBefore;
    private Map changeAfter;
    private CanalEntry.EventType eventType;
    private String tableName;
}
