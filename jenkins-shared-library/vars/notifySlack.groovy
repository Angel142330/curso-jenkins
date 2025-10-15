#!/usr/bin/env groovy

/**
 * Envía notificaciones a Slack
 * 
 * @param message Mensaje a enviar
 * @param status Estado del build (SUCCESS, FAILURE, WARNING)
 * @param channel Canal de Slack (opcional)
 * 
 * Ejemplo:
 *   notifySlack('Build completado', 'SUCCESS')
 */
def call(String message, String status = 'INFO', String channel = '#jenkins') {
    def color
    def emoji
    
    switch(status) {
        case 'SUCCESS':
            color = 'good'
            emoji = '✅'
            break
        case 'FAILURE':
            color = 'danger'
            emoji = '❌'
            break
        case 'WARNING':
            color = 'warning'
            emoji = '⚠️'
            break
        default:
            color = '#439FE0'
            emoji = 'ℹ️'
    }
    
    echo "${emoji} ${status}: ${message}"
    
    // Descomentar cuando configures Slack
    // slackSend(
    //     color: color,
    //     message: "${emoji} *${status}*: ${message}\nJob: ${env.JOB_NAME}\nBuild: ${env.BUILD_NUMBER}",
    //     channel: channel
    // )
}

