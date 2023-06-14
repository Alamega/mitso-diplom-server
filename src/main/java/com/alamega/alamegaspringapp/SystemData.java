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
    final int MAX_DATA_STORAGE = 100;

    final int MIN_WARNING_PERCENTAGE = 10;
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

    public String getStatus(Info info) {
        String status = "";
        List<JSONObject> data = All.get(info.getMac());
        JSONObject config = new JSONObject(info.getConfig());

        //Оперативная память и проц
        double ramUsageCounter = 0;
        double cpuUsageCounter = 0;
        for (int i = 0; i < data.size(); i++) {
            if ((data.get(i).getJSONObject("ram").getDouble("usage") / data.get(i).getJSONObject("ram").getDouble("total") * 100) > config.getJSONObject("ram").getInt("usage")) {
                ramUsageCounter++;
            }
            if (data.get(i).getInt("cpuusage") > config.getInt("cpuusage")) {
                cpuUsageCounter++;
            }
        }
        double ramUsagePercentage = ramUsageCounter * 100 / data.size();
        if (ramUsagePercentage > MIN_WARNING_PERCENTAGE) {
            status += "Нагрузка на оперативную память превышает допустимое значение " + String.format("%.0f", ramUsagePercentage) + "% времени работы\n";
        }
        double cpuUsagePercentage = cpuUsageCounter * 100 / data.size();
        if (cpuUsagePercentage > MIN_WARNING_PERCENTAGE) {
            status += "Общая нагрузка на процессор превышает допустимое значение " + String.format("%.0f", cpuUsagePercentage) + "% времени работы\n";
        }

        //Графические процессоры
        JSONArray gpus = new JSONArray(config.getJSONArray("gpuinfo"));
        for (int i = 0; i < gpus.length(); i++) {
            JSONObject gpuFromConfig = gpus.getJSONObject(i);
            double gpuUsageCounter = 0;
            double gpuTempCounter = 0;
            for (int j = 0; j < data.size(); j++) {
                JSONObject gpuFromData = data.get(j).getJSONArray("gpuinfo").getJSONObject(i);
                if (gpuFromData.getDouble("load") > gpuFromConfig.getDouble("load")) {
                    gpuUsageCounter++;
                }
                if (gpuFromData.getDouble("temperature") > gpuFromConfig.getDouble("temperature")) {
                    gpuTempCounter++;
                }
            }
            double gpuUsagePercentage = gpuUsageCounter * 100 / data.size();
            if (gpuUsagePercentage > MIN_WARNING_PERCENTAGE) {
                status += "Нагрузка на графический процессор " + gpuFromConfig.getString("name") + " превышает допустимое значение " + String.format("%.0f", gpuUsagePercentage) + "% времени работы\n";
            }
            double gpuTempPercentage = gpuTempCounter * 100 / data.size();
            if (gpuUsagePercentage > MIN_WARNING_PERCENTAGE) {
                status += "Температура графического процессора " + gpuFromConfig.getString("name") + " превышает допустимое значение " + String.format("%.0f", gpuTempPercentage) + "% времени работы\n";
            }
        }

        //Процессоры
        JSONArray cpusConfig = new JSONArray(config.getJSONArray("cpuinfo"));
        for (int cpuCounter = 0; cpuCounter < cpusConfig.length(); cpuCounter++) {
            String cpuName = cpusConfig.getJSONObject(cpuCounter).getString("name");
            JSONArray coresConfig = cpusConfig.getJSONObject(cpuCounter).getJSONArray("cores");
            for (int coreCounter = 0; coreCounter < coresConfig.length(); coreCounter++) {
                JSONObject coreFromConfig = coresConfig.getJSONObject(coreCounter);
                double coreUsageCounter = 0;
                double coreTempCounter = 0;
                for (int i = 0; i < data.size(); i++) {
                    JSONObject coreFromData = data.get(i).getJSONArray("cpuinfo").getJSONObject(cpuCounter).getJSONArray("cores").getJSONObject(coreCounter);
                    if (coreFromData.getInt("load") > coreFromConfig.getDouble("load")) {
                        coreUsageCounter++;
                    }
                    if (coreFromData.getInt("temperature") > coreFromConfig.getDouble("temperature")) {
                        coreTempCounter++;
                    }
                }
                double coreUsagePercentage = coreUsageCounter * 100 / data.size();
                if (coreUsagePercentage > MIN_WARNING_PERCENTAGE) {
                    status += "Нагрузка на ядро " + coreFromConfig.getString("name") + " процессора " + cpuName + " превышает допустимое значение " + String.format("%.0f", coreUsagePercentage) + "% времени работы\n";
                }
                double coreTempPercentage = coreTempCounter * 100 / data.size();
                if (coreTempPercentage > MIN_WARNING_PERCENTAGE) {
                    status += "Температура ядра " + coreFromConfig.getString("name") + " процессора " + cpuName + " превышает допустимое значение " + String.format("%.0f", coreTempPercentage) + "% времени работы\n";
                }
            }

        }

        //Физические диски
        JSONArray discsphysical = new JSONArray(config.getJSONArray("discsphysical"));
        for (int i = 0; i < discsphysical.length(); i++) {
            JSONObject discFFromConfig = discsphysical.getJSONObject(i);
            double diskFUsageCounter = 0;
            for (int j = 0; j < data.size(); j++) {
                JSONObject diskFFromData = data.get(j).getJSONArray("discsphysical").getJSONObject(i);
                if (diskFFromData.getDouble("load") > discFFromConfig.getDouble("load")) {
                    diskFUsageCounter++;
                }
            }
            double diskFUsagePercentage = diskFUsageCounter * 100 / data.size();
            if (diskFUsagePercentage > MIN_WARNING_PERCENTAGE) {
                status += "Нагрузка на физический диск " + discFFromConfig.getString("name") + " превышает допустимое значение " + String.format("%.0f", diskFUsagePercentage) + "% времени работы\n";
            }
        }

        //Логические диски
        JSONArray discs = new JSONArray(config.getJSONArray("discs"));
        for (int i = 0; i < discs.length(); i++) {
            JSONObject discFromConfig = discs.getJSONObject(i);
            double diskUsageCounter = 0;
            for (int j = 0; j < data.size(); j++) {
                JSONObject diskFromData = data.get(j).getJSONArray("discs").getJSONObject(i);
                if ((diskFromData.getDouble("usage") / diskFromData.getDouble("total") * 100) > discFromConfig.getDouble("usage")) {
                    diskUsageCounter++;
                }
            }
            double diskUsagePercentage = diskUsageCounter * 100 / data.size();
            if (diskUsagePercentage > MIN_WARNING_PERCENTAGE) {
                status += "Нагрузка на логический диск " + discFromConfig.getString("name") + " превышает допустимое значение " + String.format("%.0f", diskUsagePercentage) + "% времени работы\n";
            }
        }
        return status;
    }

    public void addJsonByMac(String mac, JSONObject json) {
        //Добавить если нету
        if (!All.containsKey(mac)) {
            All.put(mac, new ArrayList<>());

            //Создаем поле настроек для бд
            JSONObject jsonConfig = new JSONObject(json.toString());
            jsonConfig.put("cpuusage", 100);
            jsonConfig.getJSONObject("ram").put("usage", 100);
            for (int i = 0; i < jsonConfig.getJSONArray("gpuinfo").length(); i++) {
                jsonConfig.getJSONArray("gpuinfo").getJSONObject(i).put("load", 100);
            }
            for (int i = 0; i < jsonConfig.getJSONArray("discs").length(); i++) {
                jsonConfig.getJSONArray("discs").getJSONObject(i).put("usage", 100);
            }
            for (int i = 0; i < jsonConfig.getJSONArray("discsphysical").length(); i++) {
                jsonConfig.getJSONArray("discsphysical").getJSONObject(i).put("load", 100);
            }
            for (int i = 0; i < jsonConfig.getJSONArray("cpuinfo").length(); i++) {
                JSONArray array = jsonConfig.getJSONArray("cpuinfo").getJSONObject(i).getJSONArray("cores");
                for (int j = 0; j < array.length(); j++) {
                    array.getJSONObject(j).put("load", 100);
                    array.getJSONObject(j).put("temperature", 100);
                }
            }
            Info newInfo = new Info(json.getString("mac"), jsonConfig.toString());
            newInfo.setOnline(true);
            infoRepository.save(newInfo);
        }

        //Удалить если много
        while (All.get(mac).size() > MAX_DATA_STORAGE - 1) {
            All.get(mac).remove(0);
        }

        All.get(mac).add(json);
        Info info = infoRepository.findByMac(mac);
        info.setData(new JSONArray(All.get(mac)).toString());
        info.setCurrentStatus(getStatus(info));
        infoRepository.save(info);
    }

    public void addServerInfo(JSONObject jsonObject) {
        Info info = infoRepository.findByMac(jsonObject.getString("mac"));
        jsonObject.put("isonline", info.isOnline());
        jsonObject.put("status", info.getCurrentStatus());
    }

    public void resetSession(String mac) {
        JSONObject last = All.get(mac).get(All.get(mac).size()-1);
        All.get(mac).clear();
        All.get(mac).add(last);
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
