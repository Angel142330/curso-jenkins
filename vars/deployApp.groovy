#!/usr/bin/env groovy

/**
 * Despliega una aplicación con confirmación opcional para producción
 * 
 * @param config Map con configuración:
 *   - appName: Nombre de la aplicación
 *   - environment: Entorno (dev, staging, production)
 *   - deployCommand: Comando de despliegue a ejecutar
 * 
 * Ejemplo:
 *   deployApp(
 *       appName: 'mi-app',
 *       environment: 'production',
 *       deployCommand: 'kubectl apply -f k8s/'
 *   )
 */
def call(Map config) {
    def appName = config.appName ?: 'app'
    def environment = config.environment ?: 'dev'
    def deployCommand = config.deployCommand ?: 'echo "No deploy command specified"'
    
    echo "🚀 Desplegando ${appName} en ${environment}"
    
    // Pedir confirmación para producción
    if (environment == 'production') {
        input message: "⚠️ ¿Confirmar despliegue de ${appName} a PRODUCCIÓN?", 
              ok: 'Desplegar'
    }
    
    // Ejecutar despliegue
    sh deployCommand
    
    echo "✅ Despliegue completado en ${environment}"
}

