#!/usr/bin/env groovy

/**
 * Pipeline estándar completo con build, test y deploy
 * 
 * @param config Map con configuración:
 *   - projectPath: Ruta al proyecto
 *   - projectType: Tipo de proyecto ('maven', 'python', 'docker')
 *   - deployConfig: Configuración de deploy (opcional)
 * 
 * Ejemplo:
 *   standardPipeline(
 *       projectPath: 'mi-proyecto',
 *       projectType: 'maven',
 *       deployConfig: [
 *           appName: 'mi-app',
 *           environment: 'production'
 *       ]
 *   )
 */
def call(Map config) {
    pipeline {
        agent any
        
        stages {
            stage('Build') {
                steps {
                    script {
                        switch(config.projectType) {
                            case 'maven':
                                buildMaven(config.projectPath)
                                break
                            case 'python':
                                echo "Build Python project: ${config.projectPath}"
                                break
                            case 'docker':
                                dockerBuild(config.projectPath)
                                break
                            default:
                                error("Tipo de proyecto no soportado: ${config.projectType}")
                        }
                    }
                }
            }
            
            stage('Test') {
                steps {
                    script {
                        switch(config.projectType) {
                            case 'maven':
                                testMaven(config.projectPath)
                                break
                            case 'python':
                                runPytest(config.projectPath)
                                break
                            default:
                                echo "No tests configured for ${config.projectType}"
                        }
                    }
                }
            }
            
            stage('Deploy') {
                when {
                    expression { config.deployConfig != null }
                }
                steps {
                    script {
                        deployApp(config.deployConfig)
                    }
                }
            }
        }
        
        post {
            success {
                notifySlack("Pipeline ${config.projectPath} completado!", 'SUCCESS')
            }
            failure {
                notifySlack("Pipeline ${config.projectPath} falló", 'FAILURE')
            }
        }
    }
}

