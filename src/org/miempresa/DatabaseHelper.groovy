package org.miempresa

import groovy.sql.Sql

/**
 * Clase para manejar conexiones a bases de datos SQL
 */
class DatabaseHelper implements Serializable {
    
    def steps
    
    DatabaseHelper(steps) {
        this.steps = steps
    }
    
    /**
     * Crea una conexión a base de datos
     * @param config Map con: driver, url, user, password
     * @return Conexión SQL
     */
    def createConnection(Map config) {
        steps.echo "🔌 Conectando a base de datos: ${config.url}"
        
        try {
            // Cargar driver JDBC
            Class.forName(config.driver)
            
            // Crear conexión
            def sql = Sql.newInstance(
                config.url,
                config.user,
                config.password,
                config.driver
            )
            
            steps.echo "✅ Conexión exitosa"
            return sql
        } catch (Exception e) {
            steps.echo "❌ Error al conectar: ${e.message}"
            throw e
        }
    }
    
    /**
     * Ejecuta una query SELECT y retorna resultados
     * @param sql Conexión SQL
     * @param query Query a ejecutar
     * @return Lista de resultados
     */
    def executeQuery(def sql, String query) {
        steps.echo "📊 Ejecutando query: ${query.take(100)}..."
        
        def results = []
        try {
            sql.eachRow(query) { row ->
                results << row.toRowResult()
            }
            steps.echo "✅ Query ejecutada. ${results.size()} filas obtenidas"
            return results
        } catch (Exception e) {
            steps.echo "❌ Error en query: ${e.message}"
            throw e
        }
    }
    
    /**
     * Ejecuta un comando SQL (INSERT, UPDATE, DELETE)
     * @param sql Conexión SQL
     * @param command Comando a ejecutar
     * @return Número de filas afectadas
     */
    def executeCommand(def sql, String command) {
        steps.echo "⚙️ Ejecutando comando: ${command.take(100)}..."
        
        try {
            def rowsAffected = sql.executeUpdate(command)
            steps.echo "✅ Comando ejecutado. ${rowsAffected} filas afectadas"
            return rowsAffected
        } catch (Exception e) {
            steps.echo "❌ Error en comando: ${e.message}"
            throw e
        }
    }
    
    /**
     * Ejecuta múltiples comandos en una transacción
     * @param sql Conexión SQL
     * @param commands Lista de comandos
     */
    def executeTransaction(def sql, List commands) {
        steps.echo "🔄 Iniciando transacción (${commands.size()} comandos)..."
        
        try {
            sql.withTransaction {
                commands.each { cmd ->
                    sql.executeUpdate(cmd)
                }
            }
            steps.echo "✅ Transacción completada exitosamente"
        } catch (Exception e) {
            steps.echo "❌ Error en transacción (rollback): ${e.message}"
            throw e
        }
    }
    
    /**
     * Exporta resultados de query a CSV
     * @param results Lista de resultados
     * @param filename Nombre del archivo
     */
    def exportToCSV(List results, String filename) {
        if (!results) {
            steps.echo "⚠️ No hay datos para exportar"
            return
        }
        
        steps.echo "📄 Exportando ${results.size()} filas a ${filename}..."
        
        def csv = new StringBuilder()
        
        // Headers
        def headers = results[0].keySet().join(',')
        csv.append(headers).append('\n')
        
        // Rows
        results.each { row ->
            def values = row.values().collect { it?.toString()?.replaceAll(',', ';') ?: '' }
            csv.append(values.join(',')).append('\n')
        }
        
        steps.writeFile file: filename, text: csv.toString()
        steps.echo "✅ Archivo ${filename} creado"
    }
    
    /**
     * Cierra la conexión a la base de datos
     * @param sql Conexión SQL
     */
    def closeConnection(def sql) {
        try {
            sql?.close()
            steps.echo "🔌 Conexión cerrada"
        } catch (Exception e) {
            steps.echo "⚠️ Error al cerrar conexión: ${e.message}"
        }
    }
    
    /**
     * Ejecuta un backup de una tabla
     * @param sql Conexión SQL
     * @param tableName Nombre de la tabla
     * @param backupFile Archivo de backup
     */
    def backupTable(def sql, String tableName, String backupFile) {
        steps.echo "💾 Haciendo backup de tabla: ${tableName}"
        
        def query = "SELECT * FROM ${tableName}"
        def results = executeQuery(sql, query)
        exportToCSV(results, backupFile)
        
        steps.echo "✅ Backup completado: ${backupFile}"
    }
    
    /**
     * Obtiene configuración de BD desde archivo
     * @param configFile Ruta al archivo de configuración
     * @param environment Entorno (dev, staging, prod)
     * @return Map con configuración
     */
    def getDbConfig(String configFile, String environment) {
        steps.echo "📋 Cargando config de BD para: ${environment}"
        
        def configText = steps.readFile(configFile)
        def config = new groovy.json.JsonSlurper().parseText(configText)
        
        return config.databases[environment]
    }
}

