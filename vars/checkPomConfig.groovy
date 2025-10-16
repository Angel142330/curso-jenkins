#!/usr/bin/env groovy

/**
 * Lee configuración del pom.xml para saber si ejecutar tests en develop
 * 
 * @param projectPath Ruta al proyecto
 * @return Map con configuración: [runTests: boolean, insertDb: boolean]
 */
def call(String projectPath = '.') {
    echo "📋 Leyendo configuración de pom.xml..."
    
    def config = [
        runTests: false,
        insertDb: false
    ]
    
    dir(projectPath) {
        // Leer pom.xml
        def pomContent = readFile('pom.xml')
        
        // Buscar property 'develop.runTests'
        if (pomContent.contains('<develop.runTests>true</develop.runTests>')) {
            config.runTests = true
            config.insertDb = true
            echo "✅ develop.runTests = true (ejecutar tests e insertar en BD)"
        } else if (pomContent.contains('<develop.runTests>false</develop.runTests>')) {
            config.runTests = false
            config.insertDb = false
            echo "⚠️ develop.runTests = false (solo compilar, sin tests)"
        } else {
            // Si no existe la property, asumir false por defecto
            echo "ℹ️ develop.runTests no encontrado, asumiendo false"
        }
    }
    
    return config
}

