package dev.hikari.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.hikari.config.ShiroConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object DbSettings {

    val mySql: Database by lazy {
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

    val sqlite: Database by lazy {
        val db = Database.connect("jdbc:sqlite:./data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction(db) {
            SchemaUtils.createMissingTablesAndColumns(History)
        }
        db
    }

    val configValid: Boolean by lazy {
        val dbConfig = ShiroConfig.config.database
        !(dbConfig.username == null || dbConfig.password == null || dbConfig.driverClassName == null || dbConfig.url == null)
    }
}

val database: Database
    get() = DbSettings.sqlite