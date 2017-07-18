package com.uniview.imos.bigdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uniview.imos.bigdata.entity.TrafficBean;
import com.uniview.imos.bigdata.service.TrafficService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/session")
public class TrafficController {

    @Autowired
    private TrafficService service;

    @RequestMapping(value="/addOne", method=RequestMethod.POST)
    public TrafficBean postSession(@RequestParam Map<String, String> params) {
        String sessionId = params.get("sessionId");
        String identity = params.get("identity");
        String platform = params.get("platform");
        String providerId = params.get("providerId");

        TrafficBean sd = service.saveSession(sessionId, identity, platform, providerId);

        return sd;
    }

    @RequestMapping(value="/addMany", method=RequestMethod.POST)
    public int postSessions(@RequestParam Map<String, String> params) {
        String sessionIdPrefix = params.get("sessionIdPrefix");
        int start = Integer.parseInt(params.get("start"));
        int end = Integer.parseInt(params.get("end"));
        String identity = params.get("identity");
        String platform = params.get("platform");
        String providerId = params.get("providerId");

        int count = service.saveSessions(sessionIdPrefix, start, end, identity, providerId, platform);

        return count;
    }

    @RequestMapping(value="/getOne", method=RequestMethod.GET)
    public TrafficBean getSession(@RequestParam("sessionId") String sessionId) {
        return service.getSession(sessionId);
    }

    @RequestMapping(value="/getAll", method=RequestMethod.GET)
    public List<TrafficBean> getSessions() {
        return service.getSessions();
    }

    @RequestMapping(value="/recreateTable", method=RequestMethod.GET)
    public void recreateTable() throws IOException {
        service.recreateTable();
    }

}
