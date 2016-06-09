dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
    dbCreate = "update"
    url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    singleSession = true
    flush.mode = 'manual'
}
