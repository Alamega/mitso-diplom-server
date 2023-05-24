package com.alamega.alamegaspringapp;

import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SystemData {
    final int MAX_DATA_STORAGE = 10;
    final InfoRepository infoRepository;
    public Map<String, List<JSONObject>> All = new HashMap<>();

    public SystemData(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
        this.infoRepository.findAll().forEach(info -> {
            List<JSONObject> list = new ArrayList<>();
            JSONArray array = new JSONArray(info.getData());
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getJSONObject(i));
            }
            this.All.put(info.getMac(), list);
        });
    }

    public String getStatusByMacAndInfo(Info info) {
        String status = "";
        List<JSONObject> data = All.get(info.getMac());
        JSONObject config = new JSONObject(info.getConfig());

        double ramUsageCounter = 0;
        double cpuUsageCounter = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getJSONObject("ram").getInt("usage") > config.getJSONObject("ram").getInt("usage")) {
                ramUsageCounter++;
            }
            if (data.get(i).getInt("cpuusage") > config.getInt("cpuusage")) {
                cpuUsageCounter++;
            }
        }
        double ramUsagePercentage = ramUsageCounter * 100 / data.size();
        if (ramUsagePercentage > 10) {
            status += "Нагрузка на оперативную память превышает допустимое значение " + String.format("%.0f", ramUsagePercentage) + "% времени работы\n";
        }
        double cpuUsagePercentage = cpuUsageCounter * 100 / data.size();
        if (cpuUsagePercentage > 10) {
            status += "Общая нагрузка на процессор превышает допустимое значение " + String.format("%.0f", cpuUsagePercentage) + "% времени работы\n";
        }



        return status;
    }

    public void addJsonByMac(String mac, JSONObject json) {
        if (All.get(mac).size() > MAX_DATA_STORAGE - 1) {
            All.get(mac).remove(0);
        }
        All.get(mac).add(json);
        Info info = infoRepository.findByMac(mac);
        info.setData(new JSONArray(All.get(mac)).toString());
        info.setCurrentStatus(getStatusByMacAndInfo(info));
        infoRepository.save(info);
    }

    public void addServerInfo(JSONObject jsonObject) {
        Info info = infoRepository.findByMac(jsonObject.getString("mac"));
        if (info != null) {
            jsonObject.put("isonline", info.isOnline());
            jsonObject.put("status", info.getCurrentStatus());
        } else {
            jsonObject.put("isonline", false);
            jsonObject.put("status", "");
        }
    }

    public static void addToModel(Model model, JSONObject json) {
        model.addAttribute("mac",  json.get("mac"));
        model.addAttribute("cpuusage",  json.get("cpuusage"));
        model.addAttribute("cores",  json.get("cores"));
        model.addAttribute("os",  json.get("os"));
        model.addAttribute("username",  json.get("username"));
        model.addAttribute("ramusage",  ((JSONObject)json.get("ram")).get("usage"));
        model.addAttribute("ramtotal",  ((JSONObject)json.get("ram")).get("total"));
        model.addAttribute("discs",  json.get("discs"));
        model.addAttribute("discsphysical",  json.get("discsphysical"));
        model.addAttribute("cpuinfo",  json.get("cpuinfo"));
        model.addAttribute("gpuinfo",  json.get("gpuinfo"));
    }
}
