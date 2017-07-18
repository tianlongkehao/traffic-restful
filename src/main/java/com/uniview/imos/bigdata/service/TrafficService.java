package com.uniview.imos.bigdata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniview.imos.bigdata.dao.TrafficDao;
import com.uniview.imos.bigdata.entity.TrafficBean;

import java.io.IOException;
import java.util.List;

@Service
public class TrafficService {

    @Autowired
    private TrafficDao dao;

    public TrafficBean saveSession(String rowkey, String deviceID, String direction, String drivingStatusCode) {
        TrafficBean sd = new TrafficBean();
        sd.setTrafficRowKey(rowkey);
        sd.setDeviceID(deviceID);
        sd.setDirection(direction);
        sd.setDrivingStatusCode(drivingStatusCode);

        dao.save(sd);

        return sd;
    }

    public int saveSessions(String rowkey, int start, int end, String deviceID, String direction, String drivingStatusCode) {
        int count = 0;

        // The naive way to do this is to iterate from start to end, creating a new Put for each object.
        // TODO: Save in batches. Possibly spawn multiple threads.
        for (int i=start; i<=end; i++) {
            TrafficBean sd = new TrafficBean();
            sd.setTrafficRowKey(rowkey + String.format("%020d", i));
            sd.setDeviceID(deviceID);
            sd.setDirection(direction);
            sd.setDrivingStatusCode(drivingStatusCode);
            dao.save(sd);

            count++;
        }

        return count;
    }

    public TrafficBean getSession(String sessionId) {
        return dao.findOne(sessionId);
    }

    public List<TrafficBean> getSessions() {
        return dao.findAll();
    }

    public void recreateTable() throws IOException {
        dao.drop();
        dao.init();
    }
}
