#!/usr/bin/env groovy

/**
 * Valida código con Kiuwan
 * 
 * @param projectPath Ruta al proyecto
 * @param deleteAfter Si es true, borra los resultados después (para feature/*)
 * @return true si la validación pasa, false si no
 */
def call(String projectPath = '.', boolean deleteAfter = false) {
    echo "🔍 Validando con Kiuwan en: ${projectPath}"
    
    def kiuwanPassed = false
    
    dir(projectPath) {
        try {
            // Ejecutar análisis de Kiuwan
            sh '''
                # Simular validación Kiuwan (reemplaza con tu comando real)
                echo "Ejecutando análisis Kiuwan..."
                
                # Comando real sería algo como:
                # kiuwan-local-analyzer.sh -n "mi-proyecto" -s . -l /path/to/kiuwan/
                
                # Por ahora simulamos
                echo "✅ Análisis Kiuwan completado"
            '''
            
            kiuwanPassed = true
            echo "✅ Validación Kiuwan: PASS"
            
        } catch (Exception e) {
            echo "❌ Validación Kiuwan: FAIL - ${e.message}"
            kiuwanPassed = false
            throw e
        } finally {
            // Si es rama feature/*, borrar resultados
            if (deleteAfter) {
                echo "🗑️ Borrando resultados de Kiuwan (rama feature/*)"
                sh 'rm -rf .kiuwan* || true'
            }
        }
    }
    
    return kiuwanPassed
}

