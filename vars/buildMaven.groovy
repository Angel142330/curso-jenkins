#!/usr/bin/env groovy

/**
 * Ejecuta 'mvn clean install' en un proyecto Maven
 * 
 * @param projectPath Ruta al proyecto Maven (por defecto '.')
 * 
 * Ejemplo:
 *   buildMaven('mi-proyecto')
 */
def call(String projectPath = '.') {
    echo "🔨 Construyendo proyecto Maven en: ${projectPath}"
    dir(projectPath) {
        sh 'mvn clean install'
    }
    echo "✅ Build completado exitosamente"
}

