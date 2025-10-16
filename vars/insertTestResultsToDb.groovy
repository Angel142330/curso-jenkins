#!/usr/bin/env groovy

/**
 * Inserta resultados de tests en la base de datos
 * 
 * @param config Map con configuración:
 *   - projectName: Nombre del proyecto
 *   - branch: Rama
 *   - testsPassed: Si los tests pasaron
 *   - buildNumber: Número de build
 */
def call(Map config) {
    echo "💾 Insertando resultados en la base de datos..."
    
    def projectName = config.projectName ?: 'unknown'
    def branch = config.branch ?: 'unknown'
    def testsPassed = config.testsPassed ? 'PASS' : 'FAIL'
    def buildNumber = config.buildNumber ?: env.BUILD_NUMBER
    
    try {
        // Usar la función sqlExecute de la Shared Library
        sqlExecute(
            driver: 'org.postgresql.Driver',
            url: 'jdbc:postgresql://localhost:5432/jenkins_db',
            user: credentials('db-user'),
            password: credentials('db-password'),
            command: """
                INSERT INTO test_results 
                (project_name, branch, test_result, build_number, executed_at)
                VALUES 
                ('${projectName}', '${branch}', '${testsPassed}', ${buildNumber}, NOW())
            """
        )
        
        echo "✅ Resultados insertados en BD"
        
    } catch (Exception e) {
        echo "⚠️ Error al insertar en BD: ${e.message}"
        // No fallar el build por esto
    }
}

