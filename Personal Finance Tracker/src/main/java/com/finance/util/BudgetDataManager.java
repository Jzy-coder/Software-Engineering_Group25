package com.finance.util;

import com.finance.controller.BudgetPlan;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BudgetDataManager {
    private static final String DATA_FILE = "data/budget_data.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, context) ->
                    new JsonPrimitive(date.toString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) ->
                    LocalDate.parse(json.getAsString()))
            .setPrettyPrinting()
            .create();

    public static void saveBudgetPlans(List<BudgetPlan> plans) {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        try (Writer writer = new FileWriter(DATA_FILE)) {
            gson.toJson(plans, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<BudgetPlan> loadBudgetPlans() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(DATA_FILE)) {
            return gson.fromJson(reader, new TypeToken<List<BudgetPlan>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}