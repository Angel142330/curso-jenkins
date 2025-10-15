# 📚 Ejemplos de Uso - Jenkins Shared Library

Colección de ejemplos prácticos para usar la Shared Library en diferentes escenarios.

## 🚀 Ejemplos Básicos

### Ejemplo 1: Pipeline Maven Simple

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                buildMaven('mi-proyecto')
            }
        }
        
        stage('Test') {
            steps {
                testMaven('mi-proyecto')
            }
        }
    }
    
    post {
        success {
            notifySlack('Build exitoso!', 'SUCCESS')
        }
        failure {
            notifySlack('Build falló', 'FAILURE')
        }
    }
}
```

### Ejemplo 2: Pipeline Python con Pytest

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Test') {
            steps {
                runPytest('proyecto_pytest', 'venv', 'reports')
            }
        }
    }
}
```

### Ejemplo 3: Build Docker

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Build Docker Image') {
            steps {
                dockerBuild('mi-app', "${env.BUILD_NUMBER}")
            }
        }
        
        stage('Push to Registry') {
            steps {
                sh 'docker push mi-app:${BUILD_NUMBER}'
            }
        }
    }
}
```

## 🔄 Ejemplos Avanzados

### Ejemplo 4: Pipeline Multibranch con Deploy Condicional

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                buildMaven('mi-proyecto')
            }
        }
        
        stage('Test') {
            steps {
                testMaven('mi-proyecto')
            }
        }
        
        stage('Deploy Dev') {
            when {
                branch 'develop'
            }
            steps {
                deployApp(
                    appName: 'mi-app',
                    environment: 'dev',
                    deployCommand: 'kubectl apply -f k8s/dev/'
                )
            }
        }
        
        stage('Deploy Staging') {
            when {
                branch 'release/*'
            }
            steps {
                deployApp(
                    appName: 'mi-app',
                    environment: 'staging',
                    deployCommand: 'kubectl apply -f k8s/staging/'
                )
            }
        }
        
        stage('Deploy Production') {
            when {
                branch 'main'
            }
            steps {
                deployApp(
                    appName: 'mi-app',
                    environment: 'production',
                    deployCommand: 'kubectl apply -f k8s/production/'
                )
            }
        }
    }
    
    post {
        success {
            notifySlack("Deployment ${env.BRANCH_NAME} exitoso!", 'SUCCESS')
        }
        failure {
            notifySlack("Deployment ${env.BRANCH_NAME} falló", 'FAILURE')
        }
    }
}
```

### Ejemplo 5: Pipeline con Múltiples Microservicios

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Build Backend') {
            steps {
                buildMaven('backend-service')
            }
        }
        
        stage('Build Frontend') {
            steps {
                script {
                    dir('frontend') {
                        sh 'npm install'
                        sh 'npm run build'
                    }
                }
            }
        }
        
        stage('Test Backend') {
            steps {
                testMaven('backend-service')
            }
        }
        
        stage('Test Frontend') {
            steps {
                script {
                    dir('frontend') {
                        sh 'npm test'
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            parallel {
                stage('Backend Image') {
                    steps {
                        dockerBuild('backend', "${env.BUILD_NUMBER}", 'backend/Dockerfile', 'backend')
                    }
                }
                stage('Frontend Image') {
                    steps {
                        dockerBuild('frontend', "${env.BUILD_NUMBER}", 'frontend/Dockerfile', 'frontend')
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                deployApp(
                    appName: 'microservices',
                    environment: 'production',
                    deployCommand: 'kubectl apply -f k8s/'
                )
            }
        }
    }
}
```

### Ejemplo 6: Pipeline con Clase Utils

```groovy
@Library('jenkins-shared-library') _
import org.miempresa.Utils

def utils = new Utils(this)

pipeline {
    agent any
    
    environment {
        PROJECT_VERSION = ''
        BUILD_TIMESTAMP = ''
    }
    
    stages {
        stage('Prepare') {
            steps {
                script {
                    // Obtener versión del proyecto
                    PROJECT_VERSION = utils.getProjectVersion('VERSION')
                    BUILD_TIMESTAMP = utils.getTimestamp()
                    
                    echo "Building version: ${PROJECT_VERSION}"
                    echo "Build timestamp: ${BUILD_TIMESTAMP}"
                    
                    // Verificar si es rama de release
                    if (utils.isReleaseBranch(env.BRANCH_NAME)) {
                        echo "🚀 This is a release branch!"
                    }
                }
            }
        }
        
        stage('Build') {
            steps {
                buildMaven('mi-proyecto')
            }
        }
        
        stage('Tag Build') {
            steps {
                script {
                    def tag = "${PROJECT_VERSION}-${BUILD_TIMESTAMP}"
                    dockerBuild('mi-app', tag)
                }
            }
        }
    }
}
```

### Ejemplo 7: Pipeline con Checkout desde Múltiples Repositorios

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Checkout Config Repo') {
            steps {
                script {
                    gitCheckout(
                        url: 'https://github.com/usuario/config-repo.git',
                        branch: 'main',
                        credentials: 'github-token'
                    )
                }
            }
        }
        
        stage('Build') {
            steps {
                buildMaven('mi-proyecto')
            }
        }
    }
}
```

### Ejemplo 8: Pipeline con Notificaciones Personalizadas

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                script {
                    notifySlack('Iniciando build...', 'INFO')
                    buildMaven('mi-proyecto')
                    notifySlack('Build completado', 'SUCCESS')
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    notifySlack('Ejecutando tests...', 'INFO')
                    testMaven('mi-proyecto')
                    notifySlack('Tests pasaron', 'SUCCESS')
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    notifySlack('Iniciando deploy...', 'WARNING')
                    deployApp(
                        appName: 'mi-app',
                        environment: 'production',
                        deployCommand: 'kubectl apply -f k8s/'
                    )
                    notifySlack('Deploy completado exitosamente!', 'SUCCESS')
                }
            }
        }
    }
    
    post {
        failure {
            notifySlack("❌ Pipeline falló en stage: ${env.STAGE_NAME}", 'FAILURE')
        }
    }
}
```

### Ejemplo 9: Pipeline con Todas las Funciones

```groovy
@Library('jenkins-shared-library') _
import org.miempresa.Utils

