#!/usr/bin/env groovy

/**
 * Construye una imagen Docker
 * 
 * @param imageName Nombre de la imagen
 * @param tag Tag de la imagen (por defecto usa BUILD_NUMBER)
 * @param dockerfile Ruta al Dockerfile (por defecto './Dockerfile')
 * @param context Contexto de build (por defecto '.')
 * 
 * Ejemplo:
 *   dockerBuild('mi-app', 'latest')
 *   dockerBuild('mi-app', env.BUILD_NUMBER, './docker/Dockerfile', '.')
 */
def call(String imageName, String tag = "${env.BUILD_NUMBER}", String dockerfile = 'Dockerfile', String context = '.') {
    echo "🐳 Construyendo imagen Docker: ${imageName}:${tag}"
    
    sh """
        docker build -t ${imageName}:${tag} -f ${dockerfile} ${context}
        docker tag ${imageName}:${tag} ${imageName}:latest
    """
    
    echo "✅ Imagen construida: ${imageName}:${tag}"
}

