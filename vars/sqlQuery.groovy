#!/usr/bin/env groovy

/**
 * Ejecuta una query SQL SELECT y retorna resultados
 * 
 * @param config Map con configuración:
 *   - driver: Driver JDBC (ej: 'org.postgresql.Driver')
 *   - url: URL de conexión (ej: 'jdbc:postgresql://localhost:5432/mydb')
 *   - user: Usuario
 *   - password: Password
 *   - query: Query SQL a ejecutar
 *   - exportTo: (Opcional) Archivo CSV para exportar resultados
 * 
 * @return Lista de resultados
 * 
 * Ejemplo:
 *   def results = sqlQuery(
 *       driver: 'org.postgresql.Driver',
 *       url: 'jdbc:postgresql://localhost:5432/mydb',
 *       user: 'admin',
 *       password: 'password',
 *       query: 'SELECT * FROM users WHERE active = true',
 *       exportTo: 'users.csv'
 *   )
 */
def call(Map config) {
    def dbHelper = new org.miempresa.DatabaseHelper(this)
    def sql = null
    def results = []
    
    try {
        // Crear conexión
        sql = dbHelper.createConnection([
            driver: config.driver,
            url: config.url,
            user: config.user,
            password: config.password
        ])
        
        // Ejecutar query
        results = dbHelper.executeQuery(sql, config.query)
        
        // Exportar si se especifica
        if (config.exportTo) {
            dbHelper.exportToCSV(results, config.exportTo)
        }
        
        return results
    } finally {
        // Cerrar conexión siempre
        dbHelper.closeConnection(sql)
    }
}

