#!/usr/bin/env groovy

/**
 * Hace backup de una tabla a archivo CSV
 * 
 * @param config Map con configuración:
 *   - driver: Driver JDBC
 *   - url: URL de conexión
 *   - user: Usuario
 *   - password: Password
 *   - table: Nombre de la tabla
 *   - backupFile: Archivo de destino
 * 
 * Ejemplo:
 *   sqlBackup(
 *       driver: 'org.postgresql.Driver',
 *       url: 'jdbc:postgresql://localhost:5432/mydb',
 *       user: 'admin',
 *       password: 'password',
 *       table: 'users',
 *       backupFile: 'backups/users_20251015.csv'
 *   )
 */
def call(Map config) {
    def dbHelper = new org.miempresa.DatabaseHelper(this)
    def sql = null
    
    try {
        // Crear conexión
        sql = dbHelper.createConnection([
            driver: config.driver,
            url: config.url,
            user: config.user,
            password: config.password
        ])
        
        // Hacer backup
        dbHelper.backupTable(sql, config.table, config.backupFile)
        
        echo "💾 Backup guardado en: ${config.backupFile}"
    } finally {
        dbHelper.closeConnection(sql)
    }
}

