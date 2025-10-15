package org.miempresa

/**
 * Clase de utilidades reutilizables
 */
class Utils implements Serializable {
    
    def steps
    
    Utils(steps) {
        this.steps = steps
    }
    
    /**
     * Obtiene la versión del proyecto desde un archivo
     */
    def getProjectVersion(String versionFile = 'VERSION') {
        return steps.sh(script: "cat ${versionFile}", returnStdout: true).trim()
    }
    
    /**
     * Limpia workspace selectivamente
     */
    def cleanWorkspace(List excludePatterns = []) {
        steps.echo "🧹 Limpiando workspace..."
        
        excludePatterns.each { pattern ->
            steps.sh "find . -type f -not -path '${pattern}' -delete || true"
        }
        
        steps.echo "✅ Workspace limpio"
    }
    
    /**
     * Verifica si una rama es de release
     */
    def isReleaseBranch(String branch) {
        return branch.startsWith('release/') || branch.startsWith('hotfix/')
    }
    
    /**
     * Obtiene el timestamp actual en formato específico
     */
    def getTimestamp(String format = 'yyyyMMdd-HHmmss') {
        return new Date().format(format)
    }
    
    /**
     * Parsea un archivo de configuración YAML o JSON
     */
    def parseConfigFile(String filePath) {
        def fileContent = steps.readFile(filePath)
        
        if (filePath.endsWith('.json')) {
            return new groovy.json.JsonSlurper().parseText(fileContent)
        } else if (filePath.endsWith('.yml') || filePath.endsWith('.yaml')) {
            // Requiere plugin Pipeline Utility Steps
            return steps.readYaml(text: fileContent)
        }
        
        return null
    }
}

