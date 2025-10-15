#!/usr/bin/env groovy

/**
 * Ejecuta comandos SQL (INSERT, UPDATE, DELETE)
 * 
 * @param config Map con configuración:
 *   - driver: Driver JDBC
 *   - url: URL de conexión
 *   - user: Usuario
 *   - password: Password
 *   - command: Comando SQL a ejecutar (o lista de comandos)
 *   - transaction: (Opcional) Si es true, ejecuta como transacción
 * 
 * @return Número de filas afectadas
 * 
 * Ejemplo comando único:
 *   sqlExecute(
 *       driver: 'org.postgresql.Driver',
 *       url: 'jdbc:postgresql://localhost:5432/mydb',
 *       user: 'admin',
 *       password: 'password',
 *       command: "UPDATE users SET last_login = NOW() WHERE id = 1"
 *   )
 * 
 * Ejemplo con transacción:
 *   sqlExecute(
 *       driver: 'org.postgresql.Driver',
 *       url: 'jdbc:postgresql://localhost:5432/mydb',
 *       user: 'admin',
 *       password: 'password',
 *       command: [
 *           "INSERT INTO logs (message) VALUES ('Deploy started')",
 *           "UPDATE deployments SET status = 'in_progress' WHERE id = 1"
 *       ],
 *       transaction: true
 *   )
 */
def call(Map config) {
    def dbHelper = new org.miempresa.DatabaseHelper(this)
    def sql = null
    def result = 0
    
    try {
        // Crear conexión
        sql = dbHelper.createConnection([
            driver: config.driver,
            url: config.url,
            user: config.user,
            password: config.password
        ])
        
        // Ejecutar comando(s)
        if (config.command instanceof List && config.transaction) {
            // Múltiples comandos en transacción
            dbHelper.executeTransaction(sql, config.command)
        } else if (config.command instanceof List) {
            // Múltiples comandos sin transacción
            config.command.each { cmd ->
                result += dbHelper.executeCommand(sql, cmd)
            }
        } else {
            // Comando único
            result = dbHelper.executeCommand(sql, config.command)
        }
        
        return result
    } finally {
        dbHelper.closeConnection(sql)
    }
}

