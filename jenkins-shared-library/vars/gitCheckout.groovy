#!/usr/bin/env groovy

/**
 * Hace checkout de un repositorio Git
 * 
 * @param config Map con configuración:
 *   - url: URL del repositorio
 *   - branch: Rama a descargar (por defecto 'main')
 *   - credentials: ID de credenciales (opcional)
 * 
 * Ejemplo:
 *   gitCheckout(
 *       url: 'https://github.com/usuario/repo.git',
 *       branch: 'develop',
 *       credentials: 'github-credentials'
 *   )
 */
def call(Map config) {
    def url = config.url
    def branch = config.branch ?: 'main'
    def credentials = config.credentials ?: ''
    
    echo "📥 Descargando repositorio: ${url} (rama: ${branch})"
    
    if (credentials) {
        checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[
                url: url,
                credentialsId: credentials
            ]]
        ])
    } else {
        checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[url: url]]
        ])
    }
    
    echo "✅ Repositorio descargado"
}

