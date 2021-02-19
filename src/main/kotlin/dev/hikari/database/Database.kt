package dev.hikari.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.hikari.config.ShiroConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSettings {
    val db: Database by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = ShiroConfig.config.database.url
            driverClassName = ShiroConfig.config.database.driverClassName
            username = ShiroConfig.config.database.username
            password = ShiroConfig.config.database.password
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        val db = Database.connect(dataSource)
        transaction(db) {
            SchemaUtils.createMissingTablesAndColumns(History)
        }
        db
    }
}

val database: Database
    get() = DbSettings.db