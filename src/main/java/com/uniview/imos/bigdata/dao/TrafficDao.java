package com.uniview.imos.bigdata.dao;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Repository;

import com.uniview.imos.bigdata.entity.TrafficBean;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Repository
public class TrafficDao {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    private String tableNameStr = "AutoVehicleRecord_201705";
    private String columnFamily = "f";
    private byte[] cfTrafficBean = Bytes.toBytes(columnFamily);
    private byte[] qDeviceID = Bytes.toBytes("DeviceID");
    private byte[] qDirection = Bytes.toBytes("Direction");
    private byte[] qDrivingStatusCode = Bytes.toBytes("DrivingStatusCode");

    private Admin admin;
    private TableName tn;

    public TrafficDao() { }

    @PostConstruct
    public void init() throws IOException {
        admin = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration()).getAdmin();
        tn = TableName.valueOf(tableNameStr);

        if (!admin.tableExists(tn)) {
            HTableDescriptor tableDescriptor = new HTableDescriptor(tn);
            HColumnDescriptor columnDescriptor = new HColumnDescriptor(cfTrafficBean);
            columnDescriptor.setTimeToLive(432000); // five days
            tableDescriptor.addFamily(columnDescriptor);
            admin.createTable(tableDescriptor);

            
            //TODO: Log info message about creating table.
        }
    }

    public void drop() throws IOException {
        if (admin.isTableEnabled(tn)) {
            admin.disableTable(tn);
        }
        admin.deleteTable(tn);
    }

    public void save(TrafficBean tb) {
        hbaseTemplate.execute(tableNameStr, new TableCallback<Object>() {
            public Object doInTable(HTableInterface table) throws Throwable {
                Put p = new Put(Bytes.toBytes(tb.getTrafficRowKey()));
                p.addColumn(cfTrafficBean, qDeviceID, Bytes.toBytes(tb.getDeviceID()));
                p.addColumn(cfTrafficBean, qDirection, Bytes.toBytes(tb.getDirection()));
                p.addColumn(cfTrafficBean, qDrivingStatusCode, Bytes.toBytes(tb.getDrivingStatusCode()));
                table.put(p);

                return null;
            }
        });
    }

    public List<TrafficBean> findAll() {
        Scan scan = new Scan();
        scan.setMaxResultSize(100l); // TODO: Investigate. This setting is ignored on my local standalone installation.

        return hbaseTemplate.find(tableNameStr, scan, new TrafficBeanRowMapper());
    }

    public TrafficBean findOne(String sessionId) {
        TrafficBean tb = hbaseTemplate.get(tableNameStr, sessionId, columnFamily, new TrafficBeanRowMapper());

        if (tb.getTrafficRowKey() == null) {
            tb = null;
        }

        return tb;
    }

    private class TrafficBeanRowMapper implements RowMapper<TrafficBean> {
        @Override
        public TrafficBean mapRow(Result result, int rowNum) throws Exception {
            TrafficBean sd = new TrafficBean();
            sd.setTrafficRowKey(Bytes.toString(result.getRow()));
            sd.setDeviceID(Bytes.toString(result.getValue(cfTrafficBean, qDeviceID)));
            sd.setDirection(Bytes.toString(result.getValue(cfTrafficBean, qDirection)));
            sd.setDrivingStatusCode(Bytes.toString(result.getValue(cfTrafficBean, qDrivingStatusCode)));

            return sd;
        }
    }

}
