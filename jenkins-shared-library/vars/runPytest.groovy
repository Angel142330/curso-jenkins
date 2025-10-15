#!/usr/bin/env groovy

/**
 * Ejecuta tests de Python con pytest
 * 
 * @param projectPath Ruta al proyecto Python (por defecto '.')
 * @param venvPath Ruta al entorno virtual (opcional)
 * @param reportPath Ruta para guardar reportes (opcional)
 * 
 * Ejemplo:
 *   runPytest('proyecto_pytest', 'venv', 'reports')
 */
def call(String projectPath = '.', String venvPath = null, String reportPath = null) {
    echo "🐍 Ejecutando pytest en: ${projectPath}"
    
    dir(projectPath) {
        def activateCmd = ''
        
        if (venvPath) {
            // Activar entorno virtual
            if (isUnix()) {
                activateCmd = "source ${venvPath}/bin/activate && "
            } else {
                activateCmd = "${venvPath}\\Scripts\\activate && "
            }
        }
        
        def pytestCmd = "${activateCmd}pytest"
        
        if (reportPath) {
            pytestCmd += " --html=${reportPath}/test-results.html --junitxml=${reportPath}/test-results.xml"
        }
        
        if (isUnix()) {
            sh pytestCmd
        } else {
            bat pytestCmd
        }
    }
    
    echo "✅ Tests de Python completados"
}

