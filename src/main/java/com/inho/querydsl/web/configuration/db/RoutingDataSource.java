package com.inho.querydsl.web.configuration.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * AbstractRoutingDataSource 는 key 기반으로 등록된 Datasource 중 하나를 호출하게 해줍니다.
 * @Trasnsaction (readOnly = true ) : Slave DB 접근
 * @Trasnsaction (readOnly = false ) : Master DB 접근
 */
@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if(isReadOnly)
            DbContextHolder.slave();
        else
            DbContextHolder.master();
        DataSourceType dataSourceType = isReadOnly ? DataSourceType.Slave : DataSourceType.Master;
        log.info("[REPLACTION] AbstractRoutingDataSource.determineCurrentLookupKey : {}",dataSourceType);
        return dataSourceType;
    }
}
