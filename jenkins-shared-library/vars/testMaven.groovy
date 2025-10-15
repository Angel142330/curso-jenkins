#!/usr/bin/env groovy

/**
 * Ejecuta tests de Maven con 'mvn test'
 * 
 * @param projectPath Ruta al proyecto Maven (por defecto '.')
 * 
 * Ejemplo:
 *   testMaven('mi-proyecto')
 */
def call(String projectPath = '.') {
    echo "🧪 Ejecutando tests Maven en: ${projectPath}"
    dir(projectPath) {
        sh 'mvn test'
    }
    echo "✅ Tests completados"
}

