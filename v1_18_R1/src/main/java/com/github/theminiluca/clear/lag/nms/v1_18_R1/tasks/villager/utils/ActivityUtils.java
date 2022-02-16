package com.github.theminiluca.clear.lag.nms.v1_18_R1.tasks.villager.utils;

import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.entity.memory.MemoryKey;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class ActivityUtils {
    private static Method VILLAGER_GET_HANDLE_METHOD;
    private static Method VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD;
    private static Method BEHAVIOUR_CONTROLLER_GET_SCHEDULE_METHOD;
    private static Method CURRENT_ACTIVITY_METHOD;
    private static Method SET_SCHEDULE_METHOD;

    private static Field ACTIVITIES_FIELD;

    private static Object ACTIVITY_CORE;
    private static Object ACTIVITY_IDLE;
    private static Object ACTIVITY_WORK;
    private static Object ACTIVITY_MEET;
    private static Object ACTIVITY_REST;
    private static Object SCHEDULE_EMPTY;
    private static Object SCHEDULE_SIMPLE;
    private static Object SCHEDULE_VILLAGER_DEFAULT;
    private static Object SCHEDULE_VILLAGER_BABY;

    static {
        try {
            VILLAGER_GET_HANDLE_METHOD = Class.forName("org.bukkit.craftbukkit.v1_18_R1.entity.CraftVillager").getMethod("getHandle");
            VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD = Class.forName("net.minecraft.world.entity.EntityLiving").getMethod("dt");
            BEHAVIOUR_CONTROLLER_GET_SCHEDULE_METHOD = Class.forName("net.minecraft.world.entity.ai.BehaviorController").getMethod("b");
            CURRENT_ACTIVITY_METHOD = Class.forName("net.minecraft.world.entity.schedule.Schedule").getMethod("a", int.class);
            SET_SCHEDULE_METHOD = Class.forName("net.minecraft.world.entity.ai.BehaviorController").getMethod("a", Class.forName("net.minecraft.world.entity.schedule.Schedule"));


            ACTIVITIES_FIELD = Class.forName("net.minecraft.world.entity.ai.BehaviorController").getDeclaredField("j");
            ACTIVITIES_FIELD.setAccessible(true);

            ACTIVITY_CORE = Class.forName("net.minecraft.world.entity.schedule.Activity").getField("a").get(null);
            ACTIVITY_IDLE = Class.forName("net.minecraft.world.entity.schedule.Activity").getField("b").get(null);
            ACTIVITY_WORK = Class.forName("net.minecraft.world.entity.schedule.Activity").getField("c").get(null);
            ACTIVITY_MEET = Class.forName("net.minecraft.world.entity.schedule.Activity").getField("f").get(null);
            ACTIVITY_REST = Class.forName("net.minecraft.world.entity.schedule.Activity").getField("e").get(null);
            SCHEDULE_EMPTY = Class.forName("net.minecraft.world.entity.schedule.Schedule").getField("c").get(null);
            SCHEDULE_SIMPLE = Class.forName("net.minecraft.world.entity.schedule.Schedule").getField("d").get(null);
            SCHEDULE_VILLAGER_DEFAULT = Class.forName("net.minecraft.world.entity.schedule.Schedule").getField("f").get(null);
            SCHEDULE_VILLAGER_BABY = Class.forName("net.minecraft.world.entity.schedule.Schedule").getField("e").get(null);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setActivitiesNormal(Villager villager) {
        try {
            ((Set) ACTIVITIES_FIELD.get(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager)))).clear();
            ((Set) ACTIVITIES_FIELD.get(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager)))).add(ACTIVITY_CORE);
            Object currentSchedule = BEHAVIOUR_CONTROLLER_GET_SCHEDULE_METHOD.invoke(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager)));
            Object currentActivity;
            if (currentSchedule == null) {
                currentActivity = ACTIVITY_IDLE;
            } else {
                currentActivity = CURRENT_ACTIVITY_METHOD.invoke(currentSchedule, (int) villager.getWorld().getTime());
            }
            ((Set) ACTIVITIES_FIELD.get(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager)))).add(currentActivity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void setActivitiesEmpty(Villager villager) {
        try {
            ((Set) ACTIVITIES_FIELD.get(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager)))).clear();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void setScheduleNormal(Villager villager) {
        try {
            SET_SCHEDULE_METHOD.invoke(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager)), villager.isAdult() ? (villager.getProfession() == Villager.Profession.NITWIT ? SCHEDULE_SIMPLE : SCHEDULE_VILLAGER_DEFAULT) : SCHEDULE_VILLAGER_BABY);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void setScheduleEmpty(Villager villager) {
        try {
            SET_SCHEDULE_METHOD.invoke(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager)), SCHEDULE_EMPTY);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static boolean badCurrentActivity(Villager villager) {
        try {
            Object currentSchedule = BEHAVIOUR_CONTROLLER_GET_SCHEDULE_METHOD.invoke(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager)));
            if (currentSchedule == null) {
                return false;
            }
            Object currentActivity = CURRENT_ACTIVITY_METHOD.invoke(currentSchedule, (int) villager.getWorld().getTime());
            return badActivity(currentActivity, villager);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }

        return false;
    }

    public static boolean wouldBeBadActivity(Villager villager) {
        Object wouldBeSchedule = villager.isAdult() ? (villager.getProfession() == Villager.Profession.NITWIT ? SCHEDULE_VILLAGER_DEFAULT : SCHEDULE_SIMPLE) : SCHEDULE_VILLAGER_BABY;
        try {
            Object currentActivity = CURRENT_ACTIVITY_METHOD.invoke(wouldBeSchedule, (int) villager.getWorld().getTime());
            return badActivity(currentActivity, villager);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }

        return false;
    }

    private static boolean badActivity(Object activity, Villager villager) {
        if (activity == ACTIVITY_REST) {
            return villager.getMemory(MemoryKey.HOME) == null || isPlaceholderMemory(villager, MemoryKey.HOME);
        }
        if (activity == ACTIVITY_WORK) {
            return (villager.getMemory(MemoryKey.JOB_SITE) == null || isPlaceholderMemory(villager, MemoryKey.JOB_SITE));
        }
        if (activity == ACTIVITY_MEET) {
            return villager.getMemory(MemoryKey.MEETING_POINT) == null || isPlaceholderMemory(villager, MemoryKey.MEETING_POINT);
        }

        return false;
    }

    public static void replaceBadMemories(Villager villager) {
        if (villager.getMemory(MemoryKey.HOME) == null) {
            villager.setMemory(MemoryKey.HOME, new Location(villager.getWorld(), villager.getLocation().getBlockX(), -10000, villager.getLocation().getBlockZ()));
        }
        if (villager.getMemory(MemoryKey.JOB_SITE) == null) {
            villager.setMemory(MemoryKey.JOB_SITE, new Location(villager.getWorld(), villager.getLocation().getBlockX(), -10000, villager.getLocation().getBlockZ()));
        }
        if (villager.getMemory(MemoryKey.MEETING_POINT) == null) {
            villager.setMemory(MemoryKey.MEETING_POINT, new Location(villager.getWorld(), villager.getLocation().getBlockX(), -10000, villager.getLocation().getBlockZ()));
        }
    }

    public static boolean isPlaceholderMemory(Villager villager, MemoryKey<Location> memoryKey) {
        Location memoryLocation = villager.getMemory(memoryKey);
        return memoryLocation != null && memoryLocation.getY() < 0;
    }

    public static void clearPlaceholderMemories(Villager villager) {

        if (villager.getMemory(MemoryKey.HOME) != null && isPlaceholderMemory(villager, MemoryKey.HOME)) {
            villager.setMemory(MemoryKey.HOME, null);
        }
        if (villager.getMemory(MemoryKey.JOB_SITE) != null && isPlaceholderMemory(villager, MemoryKey.JOB_SITE)) {
            villager.setMemory(MemoryKey.JOB_SITE, null);
        }
        if (villager.getMemory(MemoryKey.MEETING_POINT) != null && isPlaceholderMemory(villager, MemoryKey.MEETING_POINT)) {
            villager.setMemory(MemoryKey.MEETING_POINT, null);
        }
    }

    public static boolean isScheduleNormal(Villager villager) {
        try {
            return BEHAVIOUR_CONTROLLER_GET_SCHEDULE_METHOD.invoke(VILLAGER_GET_BEHAVIOUR_CONTROLLER_METHOD.invoke(VILLAGER_GET_HANDLE_METHOD.invoke(villager))) == (villager.isAdult() ? (villager.getProfession() == Villager.Profession.NITWIT ? SCHEDULE_SIMPLE : SCHEDULE_VILLAGER_DEFAULT) : SCHEDULE_VILLAGER_BABY);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }
}