def utils = new Utils(this)

pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'docker.io'
        APP_NAME = 'mi-app'
        VERSION = ''
    }
    
    stages {
        stage('Prepare') {
            steps {
                script {
                    VERSION = utils.getTimestamp('yyyyMMdd-HHmmss')
                    echo "Building version: ${VERSION}"
                }
            }
        }
        
        stage('Compile') {
            steps {
                compileMaven('mi-proyecto')
            }
        }
        
        stage('Build') {
            steps {
                buildMaven('mi-proyecto')
            }
        }
        
        stage('Test') {
            steps {
                testMaven('mi-proyecto')
            }
        }
        
        stage('Docker Build') {
            steps {
                dockerBuild(APP_NAME, VERSION)
            }
        }
        
        stage('Docker Push') {
            steps {
                sh """
                    docker tag ${APP_NAME}:${VERSION} ${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}
                    docker push ${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}
                """
            }
        }
        
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                deployApp(
                    appName: APP_NAME,
                    environment: 'production',
                    deployCommand: """
                        kubectl set image deployment/${APP_NAME} \\
                        ${APP_NAME}=${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}
                    """
                )
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline finalizado'
        }
        success {
            notifySlack("✅ Build ${VERSION} exitoso!", 'SUCCESS')
        }
        failure {
            notifySlack("❌ Build ${VERSION} falló", 'FAILURE')
        }
    }
}
```

## 🎯 Tips y Buenas Prácticas

### 1. Usar Variables de Entorno

```groovy
environment {
    PROJECT_PATH = 'mi-proyecto'
}

stages {
    stage('Build') {
        steps {
            buildMaven(env.PROJECT_PATH)
        }
    }
}
```

### 2. Manejo de Errores

```groovy
stage('Build') {
    steps {
        script {
            try {
                buildMaven('mi-proyecto')
            } catch (Exception e) {
                notifySlack("Error en build: ${e.message}", 'FAILURE')
                throw e
            }
        }
    }
}
```

### 3. Builds Paralelos

```groovy
stage('Parallel Builds') {
    parallel {
        stage('Backend') {
            steps {
                buildMaven('backend')
            }
        }
        stage('Frontend') {
            steps {
                buildMaven('frontend')
            }
        }
    }
}
```

### 4. Parámetros Dinámicos

```groovy
parameters {
    choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'production'])
    booleanParam(name: 'SKIP_TESTS', defaultValue: false)
}

stages {
    stage('Test') {
        when {
            expression { !params.SKIP_TESTS }
        }
        steps {
            testMaven('mi-proyecto')
        }
    }
    
    stage('Deploy') {
        steps {
            deployApp(
                appName: 'mi-app',
                environment: params.ENVIRONMENT,
                deployCommand: "deploy.sh ${params.ENVIRONMENT}"
            )
        }
    }
}
```

## 📝 Plantillas Reutilizables

### Template: Backend Service

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build') { steps { buildMaven('backend') } }
        stage('Test') { steps { testMaven('backend') } }
        stage('Deploy Dev') {
            when { branch 'develop' }
            steps {
                deployApp(appName: 'backend', environment: 'dev', 
                          deployCommand: 'kubectl apply -f k8s/dev/')
            }
        }
    }
    post {
        success { notifySlack('Backend build exitoso!', 'SUCCESS') }
        failure { notifySlack('Backend build falló', 'FAILURE') }
    }
}
```

### Template: Frontend Service

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build Docker') { 
            steps { dockerBuild('frontend', "${env.BUILD_NUMBER}") } 
        }
        stage('Deploy') {
            steps {
                deployApp(appName: 'frontend', environment: 'production',
                          deployCommand: 'kubectl apply -f k8s/frontend/')
            }
        }
    }
}
```

---

¿Necesitas más ejemplos? Consulta el [README.md](README.md) o la [Guía de Configuración](GUIA_CONFIGURACION.md).

