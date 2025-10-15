#!/usr/bin/env groovy

/**
 * Compila un proyecto Maven con 'mvn compile'
 * 
 * @param projectPath Ruta al proyecto Maven (por defecto '.')
 * 
 * Ejemplo:
 *   compileMaven('mi-proyecto')
 */
def call(String projectPath = '.') {
    echo "⚙️ Compilando proyecto Maven en: ${projectPath}"
    dir(projectPath) {
        sh 'mvn compile'
    }
    echo "✅ Compilación exitosa"
}

