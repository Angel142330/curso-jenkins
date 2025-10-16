#!/usr/bin/env groovy

/**
 * Ejecuta pruebas de regresión
 * 
 * @param projectPath Ruta al proyecto
 * @param exceptions Lista de tests excepcionados (se marcan como OK)
 */
def call(String projectPath = '.', List exceptions = []) {
    echo "🔄 Ejecutando pruebas de regresión en: ${projectPath}"
    
    dir(projectPath) {
        try {
            // Ejecutar pruebas de regresión
            sh '''
                echo "Ejecutando pruebas de regresión..."
                # mvn verify -Pregression
                # o tu comando específico
            '''
            
            echo "✅ Pruebas de regresión: PASS"
            
        } catch (Exception e) {
            // Verificar si el test que falló está excepcionado
            def testName = extractFailedTestName(e.message)
            
            if (exceptions.contains(testName)) {
                echo "⚠️ Test '${testName}' falló pero está EXCEPCIONADO → marcando como OK"
                // Continuar sin error
            } else {
                echo "❌ Pruebas de regresión: FAIL - ${e.message}"
                throw e
            }
        }
    }
}

// Función auxiliar para extraer nombre del test que falló
def extractFailedTestName(String errorMessage) {
    // Extraer nombre del test del mensaje de error
    // Esto depende del formato de tu sistema de tests
    def matcher = (errorMessage =~ /Test (\w+)/)
    return matcher ? matcher[0][1] : 'unknown'
}

