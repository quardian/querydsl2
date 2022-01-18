package com.inho.querydsl.web.configuration.db;

import java.util.concurrent.atomic.AtomicInteger;

public class DbContextHolder {
    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();
    private static final AtomicInteger counter = new AtomicInteger(-1);

    public static void set(DataSourceType dataSourceType){
        contextHolder.set(dataSourceType);
    }

    public static DataSourceType get(){
        return contextHolder.get();
    }

    public static void master(){
        set(DataSourceType.Master);
    }

    public static void slave(){
        set(DataSourceType.Slave);
    }
}
